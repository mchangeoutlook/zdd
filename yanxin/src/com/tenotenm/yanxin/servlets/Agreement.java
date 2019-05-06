package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/freego/agreement")
public class Agreement extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Map<String, Object> ret = new Hashtable<String, Object>();
			ret.put("title", Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("agreement.title"));
			int itemnum = Integer.parseInt(Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("agreement.items"));
			Vector<String> items = new Vector<String>(itemnum);
			for (int i=0;i<itemnum;i++) {
				items.add(MessageFormat.format(
					Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("agreement.item."+i),
					Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIG_CORE).read("forgetpass")));
			}
			ret.put("items", items);
			Reuse.respond(response, ret, null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}