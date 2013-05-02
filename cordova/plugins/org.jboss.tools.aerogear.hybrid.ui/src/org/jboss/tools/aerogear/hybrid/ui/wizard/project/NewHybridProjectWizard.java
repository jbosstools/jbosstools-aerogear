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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class NewHybridProjectWizard extends Wizard implements INewWizard {
	
	private IWizardPage pageOne;

	public NewHybridProjectWizard() {
		setWindowTitle("Hybrid Mobile (Cordova) Application Project");
		
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
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
					openAndSelectConfigFile();
					
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
	
	private void openAndSelectConfigFile(){
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		WizardNewHybridProjectCreationPage page = (WizardNewHybridProjectCreationPage)pageOne;
		IProject project = root.getProject(page.getProjectName());
		IFile file = project.getFile(PlatformConstants.DIR_WWW+"/"+PlatformConstants.FILE_XML_CONFIG);
		
		BasicNewResourceWizard.selectAndReveal(file, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		try {
			IDE.openEditor(activePage, file);
		} catch (PartInitException e) {
			HybridUI.log(IStatus.ERROR, "Error opening the config.xml", e);
		}
	}
	
	@Override
	public void addPages() {
		super.addPages();
		pageOne = new WizardNewHybridProjectCreationPage(getWindowTitle());
		pageOne.setTitle("Create Hybrid Mobile Application Project");
		pageOne.setDescription("Create a hybrid mobile application using Apache Cordova for cross-platform mobile development");
		addPage( pageOne );
	}

}
