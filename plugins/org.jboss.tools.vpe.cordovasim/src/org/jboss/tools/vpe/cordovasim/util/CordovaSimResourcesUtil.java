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
package org.jboss.tools.vpe.cordovasim.util;

import java.io.InputStream;

import org.jboss.tools.vpe.browsersim.util.ResourcesUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimResourcesUtil {
	private static final String RESOURCES_ROOT_FOLDER = "/org/jboss/tools/vpe/cordovasim/resources/"; 

	public static InputStream getResourceAsStream(String name) {
		return ResourcesUtil.getResourceAsStream(RESOURCES_ROOT_FOLDER, name);
	}
	
	public static String getResourceAsString(String name) {
		return ResourcesUtil.getResourceAsString(RESOURCES_ROOT_FOLDER, name);
	}
}
