/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.callbacks;

import java.io.IOException;

import org.jboss.tools.vpe.cordovasim.eclipse.util.TransparentReader;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public interface CordovaSimCallback {
	String getCallbackId();
	void call(String lastString, TransparentReader reader) throws IOException;
}
