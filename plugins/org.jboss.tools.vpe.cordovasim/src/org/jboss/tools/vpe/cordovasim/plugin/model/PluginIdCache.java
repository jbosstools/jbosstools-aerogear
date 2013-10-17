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
package org.jboss.tools.vpe.cordovasim.plugin.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class PluginIdCache {
	private static final Map<String, String> FILE_TO_ID_CACHE = new HashMap<String, String>(); // Maps plugin's "file" to "id"

	private PluginIdCache() {
	}

	public static void update(List<Plugin> plugins) {
		FILE_TO_ID_CACHE.clear();
		for (Plugin plugin : plugins) {
			FILE_TO_ID_CACHE.put(plugin.getFile(), plugin.getId());
		}
	}

	public static String getPluginId(String fileName) {
		return FILE_TO_ID_CACHE.get(fileName);
	}

}
