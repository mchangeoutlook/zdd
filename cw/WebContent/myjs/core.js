var pedataurl = "http://getzdd.com/pei";
var tendataurl = "http://getzdd.com/teni";
var commonurl = "http://getzdd.com/ci";
var msgurl = "http://getzdd.com/cm";
//var pedataurl = "http://localhost:8080/pei";
//var tendataurl = "http://localhost:8080/teni";
//var commonurl = "http://localhost:8080/ci";
//var msgurl = "http://localhost:8080/cm";
			
var notice = new Audio("/cw/audio/play.aac");
var textnotice = new Audio("/cw/audio/text.aac");
var vibrateSupport = "vibrate" in navigator  
if (vibrateSupport) { 
    navigator.vibrate = navigator.vibrate || navigator.webkitVibrate || navigator.mozVibrate || navigator.msVibrate;  
}  
function post(url, datajson, yescallback,clickid,clickfunction){
	if (clickid){
		$("#"+clickid).unbind("click");
		$("#"+clickid).css("background","#666");
	}
	if ($("#msg").html()=="网络异常，请检查连接后重试"){
		$("#msg").css("color","#bbb");
    		if ($("#msg").attr("prev")){
			$("#msg").html($("#msg").attr("prev"));
		} else {
			$("#msg").html("");
		}
	}
	var ajaxpost = $.ajax({
		url:url,  
	　　	timeout : 20000, 
	　　	type : 'post',  
	　　	data :datajson, 
	　　	dataType:'json',
	　　	success:function(data){ 
			if (data.status=="yes"){
				if (yescallback){
					yescallback(data);
				}
			} else {
				if (data.reason=="expire"){
					if (window.location.href.indexOf("0.htm")==-1){
						window.location.href="0.htm";
					}
					return;
				}
				if (data.reason=="duplicatenick"){
					$("#msg").html("有人抢了你的昵称，请换一个吧");
					if (clickid&&clickfunction){
						$("#"+clickid).one("click",clickfunction);
					}
					return;
				}
				$("#msg").html("请刷新页面("+data.reason+")");
			}
	　　	},
	　　	complete : function(XMLHttpRequest,status){
			$("#"+clickid).css("background","#008B8B");
			if (status!="success"){
				ajaxpost.abort();
				if ($("#msg").html()!="网络异常，请检查连接后重试"){
					$("#msg").attr("prev",$("#msg").html());
					$("#msg").css("color","#DC143C");
	               	$("#msg").html("网络异常，请检查连接后重试");
               	}
				if (clickid&&clickfunction){
					$("#"+clickid).one("click",clickfunction);
				}
			}
	　　	}
	});
}

function longpollcheck(){
	var datajson = {};
	var longpollcheckurl = "";
	if (window.location.href.indexOf("pew")!=-1&&localStorage.getItem("pejudgeid")!=null&&localStorage.getItem("peplayerid")!=null){
		datajson.judgeid=localStorage.getItem("pejudgeid");
		datajson.playerid=localStorage.getItem("peplayerid");
		longpollcheckurl = pedataurl+"/lpch";
	} else if (window.location.href.indexOf("tenw")!=-1&&localStorage.getItem("game10judgeid")!=null&&localStorage.getItem("game10playerid")!=null){
		datajson.judgeid=localStorage.getItem("game10judgeid");
		datajson.playerid=localStorage.getItem("game10playerid");
		longpollcheckurl = tendataurl+"/lpch";
	}
	if (longpollcheckurl==""){
		setTimeout(longpollcheck,3000);
		return;
	} 
	sessionStorage.setItem("startpollmsg","startpollmsg");
	if ($("#msg").html()=="网络异常，请检查网络连接"){
		$("#msg").css("color","#bbb");
    		if ($("#msg").attr("prev")){
			$("#msg").html($("#msg").attr("prev"));
		} else {
			$("#msg").html("");
		}
	}
	if (sessionStorage.getItem("notifytime")==null){
		sessionStorage.setItem("notifytime","");
	}
	datajson.notifytime=sessionStorage.getItem("notifytime");
	$.ajax({
		url:longpollcheckurl,  
	　　	timeout : 90000, 
	　　	type : 'post',  
	　　	data :datajson, 
	　　	dataType:'json',
	　　	success:function(data){ 
			if (data.status=="yes"){
				sessionStorage.setItem("notifytime",data.notifytime);
				lpchcallback(data);
			} else {
				if (data.reason=="expire"){
					if (window.location.href.indexOf("0.htm")==-1){
						window.location.href="0.htm";
					}
				}
			}
	　　	},
	　　	complete : function(XMLHttpRequest,status){
			if (XMLHttpRequest.status!=200){
				if ($("#msg").html()!="网络异常，请检查网络连接"){
					$("#msg").attr("prev",$("#msg").html());
					$("#msg").css("color","#DC143C");
	               	$("#msg").html("网络异常，请检查网络连接");
               	}
               	setTimeout(longpollcheck, 3000);
            } else {
				longpollcheck();
			}
		}
	});
}

