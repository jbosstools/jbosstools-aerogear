package org.jboss.tools.vpe.cordovasim;

import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.servlets.ProxyServlet;

public class CrossOriginProxyServlet extends ProxyServlet {
	private String urlParameterName;

	public CrossOriginProxyServlet(String urlParameterName) {
		this.urlParameterName = urlParameterName;
	}
	
	@Override
	protected HttpURI proxyHttpURI(HttpServletRequest request, String uri)
			throws MalformedURLException {;
		String proxiedUrl = request.getParameter(urlParameterName);
		if (proxiedUrl != null) {
			return new HttpURI(proxiedUrl);				
		} else {
			return null;
		}
	}
	
	@Override
	protected void customizeExchange(HttpExchange exchange,
			HttpServletRequest request) {
		HttpURI url = new HttpURI(exchange.getRequestURI());
		exchange.setRequestHeader("Host", url.getHost()); //$NON-NLS-1$
	}
}
