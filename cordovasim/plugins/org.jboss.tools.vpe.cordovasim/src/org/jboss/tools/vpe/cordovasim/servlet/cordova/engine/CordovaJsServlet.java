package org.jboss.tools.vpe.cordovasim.servlet.cordova.engine;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.tools.vpe.cordovasim.CordovaSimLogger;

public class CordovaJsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB
	private static final String MIME_TYPE = "application/javascript"; //$NON-NLS-1$
	private static final String CONTENT_LENGTH = "Content-Length"; //$NON-NLS-1$
	private static final String DEFAULT_CORDOVA_JS = "/ripple/cordova/cordova-3.1.0.js"; //$NON-NLS-1$
	private String cordovaJsLocation;

	public CordovaJsServlet(String cordovaJsLocation) {
		super();
		this.cordovaJsLocation = cordovaJsLocation;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (cordovaJsLocation == null) {
			response.sendRedirect(DEFAULT_CORDOVA_JS); // Using cordova-3.1.0 engine
			return;
		}

		File cordovaJsFile = new File(cordovaJsLocation);

		if (!cordovaJsFile.exists()) {
			response.sendRedirect(DEFAULT_CORDOVA_JS); // Using cordova-3.1.0 engine
			return;
		}

		// Init servlet response
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType(MIME_TYPE);
		response.setHeader(CONTENT_LENGTH, String.valueOf(cordovaJsFile.length()));

		// Prepare streams
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {
			// Open streams
			input = new BufferedInputStream(new FileInputStream(cordovaJsFile), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			// Write file contents to response
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {
			close(output);
			close(input);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				CordovaSimLogger.logError(e.getMessage(), e);
			}
		}
	}

}
