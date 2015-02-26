/*******************************************************************************
	 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordovasim.util;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class StartPageParametersUtil {

	private StartPageParametersUtil() {
	}

	public static String getRippleHomeUrl(String homeUrl) {
		String rippleHomeUrl = homeUrl;
		int indexOfQueryParameter = getQueryIndex(homeUrl);
		if (indexOfQueryParameter > 0) {
			rippleHomeUrl = homeUrl.substring(0, indexOfQueryParameter); // removing startPage query parameters
		}
		return rippleHomeUrl + "?enableripple=true"; //$NON-NLS-1$
	}

	public static String getStartPageParameters(String homeUrl) {
		String parameterString = null;
		int indexOfQueryParameter = getQueryIndex(homeUrl);
		if (indexOfQueryParameter > 0) {
			parameterString = homeUrl.substring(indexOfQueryParameter, homeUrl.length());
		}
		return parameterString;
	}

	private static int getQueryIndex(String homeUrl) {
		return homeUrl.indexOf("?"); //$NON-NLS-1$
	}

}
