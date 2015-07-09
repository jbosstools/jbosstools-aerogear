/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.cordova.internal.wizards.NewApplicationWizard;

public class ConvertToFeedHenryApplicationHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWizardDescriptor wizDescriptor = workbench.getNewWizardRegistry().findWizard(NewApplicationWizard.WIZARD_ID);
		if(wizDescriptor == null ){
			FHPlugin.log(IStatus.WARNING, 
					"Missing wizard description for wizard id" + NewApplicationWizard.WIZARD_ID, null);
			return null;
		}
		
		try {
			IWorkbenchWizard wizard = wizDescriptor.createWizard();
			wizard.init(workbench, getSelection(event));
			
			WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.setTitle(wizard.getWindowTitle());
			dialog.open();
			
		} catch (CoreException e) {
			throw new ExecutionException("Unable to start wizard", e);
		}
		return null;
	}
	
	private IStructuredSelection getSelection(ExecutionEvent event){
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection != null && selection instanceof IStructuredSelection){
			return (IStructuredSelection) selection;
		}
		return StructuredSelection.EMPTY;
		
	}
	
}

