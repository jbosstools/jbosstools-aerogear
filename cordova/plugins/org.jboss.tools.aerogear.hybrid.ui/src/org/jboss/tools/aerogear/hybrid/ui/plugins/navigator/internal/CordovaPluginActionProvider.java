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
package org.jboss.tools.aerogear.hybrid.ui.plugins.navigator.internal;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.jboss.tools.aerogear.hybrid.ui.plugins.internal.PluginUninstallAction;

public class CordovaPluginActionProvider extends CommonActionProvider {
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.add(new PluginUninstallAction());
	}

}
