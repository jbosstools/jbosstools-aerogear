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
package org.jboss.tools.vpe.cordovasim.servlet.plugin;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.tools.vpe.cordovasim.plugin.model.PluginIdCache;
import org.jboss.tools.vpe.cordovasim.plugin.util.CordovaFileUtil;
import org.jboss.tools.vpe.cordovasim.servlet.util.ServletUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class PluginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private File pluginDir;

	public PluginServlet(File pluginDir) {
		super();
		this.pluginDir = pluginDir;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pathInfo = req.getPathInfo();
		File file = new File(pluginDir, pathInfo);

		resp.setHeader(ServletUtil.CACHE_CONTROL, ServletUtil.NO_CACHE);
		
		if (file.exists()) {
			String ifNoneMatchValue = req.getHeader(ServletUtil.IF_NONE_MATCH);
			String eTag = ServletUtil.generateEtag(file);
			if ((ifNoneMatchValue != null) && (eTag.equals(ifNoneMatchValue))) {
				resp.setHeader(ServletUtil.ETAG, eTag);
				resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			} else {
				String uri = req.getRequestURI();
				String pluginId = getPluginId(uri);
				if (pluginId == null) {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} else {
					String content = null;
					try {
						content = CordovaFileUtil.generatePluginContent(file, pluginId);
					} catch (IOException e) { // TODO log the exception
						resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
					if (content != null) {
						resp.setStatus(HttpServletResponse.SC_OK);
						resp.setContentType(ServletUtil.APPLICATION_JAVASCRIPT_CONTENT_TYPE);
						resp.setHeader(ServletUtil.ETAG, eTag);
						resp.getWriter().write(content);
					}
				}
			}
		} else {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	};

	private String getPluginId(String uri) {
		return PluginIdCache.getPluginId(uri.substring(1)); // removing first "/"
	}

}
