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
package org.jboss.tools.aerogear.hybrid.ui.wizard.export;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.extensions.NativeProjectBuilder;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractNativeBinaryBuildDelegate;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.jboss.tools.aerogear.hybrid.ui.internal.status.StatusManager;

public class NativeBinaryExportWizard extends Wizard implements IExportWizard {

	private static final String DIALOG_SETTINGS_KEY = "NativeBinaryExportWizard";
	private NativeBinaryDestinationPage pageOne;
	private IStructuredSelection initialSelection;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle("Export Mobile Application");
		setNeedsProgressMonitor(true);
		this.initialSelection=selection;
		IDialogSettings workbenchSettings= HybridUI.getDefault().getDialogSettings();
		IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		setDialogSettings(section);
	}

	@Override
	public boolean performFinish() {
		List<HybridProject> projects =  pageOne.getSelectedProjects();
		List<NativeProjectBuilder> builders = pageOne.getSelectedPlatforms();
		ArrayList<AbstractNativeBinaryBuildDelegate> delegates = new ArrayList<AbstractNativeBinaryBuildDelegate>();
		for (HybridProject hybridProject : projects) {
			for (NativeProjectBuilder nativeProjectBuilder : builders) {
				try {
					AbstractNativeBinaryBuildDelegate dlg =nativeProjectBuilder.createDelegate(hybridProject.getProject(), null);
					delegates.add(dlg);
				} catch (CoreException e) {
					HybridCore.log(IStatus.ERROR, "Error creating native binary builder delegate for " +nativeProjectBuilder.getPlatform(), e);
				}
			}
		}
		
		NativeBinaryExportOperation op = new NativeBinaryExportOperation(delegates,new File(pageOne.getDestinationDirectory()), pageOne);
		
		
		try {
			getContainer().run(true, true, op);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() != null) {
				if(e.getTargetException() instanceof CoreException ){
					StatusManager.handle((CoreException) e.getTargetException());
				}else{
					ErrorDialog.openError(getShell(), "Error exporting mobile application",null, 
							new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error while exporting mobile application", e.getTargetException() ));
					return false;
				}
			}
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		savePageSettings();
		return true;
	}

	@Override
	public void addPages() {
		super.addPages();
		pageOne = new NativeBinaryDestinationPage("Export Mobile Application",initialSelection);
		pageOne.setTitle("Export Mobile Application");
		pageOne.setDescription("Builds a mobile application that can be installed and run on a mobile device");
		addPage(pageOne);
	}
	private void savePageSettings() {
		IDialogSettings workbenchSettings= HybridUI.getDefault().getDialogSettings();
		IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		if(section == null ){
			section = workbenchSettings.addNewSection(DIALOG_SETTINGS_KEY);
		}
		setDialogSettings(section);
		pageOne.saveWidgetValues();
	}
	
}
