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
package org.jboss.tools.aerogear.hybrid.ui.wizard.project;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewHybridProjectWizard extends Wizard implements INewWizard {
	
	private IWizardPage pageOne;

	public NewHybridProjectWizard() {
		setWindowTitle("New Aerogear Hybrid Application Project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performFinish() {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				HybridProjectCreator creator = new HybridProjectCreator();
				WizardNewHybridProjectCreationPage page = (WizardNewHybridProjectCreationPage)pageOne;
				try {
					URI location = null;
					if( !page.useDefaults() ){
						location = page.getLocationURI();
					}
					String appName = page.getApplicationName();
					String appID = page.getApplicationID();
					creator.createProject(page.getProjectName(), location ,appName, appID, monitor);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		
		try {
			getContainer().run(false, true, runnable);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		pageOne = new WizardNewHybridProjectCreationPage(getWindowTitle());
		pageOne.setTitle("Create Hybrid Application Project");
		pageOne.setDescription("Creates an aerogear hybrid application project for cross-platform mobile development");
		addPage( pageOne );
	}

}
