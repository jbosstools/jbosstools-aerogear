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
package org.jboss.tools.vpe.cordovasim.plugin.exception;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class PluginJsException extends Exception {

	private static final long serialVersionUID = 1L;

	public PluginJsException() {
		super();
	}

	public PluginJsException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginJsException(String message) {
		super(message);
	}

	public PluginJsException(Throwable cause) {
		super(cause);
	}

}
