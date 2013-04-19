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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class TempFileStorage {
	private static final Map<String, TempFile> TEMP_FILE_STORAGE = new ConcurrentHashMap<String, TempFile>();

	private TempFileStorage() {
	}

	public static Map<String, TempFile> getTempFileStorage() {
		return TEMP_FILE_STORAGE;
	}
}
