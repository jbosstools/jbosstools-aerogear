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

import org.eclipse.swt.widgets.Widget;
import org.jboss.tools.vpe.browsersim.util.ImageList;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class CordovaSimImageList extends ImageList {

	public CordovaSimImageList(Widget disposable) {
		super(disposable);
	}

	@Override
	public InputStream getResourceAsStream(String location) {
		return CordovaSimResourcesUtil.getResourceAsStream(location);
	}

}
