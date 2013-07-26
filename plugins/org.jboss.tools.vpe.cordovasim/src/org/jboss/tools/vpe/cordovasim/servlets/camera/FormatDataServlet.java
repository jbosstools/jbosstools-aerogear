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
package org.jboss.tools.vpe.cordovasim.servlets.camera;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.jboss.tools.vpe.cordovasim.CordovaSimLogger;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class FormatDataServlet extends HttpServlet {
	private static final long serialVersionUID = -4120900565146871118L;
	
	private static final String TEMP_FILE_URL = "tempFileUrl"; //$NON-NLS-1$
	private static final String APPLICATION_JSON = "application/json"; //$NON-NLS-1$
	private static final String CODECS = "codecs"; //$NON-NLS-1$
	private static final String BITRATE = "bitrate"; //$NON-NLS-1$
	private static final String HEIGHT = "height"; //$NON-NLS-1$
	private static final String WIDTH = "width"; //$NON-NLS-1$
	private static final String DURATION = "duration"; //$NON-NLS-1$
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tempFileUrl = request.getParameter(TEMP_FILE_URL);
		String tempFileName = null;
		TempFile tempFile = null;
		File file = null;
		Image image = null;
		Rectangle imageData = null;

		if (tempFileUrl != null) {
			tempFileName = getTempFileName(tempFileUrl);
		}

		if (tempFileName != null) {
			tempFile = TempFileStorage.getTempFileStorage().get(tempFileName);
		}

		if (tempFile != null) {
			file = tempFile.getFile();
		}

		if (file != null && file.exists()) {
			/* JBIDE-15003 - Have to use org.eclipse.swt.graphics.Image because of the  
			 * SWT Bug 212617 Launching Swing based IApplications on Mac still results in deadlock */	
			image = new Image(null, file.getAbsolutePath());
		}
		
		if (image != null) {
			imageData = image.getBounds();
			image.dispose();
		}

		if (imageData != null) {
			String height = Integer.toString(imageData.height);
			String width = Integer.toString(imageData.width);
			String json = generateJSON(height, width);
			sendResponse(response, json);
		} else { 
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private void sendResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType(APPLICATION_JSON);
		PrintWriter out = response.getWriter();
		out.print(json);
	}

	private String getTempFileName(String tempFileUrl) {
		String tempFileName = null;
		try {
			URI uri = new URI(tempFileUrl);
			String path = uri.getPath();
			tempFileName = path.substring(path.lastIndexOf('/') + 1);
		} catch (URISyntaxException e) {
			CordovaSimLogger.logError(e.getMessage(), e);
		}
		return tempFileName;
	}
	
	private String generateJSON(String height, String width) {
		String json = "{" +  //$NON-NLS-1$
							"\"" + CODECS    +   "\""  +    ": \"null\", "  + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"\"" + BITRATE   +   "\""  +    ": \"0\", "     + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							"\"" + HEIGHT    +   "\""  +    ": \"" +  height + "\", " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"\"" + WIDTH     +   "\""  +    ": \"" +  width  + "\", " + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							"\"" + DURATION  +   "\""  +    ": \"0\""       + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					   "}"; //$NON-NLS-1$
		return json;
	}
}
