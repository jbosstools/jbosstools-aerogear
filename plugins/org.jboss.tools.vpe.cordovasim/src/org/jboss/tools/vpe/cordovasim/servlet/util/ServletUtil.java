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
package org.jboss.tools.vpe.cordovasim.servlet.util;

import java.io.File;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class ServletUtil {
	public static final String CACHE_CONTROL = "Cache-Control"; //$NON-NLS-1$
	public static final String NO_CACHE = "no-store, no-cache, must-revalidate, max-age=0"; //$NON-NLS-1$
	public static final String APPLICATION_JAVASCRIPT_CONTENT_TYPE = "application/javascript"; //$NON-NLS-1$
	public static final String ETAG = "Etag"; //$NON-NLS-1$
	public static final String IF_NONE_MATCH = "If-None-Match"; //$NON-NLS-1$

	public static String generateEtag(File file) {
		if (file.exists()) {
			return String.valueOf(file.lastModified());
		}
		return null;
	}

}
