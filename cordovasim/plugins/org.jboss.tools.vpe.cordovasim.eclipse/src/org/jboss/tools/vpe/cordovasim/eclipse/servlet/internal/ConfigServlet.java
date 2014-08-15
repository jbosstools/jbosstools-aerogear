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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.internal.util.ServletUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.internal.CordovaSimLaunchParametersUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class ConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;	
	private IProject project;

	public ConfigServlet(IProject project) {
		this.project = project;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		IFile configXml = CordovaSimLaunchParametersUtil.getConfigXml(project);
		
		if (configXml == null || !configXml.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		response.setContentType(ServletUtil.TEXT_XML);
		
		try {
			InputStream  contents = configXml.getContents();
			if (contents != null) {
				InputStreamReader isr = new InputStreamReader(contents);
				BufferedReader reader = new BufferedReader(isr);
				PrintWriter writer = response.getWriter();
				String text = ""; //$NON-NLS-1$
				while ((text = reader.readLine()) != null) {
					writer.println(text);
				}
			}
		} catch (CoreException e) {
			Activator.logError(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
