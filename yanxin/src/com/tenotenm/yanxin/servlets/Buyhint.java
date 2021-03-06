package com.tenotenm.yanxin.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenotenm.yanxin.util.Reuse;
import com.zdd.bdc.client.biz.Configclient;
import com.zdd.bdc.client.util.STATIC;

@SuppressWarnings("serial")
@WebServlet("/freego/buyhint")
public class Buyhint extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Reuse.respond(response, Configclient.getinstance(Reuse.namespace_yanxin, STATIC.REMOTE_CONFIGFILE_CORE).read("buyhint"), null);
		} catch (Exception e) {
			Reuse.respond(response, null, e);
		}
	}

}