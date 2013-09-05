/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.plugins.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.aerogear.hybrid.ui.config.internal.ConfigEditor;

public class LaunchCordovaPluginWizardAction extends Action {

	
	/*
	 * @see IAction.run()
	 */
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelection selection = workbenchWindow.getSelectionService().getSelection();
		
		if(selection == null || selection.isEmpty()){
			IWorkbenchPage page = workbenchWindow.getActivePage();
			if(page != null && page.getActiveEditor() instanceof ConfigEditor){
				ConfigEditor editor = (ConfigEditor)page.getActiveEditor();
				IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
				selection= new StructuredSelection(input.getFile().getProject());
			}
		}
	
		IStructuredSelection selectionToPass = null;
		if (selection instanceof IStructuredSelection)
			selectionToPass = (IStructuredSelection) selection;
		else
			selectionToPass = StructuredSelection.EMPTY;
	
		CordovaPluginWizard wizard = new CordovaPluginWizard();
		wizard.init(workbench, selectionToPass);
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}
}
