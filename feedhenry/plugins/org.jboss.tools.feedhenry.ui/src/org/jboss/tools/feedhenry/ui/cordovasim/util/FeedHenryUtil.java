/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.feedhenry.ui.cordovasim.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class FeedHenryUtil {
	private static final String FH_CONFIG_JSON = "www/fhconfig.json"; //$NON-NLS-1$
	private static final String INDEX_HTML = "index.html"; //$NON-NLS-1$
	private static final String DEFAULT_FH_PARAMETER = "http://localhost:8001"; //$NON-NLS-1$
	private static final String URL = "url"; //$NON-NLS-1$

	private FeedHenryUtil() {
	}

	public static boolean isFeedHenry(final IProject project) {
		if (project != null) {
			IFile file = project.getFile(FH_CONFIG_JSON);
			if (file != null && file.exists()) {
				return true;
			}
		}
		return false;
	}

	public static String addDefaultServerParameter(final String startPage) {
		String newStartPage = (startPage != null) ? startPage : INDEX_HTML;
		int startParamIndex = newStartPage.lastIndexOf("?"); //$NON-NLS-1$
		if (startParamIndex == -1) {
			newStartPage += "?" + URL + "=" + DEFAULT_FH_PARAMETER; //$NON-NLS-1$ //$NON-NLS-2$			
		} else {
			newStartPage += "&" + URL + "=" + DEFAULT_FH_PARAMETER; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return newStartPage;
	}

}