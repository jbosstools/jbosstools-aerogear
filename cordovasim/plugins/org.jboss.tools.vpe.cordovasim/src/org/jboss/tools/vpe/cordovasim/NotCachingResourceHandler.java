/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class NotCachingResourceHandler extends ResourceHandler {

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpServletRequestWrapper modifiedRequest = new HttpServletRequestWrapper(request) { // Prevent NOT_MODIFIED_SINCE 304 response
			private static final long WAS_MODIFIED = -1;

			@Override
			public long getDateHeader(String name) {
				HttpServletRequest request = (HttpServletRequest) getRequest();

				if (HttpHeaders.IF_MODIFIED_SINCE.equals(name)) {
					return WAS_MODIFIED;
				}
				return request.getDateHeader(name);
			};
		};

		HttpServletResponseWrapper modifiedResponce = new HttpServletResponseWrapper(response) { // Prevent "if-modified-since" headers handling
			
			@Override
			public void setDateHeader(String name, long date) {
			}
		};

		super.handle(target, baseRequest, modifiedRequest, modifiedResponce);
	}
}
