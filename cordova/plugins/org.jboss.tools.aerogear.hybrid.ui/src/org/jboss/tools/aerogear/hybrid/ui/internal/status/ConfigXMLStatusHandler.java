/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.jboss.tools.aerogear.hybrid.ui.status.AbstractStatusHandler;

public class ConfigXMLStatusHandler extends AbstractStatusHandler {

	@Override
	public void handle(IStatus status) {
		MessageDialog.openError(AbstractStatusHandler.getShell(), "Plug-in Error", 
				"Error parsing the plug-in information. Your platform(s) may not be supported by this plug-in or it may indicate a problem with the plug-in itself.");
	}

	@Override
	public void handle(CoreException e) {
		handle(e.getStatus());
	}

}
