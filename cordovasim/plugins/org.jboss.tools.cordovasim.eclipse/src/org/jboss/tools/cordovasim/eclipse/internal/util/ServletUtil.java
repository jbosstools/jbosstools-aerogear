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
package org.jboss.tools.cordovasim.eclipse.internal.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class ServletUtil {
	public static final String CACHE_CONTROL = "Cache-Control"; //$NON-NLS-1$
	public static final String NO_CACHE = "no-store, no-cache, must-revalidate, max-age=0"; //$NON-NLS-1$
	public static final String APPLICATION_JAVASCRIPT_CONTENT_TYPE = "application/javascript"; //$NON-NLS-1$
	public static final String ETAG = "Etag"; //$NON-NLS-1$
	public static final String IF_NONE_MATCH = "If-None-Match"; //$NON-NLS-1$
	public static final String TEXT_XML = "text/xml"; //$NON-NLS-1$

	public static String generateEtag(File file) {
		if (file != null && file.exists()) {
			return String.valueOf(file.lastModified());
		}
		return null;
	}
	
	public static byte[] InputStreamToByteArray(InputStream inputStream) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();

		return buffer.toByteArray();
	}
}
