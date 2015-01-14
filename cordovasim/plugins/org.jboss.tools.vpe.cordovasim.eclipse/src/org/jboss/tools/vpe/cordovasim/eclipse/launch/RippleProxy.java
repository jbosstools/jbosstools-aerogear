/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.launch;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public enum RippleProxy {
	DISABLED("disabled"), LOCAL("local"), REMOTE("remote"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private final String value;

	private RippleProxy(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}