package com.tenotenm.gdy.servlets;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/act/sg")
public class Actstartgame extends HttpServlet {

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		OutputStream os = null;
		try {
			os = res.getOutputStream();avabcdefghlijkjlll
			String[] parts = req.getRequestURI().split("/");
		} catch (Exception e) {
			
			throw new IOException(e);
		} finally {
			try {
				os.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
}