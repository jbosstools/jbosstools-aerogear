/*******************************************************************************
 * Copyright (c) 2013,2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.internal.status;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.statushandlers.StatusManager;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class DefaultStatusHandler extends AbstractStatusHandler {

	@Override
	public void handle(IStatus status) {
		StatusManager platformStatusMgr = StatusManager.getManager();
		platformStatusMgr.handle(status,StatusManager.SHOW | StatusManager.LOG);
	}

	@Override
	public void handle(CoreException e) {
		StatusManager platformStatusMgr = StatusManager.getManager();
		platformStatusMgr.handle(e,HybridUI.PLUGIN_ID);
	}

}
