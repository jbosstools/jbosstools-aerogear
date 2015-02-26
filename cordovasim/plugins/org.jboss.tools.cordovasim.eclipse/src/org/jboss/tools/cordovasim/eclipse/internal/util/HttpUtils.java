/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Xavier Coulon - Initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cordovasim.eclipse.internal.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.StringTokenizer;

/**
 * Utility for HTTP Requests
 * @author xcoulon
 *
 */
public class HttpUtils {

	/**
	 * Private constructor of this utiliy class
	 */
	private HttpUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * <p>
	 * Iterates over the given acceptedContentTypes, looking for one of those
	 * values:
	 * <ul>
	 * <li>text/html</li>
	 * <li>application/xhtml+xml</li>
	 * <li>application/xml</li>
	 * </ul>
	 * </p>
	 * 
	 * @param acceptedContentTypes
	 * @return true if one of the values above was found, false otherwise
	 */
	public static boolean isHtmlContentType(final String acceptedContentTypes) {
		if (acceptedContentTypes == null) {
			return false;
		}
		// first, let's remove everything behind the comma character
		int location = acceptedContentTypes.indexOf(";"); //$NON-NLS-1$
		final String contentTypes = (location != -1) ? acceptedContentTypes.substring(0, location):acceptedContentTypes; 
		// now, let's analyze each type
		final StringTokenizer tokenizer = new StringTokenizer(contentTypes, ","); //$NON-NLS-1$
		while (tokenizer.hasMoreElements()) {
			final String acceptedContentType = tokenizer.nextToken();
			if ("text/html".equals(acceptedContentType) || "application/xhtml+xml".equals(acceptedContentType)  //$NON-NLS-1$//$NON-NLS-2$
					|| "application/xml".equals(acceptedContentType)) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the {@link Charset} from the given content-type.
	 * 
	 * @param contentType
	 *            the given content type that may contain some ";charset=...".
	 * @param defaultCharsetName
	 *            the name of the default charset to return in case when the given
	 *            contentType would be null or would not contain any specific
	 *            charset. If this name cannot be resolved into a valid charset, then "UTF-8" is used.
	 * @return the value of the "charset" token in the given contentType, or
	 *         the given defaultCharsetName, or "UTF-8" as a last resort.
	 */
	public static Charset getContentCharSet(final String contentType, final String defaultCharsetName) {
		if(contentType != null) { 
			final StringTokenizer stk = new StringTokenizer(contentType, ";"); //$NON-NLS-1$
			while(stk.hasMoreTokens()) {
				final String token = stk.nextToken().toLowerCase().replace(" ", "");  //$NON-NLS-1$//$NON-NLS-2$
				if(token.startsWith("charset=")) {  //$NON-NLS-1$
					final StringTokenizer tokenSplitter = new StringTokenizer(token, "="); //$NON-NLS-1$
					tokenSplitter.nextToken(); // skip the 'charset' part as we already know it
					final String value = tokenSplitter.hasMoreTokens()? tokenSplitter.nextToken() : null;
					return Charset.forName(value.toUpperCase());
				}
			}
		}
		try {
			return Charset.forName(defaultCharsetName);
		} catch(UnsupportedCharsetException e) {
			return Charset.forName("UTF-8"); //$NON-NLS-1$
		}
	}

}
