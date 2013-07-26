package org.jboss.tools.vpe.cordovasim;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticResponseServlet extends HttpServlet {
	private static final long serialVersionUID = -7965774938057553057L;
	
	private String responseString;

	public StaticResponseServlet(String responseString) {
		super();
		this.responseString = responseString;
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain"); //$NON-NLS-1$
		resp.setStatus(HttpServletResponse.SC_OK);
		
		resp.getWriter().write(responseString);
	}
}