var index = 0;
function longpollmsg(){
	if (sessionStorage.getItem("startpollmsg")==null){
		setTimeout(longpollmsg,3000);
		return;
	}
	$("#talk").show();
  	$("#talkto").show();
  	var receiverid = "";
	if (window.location.href.indexOf("pew/1")!=-1&&localStorage.getItem("peplayerid")!=null){
		receiverid = localStorage.getItem("peplayerid");
	} else if (window.location.href.indexOf("tenw/1")!=-1&&localStorage.getItem("game10playerid")!=null){
		receiverid = localStorage.getItem("game10playerid");
	}
	$.ajax({
		url:msgurl+'/lpmsg',  
	　　	timeout : 90000, 
	　　	type : 'post',  
	　　	data :{
	　　		receiverid:receiverid
		}, 
	　　	dataType:'json',
	　　	success:function(data){ 
　　			if (data.status=="yes"){
				if (data.msgs.length!=0&&$("#notice").attr("canplay")=="yes"){
					textnotice.play();
				}
	　　			var height=[192,250];
	　　			for (var i=0;i<data.msgs.length;i++){
	　　				var left = $(window).width();
	　　				var style="position:fixed;top:"+height[index%2]+"px;left:"+left+"px;color:#228B22;white-space:nowrap;font-size:20px;";
　　					$("body").append($("<font id='msg"+index+"' style='"+style+"'>"+data.msgs[i].sendernick+" 说："+data.msgs[i].msg+"</font>"));
			　　		index=index+1;
	　　			}
			}
		},
	　　	complete : function(XMLHttpRequest,status){
			longpollmsg();
	　　	}
	});			
}

function maintain(){
	var target = "";
	if (window.location.href.indexOf("pew")!=-1){
		target = "pemaintain";
	} else {
		target = "game10maintain";
	}
	
	post(commonurl+'/maintain',{target:target},function(data){
		if ($("#maintain").is(":visible")){
			$("#maintain").remove();
		}
		if (data.maintain == 1){
			$("body").append($("<div id='maintain' style='display:none;position:fixed;width:100%;left:0px;right:0px;height:21px;top:0px;z-index:10000;background:#DC143C;border:0px;text-align:center;font-size:15px;'></div>"));
			$("#maintain").html("将于"+data.date+" 22:00停机检修，我还会回来的");
			$("#maintain").fadeIn(1000);
		}
		setTimeout(maintain,30*60000);
	});
}

function animatemsg(msgindex){
	if ($("#msg"+msgindex).length){
		$("#msg"+msgindex).animate({left:"-430px"},9000,"linear",function(){
			$(this).remove();
			animatemsg(msgindex+2);
		});
	} else {
		setTimeout(function(){animatemsg(msgindex)},3000);
	}
}

$(document).ready(function(){
	sessionStorage.removeItem("startpollmsg");
	longpollmsg();	
	animatemsg(0);
	setTimeout(function(){animatemsg(1)},500);
	maintain();			
});