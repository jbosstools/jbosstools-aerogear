/*******************************************************************************
 * Copyright (c) 2014,2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.cordova.internal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.egit.core.op.CloneOperation;
import org.eclipse.egit.core.op.CloneOperation.PostCloneTask;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.thym.core.engine.HybridMobileEngineManager;
import org.eclipse.thym.ui.HybridUI;
import org.eclipse.thym.ui.wizard.project.HybridProjectCreator;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;

public class CordovaImportWizard extends Wizard implements IImportWizard {

	private static final String DIALOG_SETTINGS_KEY = "FeedHenryCordovaImportWizard";
	private FHApplicationSelectionPage page;
	private IStructuredSelection currentSelection;
	
	public CordovaImportWizard() {
		super();
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID,"/icons/wizban/fh_wizban.png"));
		IDialogSettings wbSettings = FHPlugin.getDefault().getDialogSettings();
		IDialogSettings section= wbSettings.getSection(DIALOG_SETTINGS_KEY);
		setDialogSettings(section);

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	    setWindowTitle("Import");
	    currentSelection = selection;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		page = new FHApplicationSelectionPage(currentSelection);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		final List<FeedHenryApplication> apps = page.getSelectedApplications();
		final FeedHenryApplication app = apps.get(0);
		PostCloneTask pct = new PostCloneTask() {
			
			@Override
			public void execute(Repository repository, IProgressMonitor monitor) throws CoreException {
				importProject( app, repository.getWorkTree(), monitor);
			}
		};
		try {
			URIish uri = new URIish(app.getRepoUrl());
			IPath workpath = new Path(page.getWorkingPath());
			final CloneOperation op = new CloneOperation(uri, true, null, workpath.toFile(), "master", "origin", 60);
			op.addPostCloneTask(pct);
			getContainer().run(false, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					op.run(monitor);
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		savePageSettings();
		return true;
	}
	private void addToWorkingSets(IProject project) {
		IWorkingSet[] selectedWorkingSets = page.getSelectedWorkingSets();
		if(selectedWorkingSets == null || selectedWorkingSets.length == 0)
			return; // no Working set is selected
		IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
		workingSetManager.addToWorkingSets(project, selectedWorkingSets);
	}
	
	private void savePageSettings() {
		IDialogSettings workbenchSettings= HybridUI.getDefault().getDialogSettings();
		IDialogSettings section= workbenchSettings.getSection(DIALOG_SETTINGS_KEY);
		if(section == null ){
			section = workbenchSettings.addNewSection(DIALOG_SETTINGS_KEY);
		}
		setDialogSettings(section);
		page.saveWidgetValues();
	}

	@SuppressWarnings("restriction")
	private void importProject(FeedHenryApplication app, File location,IProgressMonitor monitor) throws CoreException{
		HybridProjectCreator projectCreator = new HybridProjectCreator();
		app.getTitle();
		IProject project = projectCreator.createProject(app.getTitle(), location.toURI(), app.getTitle(), app.getTitle(),
				HybridMobileEngineManager.getDefaultEngine(), monitor);
		addToWorkingSets(project);
	}
	
}
