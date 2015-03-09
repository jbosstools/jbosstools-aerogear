/*******************************************************************************
 * Copyright (c) 2007-2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordovasim.eclipse.servlet.internal;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.proxy.ProxyServlet;

/**
 * @author Yahor Radtsevich (yradtsevich)
 * @author Ilya Buziuk (ibuziuk)
 */
public class CrossOriginProxyServlet extends ProxyServlet {
	private static final long serialVersionUID = 1L;
	private String urlParameterName;

	public CrossOriginProxyServlet(String urlParameterName) {
		this.urlParameterName = urlParameterName;
	}

	@Override
	protected URI rewriteURI(HttpServletRequest request) {
		String proxyTo = getProxyTo(request);
		if (proxyTo != null) {
			return URI.create(proxyTo).normalize();
		} else {
			return null;
		}
	}

	@Override
	protected void customizeProxyRequest(Request proxyRequest, HttpServletRequest request) {
		HttpFields headers = proxyRequest.getHeaders();
		// Removing local "Host" header
		headers.remove("Host"); //$NON-NLS-1$ 
		HttpURI url = new HttpURI(proxyRequest.getURI());
		headers.add("Host", url.getHost()); //$NON-NLS-1$
	} 

	private String getProxyTo(HttpServletRequest request) {
		return request.getParameter(urlParameterName);
	}

}
