package org.jboss.tools.vpe.cordovasim;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.servlets.ProxyServlet;

public class CrossOriginProxyServlet extends ProxyServlet {
	private String servletPrefix;

	public CrossOriginProxyServlet(String servletPrefix) {
		this.servletPrefix = servletPrefix;
	}
	
	@Override
	protected HttpURI proxyHttpURI(HttpServletRequest request, String uri)
			throws MalformedURLException {;
		if (uri.startsWith(servletPrefix)) {
			return new HttpURI(uri.substring(servletPrefix.length()));				
		} else {
			return null;
		}
	}
	
	@Override
	protected void customizeExchange(HttpExchange exchange,
			HttpServletRequest request) {
		HttpURI url = new HttpURI(exchange.getRequestURI());
		exchange.setRequestHeader("Host", url.getHost());
	}
}
