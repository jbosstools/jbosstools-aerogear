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
package org.eclipse.jetty.servlets.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
@MultipartConfig(maxFileSize = 1024 * 20)
public class UploadFileServlet extends HttpServlet {
	private static final String PHOTO_PART = "photo";
	private static final String TEMP_PREFIX = "temp";
	private static final String JSON_KEY = "photoUrl";
	private static final String RESOURCE_MAPPING = "/temp-photo/";
	private static final String CONTENT_DISPOSITION = "content-disposition";
	private static final String FILE_NAME = "filename";
	private static final String TEXT_HTML = "text/html";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part photoPart = request.getPart(PHOTO_PART);
		if (photoPart != null) {
			InputStream inputStream = photoPart.getInputStream();
			File file = saveAsTempFile(inputStream, photoPart);
			String mimeType = photoPart.getContentType();
			SaveTempFileInStrorage(file, mimeType);
			String json = generateJSON(file);
			sendResponse(response, json);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	private void SaveTempFileInStrorage(File file, String mimeType) {
		TempFile tempFile = new TempFile();
		tempFile.setFile(file);
		tempFile.setMimeType(mimeType);
		TempFileStorage.getTempFileStorage().put(file.getName(), tempFile);
	}

	private void sendResponse(HttpServletResponse response, String json) throws IOException {
		response.setContentType(TEXT_HTML);
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
	}

	private String generateJSON(File tempFile) {
		String url = RESOURCE_MAPPING + tempFile.getName();
		String json = "{\"" + JSON_KEY + "\": " + "\"" + url + "\"}"; 
		return json;
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private File saveAsTempFile(InputStream inputStream, Part part) throws IOException {
		String fileName = getFileNameFromPart(part);
		File tempFile = File.createTempFile(TEMP_PREFIX, fileName);
		tempFile.deleteOnExit();
		OutputStream out = new FileOutputStream(tempFile);
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}

		inputStream.close();
		out.flush();
		out.close();
		return tempFile;
	}
	
	private String getFileNameFromPart(Part part) {
		for (String cd : part.getHeader(CONTENT_DISPOSITION).split(";")) {
			if (cd.trim().startsWith(FILE_NAME)) {
				return cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}

}