var urls={
		"ten":"http://getzdd.com/teni",
		"pe":"http://getzdd.com/pei",
		"common":"http://getzdd.com/ci",
		"msg":"http://getzdd.com/cm"
};
/*
var urls={
		"ten":"http://localhost:8080/teni",
		"pe":"http://localhost:8080/pei",
		"common":"http://localhost:8080/ci",
		"msg":"http://localhost:8080/cm"
};
*/
var audios={
		"uturn":[
			"/cw/audio/notice0.m4a",
			"/cw/audio/notice1.m4a",
			"/cw/audio/notice2.m4a",
			"/cw/audio/notice3.m4a",
			"/cw/audio/notice4.m4a"
		],
		"msg":"/cw/audio/msg.m4a"
};

var noticeuturn = document.createElement("audio");
var noticemsg = document.createElement("audio");
var numofaudios = 0;
var vibrateSupport = "vibrate" in navigator  
if (vibrateSupport) { 
    navigator.vibrate = navigator.vibrate || navigator.webkitVibrate || navigator.mozVibrate || navigator.msVibrate;  
}  

var game="";
var meindex=-1;
$(document).ready(function(){
	if (window.location.href.indexOf("/pew/")!=-1){
		game="pe";
	} else if (window.location.href.indexOf("/tenw/")!=-1){
		game="ten";
	}
	$("body").append(
		"<a href='/ad/0.htm' style='color:#bbb;text-decoration:none;' target='blank'>"+
			"<div style='border-radius:15px;border:1px #000 solid;background:#008B8B;font:15px Arial;width:28px;height:28px;line-height:28px;color:#000;position:absolute;top:0px;left:0px;text-align:center;'>"+
				"AD"+
			"</div>"+
		"</a>"
	);
	
	$("body").append(
		"<div id='zhcopyright' style='display:none;font-size:9px;position:fixed;left:0px;right:0px;bottom:0px;width:100%;text-align:right;color:#999;'>© 02/09/2018-2090 getzdd@outlook.com 沪ICP备18004230号</div>"+
		"<div id='encopyright' style='display:none;font-size:9px;position:fixed;left:0px;right:0px;bottom:0px;width:100%;text-align:right;color:#999;'>© 02/09/2018-2090 getzdd@outlook.com</div>"
	);
	
	if (game!=""){
		$("body").append(
			"<div id='rule' style='z-index:20000;background:#000;display:none;font:15px Arial;position:absolute;top:30px;left:0px;right:0px;bottom:0px;overflow-x:hidden;overflow-y:scroll;margin-bottom:15px;'>"+
			"</div>"
		);
		$("#rule").append(
			"<div id='invitehint' style='display:none;width:100%;line-height:30px;'></div>"+
			"<div id='invite' style='display:none;margin:auto;width:300px;height:300px;'></div>"
		);
		var qrcode = new QRCode("invite", {
		    text: window.location.href,
		    width: 300,
		    height:300,
		    colorDark : "#000000",
		    colorLight : "#bbb",
		    correctLevel : QRCode.CorrectLevel.H
		});
		$("#rule").append(
			"<div style='position:relative;width:300px;margin:auto;margin-top:10px;'>"+
				"<div id='zhrule' style='position:absolute;cursor:pointer;width:148px;border:1px #000 solid;height:48px;line-height:48px;top:0px;left:0px;background:#000;z-index:2;'>规则</div>"+
				"<div id='enrule' style='position:absolute;cursor:pointer;width:148px;border:1px #000 solid;height:48px;line-height:50px;top:0px;left:150px;background:#000;z-index:2;'>Rule</div>"+
				"<div id='zhruledesc' class='ruledesc' style='font-size:12px;position:absolute;display:none;border:1px #008B8B solid;width:288px;text-align:left;padding:5px;top:48px;left:0px;z-index:1;'></div>"+
				"<div id='enruledesc' class='ruledesc' style='font-size:12px;position:absolute;display:none;border:1px #008B8B solid;width:288px;text-align:left;padding:5px;top:48px;left:0px;z-index:1;'></div>"+
			"</div>"
		);	
		var commonzhdesc = "&gt;所有方形或圆形区域都可以点击。<br>"+
			"&gt;👉: 开局，加入或发牌<br>"+
			"&gt;👆️: 出牌<br>"+
			"&gt;👈️: 返回当前局<br>"+
			"&gt;👍: 得分了，为你打call<br>"+
			"&gt;👎: 扣分了，蓝瘦香菇<br>"+
			"&gt;👤: 请输入昵称<br>"+
			"&gt;🕸😫: 网络异常，请检查网络连接<br>"+
			"&gt;👤😫: 有人抢了你的昵称，请换一个吧<br>"+
			"&gt;full⚙️😫: 服务器满员了，等有人退出了再来吧<br>"+
			"&gt;started⚙️😫: 他们没等你就开始了，请另开一局<br>"+
			"&gt;toomanyplayers⚙️😫: 玩家太多了，请另开一局<br>"+
			"&gt;notyourturn⚙️😫: 没轮到你，请刷新页面<br>"+
			"&gt;🔧: 系统维护期间，得分将无法保留，请提前结束，待维护完成后再来<br>"+
			"&gt;🥊 ♨️: 玩了太多局，该休息一下，皮皮虾，我们走<br>"+
			"&gt;🥊: 局<br>"+
			"&gt;👀: 甘瞪眼<br>"+
			"&gt;🃏: 一人补牌<br>"+
			"&gt;🃏🃏🃏: 每人补牌<br>"+
			"&gt;*: 必须参与计算<br>"+
			"&gt;?: 可以参与计算，也可以不参与计算<br>"+
			"&gt;O: 选择或者重新选择或者重新计算<br>"+
			"&gt;X: 过，不扣分<br>"+
			"&gt;-1: 过，扣1分";
		var commonendesc = "&gt;所有方形或圆形区域都可以点击。<br>"+
			"&gt;👉: 开局，加入或发牌<br>"+
			"&gt;👆️: 出牌<br>"+
			"&gt;👈️: 返回当前局<br>"+
			"&gt;👍: 得分了，为你打call<br>"+
			"&gt;👎: 扣分了，蓝瘦香菇<br>"+
			"&gt;👤: 请输入昵称<br>"+
			"&gt;🕸😫: 网络异常，请检查网络连接<br>"+
			"&gt;👤😫: 有人抢了你的昵称，请换一个吧<br>"+
			"&gt;full⚙️😫: 服务器满员了，等有人退出了再来吧<br>"+
			"&gt;started⚙️😫: 他们没等你就开始了，请另开一局<br>"+
			"&gt;toomanyplayers⚙️😫: 玩家太多了，请另开一局<br>"+
			"&gt;notyourturn⚙️😫: 没轮到你，请刷新页面<br>"+
			"&gt;🔧: 系统维护期间，得分将无法保留，请提前结束，待维护完成后再来<br>"+
			"&gt;🥊 ♨️: 玩了太多局，该休息一下，皮皮虾，我们走<br>"+
			"&gt;🥊: 局<br>"+
			"&gt;👀: 甘瞪眼<br>"+
			"&gt;🃏: 一人补牌<br>"+
			"&gt;🃏🃏🃏: 每人补牌<br>"+
			"&gt;*: 必须参与计算<br>"+
			"&gt;?: 可以参与计算，也可以不参与计算<br>"+
			"&gt;O: 选择或者重新选择或者重新计算<br>"+
			"&gt;X: 过，不扣分<br>"+
			"&gt;-1: 过，扣1分";
		if (game=="pe"){
			$("#zhruledesc").append(
				"&gt;40秒不出牌则系统自动甘瞪眼<br>"+
				"&gt;每局最多8位玩家<br>"+
				"&gt;如果一副牌未分出胜负，则从已出牌里继续补牌<br>"+
				"&gt;单张牌，跟出的牌为牌面大1的单张牌，或者2，或者炸弹，除炸弹外2最大。比如上家出3，只能跟出4或者2或者炸弹<br>"+
				"&gt;两张相同的牌，跟出的牌为牌面大1的两张相同的牌，或者两张2，或者炸弹，除炸弹外两张2最大。比如上家出33，只能跟出44或者22或者炸弹<br>"+
				"&gt;三张及以上不同的牌必须连续，跟出的牌为上家出的第二张牌开始的连续相同张数的牌，或者炸弹，最多连到A。比如上家出345，只能跟出456或者炸弹<br>"+
				"&gt;三张及以上相同的牌即为炸弹，跟出的牌为牌面大的相同张数的炸弹，或者张数更多的任意大小的炸弹。比如上家出444，可跟出666或者3333<br>"+
				"&gt;两张王是最大的炸弹<br>"+
				"&gt;单张王或者两张王可与其它牌一起出，凑成两张相同的牌或者三张及以上连续的牌<br>"+
				"&gt;记分少的为赢家，全关记分翻一倍，三张的炸弹翻一倍，四张和王炸翻两倍，同一局所有翻倍都是累积的<br>"+
				commonzhdesc
			);	
			$("#enruledesc").append(
				">comingsoon<br>"+
				commonendesc
			);	
		} else if (game=="ten"){
			$("#zhruledesc").append(
				"&gt;10分钟不发牌或不出牌则牌局失效，得分友尽<br>"+
				"&gt;每局最多108位玩家<br>"+
				"&gt;玩家出牌后如果系统提示选牌，则选择一张牌能与最后出的牌通过设定的运算符号计算得到10，计算过程中可增加选择的牌和最后出的牌之间的任意牌参与计算，如果选牌后无法算得10，则扣1分。如果选牌之前已经断定无法计算得到10，则可以不选牌，直接点击X表示放弃牌权<br>"+
				"&gt;如果设定的运算符号为<font style='text-shadow:#fff 1px 0 2px,#fff 0 1px 2px,#fff -1px 0 2px,#fff 0 -1px 2px;'>➕➖✖️➗</font>，而且最终的计算结果不是整数，则最终的结果四舍五入。中间结果不进行四舍五入，仍以小数继续计算<br>"+
				"&gt;记分多的为赢家，吃牌数量在三张及以上，则奖励半数。比如吃3张牌得4分，吃4张牌得6分<br>"+
				commonzhdesc
			);	
			$("#enruledesc").append(
				">comingsoon<br>"+
				commonendesc
			);	
		}
		$("#zhrule").click(function(){
			localStorage.setItem("language","zhrule");
			$(this).css("border","1px #008B8B solid");
			$(this).css("border-bottom","1px #000 solid");
			$("#enrule").css("border","1px #000 solid");
			$(this).css("z-index","2");
			$("#enrule").css("z-index","1");
			$(".ruledesc").hide();
			$("#zhruledesc").show();
			$("#invitehint").html("扫描二维码，加入一起嗨");
			$("#zhcopyright").show();
			$("#encopyright").hide();
		});
		$("#enrule").click(function(){
			localStorage.setItem("language","enrule");
			$(this).css("border","1px #008B8B solid");
			$(this).css("border-bottom","1px #000 solid");
			$("#zhrule").css("border","1px #000 solid");
			$(this).css("z-index","2");
			$("#zhrule").css("z-index","1");
			$(".ruledesc").hide();
			$("#enruledesc").show();
			$("#invitehint").html("Scan and Join");
			$("#zhcopyright").hide();
			$("#encopyright").show();
		});
		if (localStorage.getItem("language")!=null){
			$("#"+localStorage.getItem("language")).click();
		} else {
			$("#enrule").click();
		}
		$("body").append(
			$("<div style='cursor:pointer;border:1px #000 solid;border-radius:5px;background:#008B8B;font:15px Arial;width:25px;height:28px;line-height:28px;color:#000;position:absolute;top:0px;left:30px;'>"+
				"?"+
			"</div>").click(function(){
				if ($("#rule").is(":visible")){
					$("#rule").fadeOut(300);
				} else {
					$("#rule").fadeIn(300);
				}
			})
		);
		$("#rule").append(
				$("<div style='cursor:pointer;border:1px #000 solid;border-radius:5px;background:#008B8B;font:15px Arial;width:25px;height:28px;line-height:28px;color:#000;position:fixed;bottom:15px;right:15px;'>"+
					"X"+
				"</div>").click(function(){
					$("#rule").fadeOut(300);
				})
			);
	}

	sessionStorage.removeItem("startpollmsg");
	longpollmsg();	
	animatemsg(0);
	setTimeout(function(){animatemsg(1)},500);
	maintain();	
	checknotice();
});


