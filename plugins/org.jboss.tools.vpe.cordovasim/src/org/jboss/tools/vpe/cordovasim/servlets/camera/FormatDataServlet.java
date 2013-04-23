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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class FormatDataServlet extends HttpServlet {
	private static final String TEMP_FILE_URL = "tempFileUrl";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CODECS = "codecs";
	private static final String BITRATE = "bitrate";
	private static final String HEIGHT = "height";
	private static final String WIDTH = "width";
	private static final String DURATION = "duration";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String tempFileUrl = request.getParameter(TEMP_FILE_URL);
		String tempFileName = null;
		TempFile tempFile = null;
		File file = null;
		BufferedImage image = null;

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
			image = ImageIO.read(file);
		}

		if (image != null) {
			String height = Integer.toString(image.getHeight());
			String width = Integer.toString(image.getWidth());
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
			e.printStackTrace();
		}
		return tempFileName;
	}
	
	private String generateJSON(String height, String width) {
		String json = "{" + 
							"\"" + CODECS    +   "\""  +    ": \"null\", "  +
							"\"" + BITRATE   +   "\""  +    ": \"0\", "     +
							"\"" + HEIGHT    +   "\""  +    ": \"" +  height + "\", " +
							"\"" + WIDTH     +   "\""  +    ": \"" +  width  + "\", " +
							"\"" + DURATION  +   "\""  +    ": \"0\""       +
					   "}";
		return json;
	}
}
