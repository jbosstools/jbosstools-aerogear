/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.android.core.adt;

import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_ASSETS;

import java.io.File;

import org.eclipse.core.runtime.Assert;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;

public class AndroidProjectUtils {
	
	public static File getPlatformWWWDirectory(File projectDirectory) {
		Assert.isNotNull(projectDirectory);
		return new File(projectDirectory, DIR_ASSETS + File.separator +PlatformConstants.DIR_WWW);
	}

}
