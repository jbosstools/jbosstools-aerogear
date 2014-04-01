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
package org.jboss.tools.vpe.cordovasim;

import org.jboss.tools.vpe.browsersim.BrowserSimLogger;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class CordovaSimLogger {
	public static void logError(String message, Throwable throwable) {
		BrowserSimLogger.logError(message, throwable, CordovaSimRunner.PLUGIN_ID);
	}
}
