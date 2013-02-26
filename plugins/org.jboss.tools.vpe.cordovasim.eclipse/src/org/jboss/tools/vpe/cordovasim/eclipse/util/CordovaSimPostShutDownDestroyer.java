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
package org.jboss.tools.vpe.cordovasim.eclipse.util;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

public class CordovaSimPostShutDownDestroyer implements IWorkbenchListener {

	Process browserSimProcess;

	public CordovaSimPostShutDownDestroyer(Process browserSimProcess) {
		this.browserSimProcess = browserSimProcess;
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return true;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
		browserSimProcess.destroy();
	}
}