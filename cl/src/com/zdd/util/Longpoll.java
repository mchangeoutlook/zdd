package com.zdd.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class Longpoll {
	private static final Map<String, AsyncContext> polls = new HashMap<String, AsyncContext>();
	public static void start(String starter, Long timeout, ServletRequest req, ServletResponse res) {
		if (polls.get(starter)!=null) {
			polls.get(starter).getRequest().setAttribute("starter", "none");
		}
		polls.put(starter, req.startAsync(req, res));
		polls.get(starter).setTimeout(timeout);
		polls.get(starter).getRequest().setAttribute("starter", starter);
		polls.get(starter).addListener(new AsyncListener() {
			public void onComplete(AsyncEvent event) throws IOException{
				polls.remove(event.getAsyncContext().getRequest().getAttribute("starter"));
			}

	        public void onTimeout(AsyncEvent event) throws IOException{
	        		event.getAsyncContext().complete();
	        		polls.remove(event.getAsyncContext().getRequest().getAttribute("starter"));
	        }

			@Override
			public void onError(AsyncEvent event) throws IOException {
				event.getAsyncContext().complete();
				polls.remove(event.getAsyncContext().getRequest().getAttribute("starter"));
			}

			@Override
			public void onStartAsync(AsyncEvent arg0) throws IOException {
				//do nothing
			}
		});
	}
	public static synchronized boolean end(String starter, String returnvalue) {
		if (polls.get(starter)!=null) {
			try {
				polls.get(starter).getResponse().getWriter().write(returnvalue);
				polls.get(starter).getRequest().setAttribute("starter", "none");
				polls.get(starter).complete();
				polls.remove(starter);
				return true;
			} catch (Exception e) {
				//do nothing
			}
		}
		return false;
	}
	
}
