/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.servlet.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.vpe.browsersim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.internal.util.HttpUtils;
import org.jboss.tools.vpe.cordovasim.eclipse.internal.util.ResourceUtils;
import org.jboss.tools.vpe.cordovasim.eclipse.internal.util.ServletUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class WorkspaceFileServlet extends HttpServlet {
	String rootFolderLocation;

	/** serialVersionUID */
	private static final long serialVersionUID = 163695311668462503L;
	
	public WorkspaceFileServlet(String rootFolderLocation) {
		super();
		this.rootFolderLocation = rootFolderLocation;
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		final String requestURI = request.getRequestURI();
		if (requestURI == null) {
			httpServletResponse.setStatus(400);
		} else {
			final IResource resource = ResourceUtils.retrieveResource(rootFolderLocation + requestURI);
			if (resource != null && resource.getType() == IResource.FILE) {
				try {
					final IFile workspaceFile = (IFile) resource;
					InputStream inputStream = workspaceFile.getContents();
					final byte[] content = ServletUtil.InputStreamToByteArray(inputStream);
								
					httpServletResponse.getOutputStream().write(content);
					httpServletResponse.setStatus(HttpServletResponse.SC_OK);
					
					final Charset charset = HttpUtils.getContentCharSet(request.getHeader("accept"), workspaceFile.getCharset()); //$NON-NLS-1$
					String guessedContentType = URLConnection.guessContentTypeFromName(resource.getName());
					if(guessedContentType != null && !guessedContentType.contains("charset")) { //$NON-NLS-1$
						guessedContentType = guessedContentType.concat("; charset=").concat(charset.name()); //$NON-NLS-1$
					}
					httpServletResponse.setContentType(guessedContentType);
				} catch (CoreException e) {
					Activator.logError(e.getMessage(), e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else if (resource != null && resource.getType() == IResource.FOLDER) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
	}

}
