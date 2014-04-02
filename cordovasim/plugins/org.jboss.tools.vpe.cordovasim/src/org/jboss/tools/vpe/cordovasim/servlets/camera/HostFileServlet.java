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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class HostFileServlet extends HttpServlet {
	private static final long serialVersionUID = 877976028427058251L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = getFileName(request);
		TempFile tempFile = TempFileStorage.getTempFileStorage().get(fileName);
		if (tempFile != null) {
			File hostedFile = tempFile.getFile();
			String mimeType = tempFile.getMimeType();
			sendFile(response, hostedFile, mimeType);
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private String getFileName(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		String fileName = pathInfo.substring(1, pathInfo.length());
		return fileName;
	}
	
	private void sendFile(HttpServletResponse response, File hostedFile, String mimeType) throws IOException {
		response.setContentType(mimeType);
		OutputStream out = response.getOutputStream();
        FileInputStream in = new FileInputStream(hostedFile);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = in.read(buffer)) > 0){
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
	}
}