function post(url, datajson, yescallback,clickid,clickfunction){
	if (clickid){
		$("#"+clickid).unbind("click");
		$("#"+clickid).css("background","#666");
	}
	if ($("#msg").html()=="🕸😫"){
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
				if (data.players&&meindex==-1){
					for (var i=0;i<data.players.length;i++){
						if (data.players[i].meindex!=undefined){
							meindex = data.players[i].meindex;
						}
					}
				}
				if (data.judge&&data.judge.round==0&&meindex==data.judge.nextplayerindex){
					if (data.players.length==1){
						$("#rule").fadeIn(300);
					} else {
						$("#rule").fadeOut(300);
					}
				}
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
					$("#msg").html("👤😫");
					if (clickid&&clickfunction){
						$("#"+clickid).one("click",clickfunction);
					}
					return;
				}
				$("#msg").html(data.reason+"⚙️😫");
			}
	　　	},
	　　	complete : function(XMLHttpRequest,status){
			$("#"+clickid).css("background","#008B8B");
			if (status!="success"){
				ajaxpost.abort();
				if ($("#msg").html()!="🕸😫"){
					$("#msg").attr("prev",$("#msg").html());
					$("#msg").css("color","#DC143C");
	               	$("#msg").html("🕸😫");
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
	if (game!=""&&localStorage.getItem(game+"judgeid")!=null&&localStorage.getItem(game+"playerid")!=null){
		datajson.judgeid=localStorage.getItem(game+"judgeid");
		datajson.playerid=localStorage.getItem(game+"playerid");
		longpollcheckurl = urls[game]+"/lpch";
	}
	if (longpollcheckurl==""){
		setTimeout(longpollcheck,3000);
		return;
	} 
	sessionStorage.setItem("startpollmsg","startpollmsg");
	if ($("#msg").html()=="🕸😫"){
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
				if (data.players&&meindex==-1){
					for (var i=0;i<data.players.length;i++){
						if (data.players[i].meindex){
							meindex = data.players[i].meindex;
						}
					}
				}
				if (data.judge.nextplayerindex==meindex){
					if (data.judge.round==0){
						if (data.players.length==1){
							$("#rule").fadeIn(300);
						} else {
							$("#rule").fadeOut(300);
						}
					}
					if ($("#notice").attr("canplay")=="yes"){
						noticeuturn.play();
					} else {
						if (vibrateSupport) {
							navigator.vibrate(700);
						}
					}
				}
				if (data.judge.round!=0&&data.judge.round%10==0){
					$("#talk").attr("placeholder",data.judge.round+"🥊 ♨️");
				} else {
					$("#talk").attr("placeholder",data.judge.round+"🥊");
				}
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
				if ($("#msg").html()!="🕸😫"){
					$("#msg").attr("prev",$("#msg").html());
					$("#msg").css("color","#DC143C");
	               	$("#msg").html("🕸😫");
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
	if (!$("#talk").length){
		$("body").append(
			"<input id='talk' maxlength='20' style='position:absolute;top:0px;right:30px;display:none;margin:2px;width:120px;height:26px;font-size:15px;background:#ccc;border:0px;' placeholder='0🥊'/>"+
			"<div id='talkto' style='cursor:pointer;border-radius:15px;border:1px #000 solid;background:#008B8B;font:15px Arial;width:28px;height:28px;line-height:28px;color:#000;position:absolute;top:0px;right:0px;'>💬</div>"
		);
		$("#talkto").unbind("click");
		$("#talkto").bind("click",function(){
			if ($.trim($("#talk").val())==""||$("#talk").attr("placeholder")=="..."){
				return;
			}
			$("#talk").attr("oldph",$("#talk").attr("placeholder"));
			$("#talk").attr("placeholder","...");
			var tosend = $("#talk").val();
			$("#talk").val("");
			post(urls.msg+'/sd',{
		　　		groupid:game,
	　　			sessionid:localStorage.getItem(game+"judgeid"),
　　				senderid:localStorage.getItem(game+"playerid"),
		　　		msg:tosend
			},function(data){$("#talk").attr("placeholder",$("#talk").attr("oldph"));});
		});
	}
	$("#talk").fadeIn(1000);
  	$("#talkto").fadeIn(1000);
  	$("#invite").fadeIn(1000);
	$("#invitehint").fadeIn(1000);
  	var receiverid = localStorage.getItem(game+"playerid");
	$.ajax({
		url:urls.msg+'/lpmsg',  
	　　	timeout : 90000, 
	　　	type : 'post',  
	　　	data :{
	　　		receiverid:receiverid
		}, 
	　　	dataType:'json',
	　　	success:function(data){ 
　　			if (data.status=="yes"){
				if (data.msgs.length!=0){
					if ($("#notice").attr("canplay")=="yes"){
						noticemsg.play();
					} else {
						if (vibrateSupport) {
							navigator.vibrate(700);
						}
					}
				}
	　　			var height=[192,250];
	　　			for (var i=0;i<data.msgs.length;i++){
	　　				var left = $(window).width();
	　　				var style="position:fixed;top:"+height[index%2]+"px;left:"+left+"px;color:#228B22;white-space:nowrap;font-size:20px;";
　　					$("body").append($("<font id='msg"+index+"' style='"+style+"'>"+data.msgs[i].sendernick+" ："+data.msgs[i].msg+"</font>"));
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
	var target = game+"maintain";
	post(urls.common+'/maintain',{target:target},function(data){
		if ($("#maintain").is(":visible")){
			$("#maintain").remove();
		}
		if (data.maintain == 1){
			$("body").append($("<div id='maintain' style='display:none;position:fixed;width:100%;left:0px;right:0px;height:21px;top:0px;z-index:10000;background:#DC143C;border:0px;text-align:center;font-size:15px;'></div>"));
			$("#maintain").html("🔧 "+data.date+" 22:00 🔧");
			$("#maintain").fadeIn(1000);
		}
		setTimeout(maintain,30*60000);
	});
}

function shownotice(){
	numofaudios+=1;
	if (numofaudios==2){
		var noticetop = 85;
		if (game=="pe"){
			noticetop = 105;
		}
		$("body").append(
			"<div id='notice' canplay='no' style='display:none;cursor:pointer;border:1px #000 solid;border-radius:5px;background:#008B8B;font:15px Arial;width:33px;height:38px;line-height:38px;color:#000;position:absolute;top:"+noticetop+"px;left:0px;'>🔇</div>"
		);
		$("#notice").fadeIn(1000);
		$("#notice").unbind("click");
		$("#notice").bind("click",function(){
			if ($(this).attr("canplay")=="no"){
				$(this).attr("canplay","yes");
				noticeuturn.play();
				noticemsg.play();
				$(this).html("🔊");
			} else {
				$(this).html("🔇");
				$(this).attr("canplay","no");
			}
		});
	}
}

function checknotice(){
	if (meindex!=-1){
		noticeuturn.src=audios.uturn[meindex%audios.uturn.length];
		noticemsg.src=audios.msg;
		noticeuturn.addEventListener("canplaythrough", function () {
			shownotice();
		}, false);
		noticeuturn.load();
		noticemsg.addEventListener("canplaythrough", function () {
			shownotice();
		}, false);
		noticemsg.load();
	} else {
		setTimeout(checknotice,2000);
	}
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
