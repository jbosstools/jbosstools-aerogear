/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginVersion;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.jboss.tools.aerogear.hybrid.ui.internal.status.StatusManager;

public class RestorePluginWizard extends Wizard {
	
	private PluginRestorePage page;
	private HybridProject project;
	
	public RestorePluginWizard(HybridProject project) {
		this.project = project;
		setWindowTitle("Restore Cordova Plug-in");
		setDefaultPageImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, CordovaPluginWizard.IMAGE_WIZBAN));
	}
	@Override
	public void addPages() {
		page = new PluginRestorePage(project);
		addPage(page);
		
	}
	@Override
	public boolean performFinish() {
		CordovaRegistryPluginVersion[] versions = page.getSelectedRestorables();
		try {
			getContainer().run(true, true, new PluginRestoreOperation(project, versions));
		} catch (InvocationTargetException e) {
			if (e.getTargetException() != null) {
				if(e.getTargetException() instanceof CoreException ){
					StatusManager.handle((CoreException) e.getTargetException());
				}else{
				ErrorDialog.openError(getShell(), "Error restoring plug-ins", null, 
						new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Errors occured during plug-in installation", e.getTargetException() ));
				return false;
				}
			}
			return false;
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}
		return true;
	}

}
