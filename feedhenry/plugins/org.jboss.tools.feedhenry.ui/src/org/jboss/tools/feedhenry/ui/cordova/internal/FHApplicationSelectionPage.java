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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.thym.ui.util.DirectorySelectionGroup;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WorkingSetGroup;
import org.jboss.tools.feedhenry.ui.internal.FeedHenryApplicationSelector;
import org.jboss.tools.feedhenry.ui.internal.FeedHenryApplicationSelector.SelectionChangeCallback;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;

public class FHApplicationSelectionPage extends WizardPage implements SelectionChangeCallback {
	

	private FeedHenryApplicationSelector appSelector;
	private final IStructuredSelection currentSelection;
	private DirectorySelectionGroup destinationDirectoryGroup;
	private WorkingSetGroup workingSetGroup;

	protected FHApplicationSelectionPage(IStructuredSelection selection) {
		super("FeedHenry Application Selection Page");
		currentSelection = selection;
		setTitle("FeedHenry Application Import");
		setDescription("Import a Cordova application from FeedHenry");
	}

	@Override
	public void createControl(Composite parent){
		initializeDialogUnits(parent);
		final Composite workArea = new Composite(parent, SWT.NONE);
		setControl(workArea);
		GridLayoutFactory.fillDefaults().applyTo(workArea);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(workArea);
		
		appSelector = new FeedHenryApplicationSelector();
		appSelector.setProjectsFilter(new IFilter() {
			
			@Override
			public boolean select(Object toTest) {
				if( !(toTest instanceof FeedHenryApplication) ){
					return false;
				}
				FeedHenryApplication app = (FeedHenryApplication) toTest;
				if(app.getType().equals(FeedHenryApplication.APP_TYPE_CORDOVA_ADVANCED)){
					return true;
				}
				return false;
			}
		}).setLabel("Select applications:").
			setSelectionChangeCallback(this).
			setDisabledProjectsFilter(new IFilter() {
				
				@Override
				public boolean select(Object toTest) {
					if( !(toTest instanceof FeedHenryApplication) ){
						return false;
					}
					FeedHenryApplication app = (FeedHenryApplication) toTest;
					return app.findEclipseProject() != null;
				}
			}).
			createSelectorUI(workArea);
		

		destinationDirectoryGroup = new DirectorySelectionGroup(workArea, SWT.NONE);
		destinationDirectoryGroup.setText("Destination:");
		GridDataFactory.fillDefaults().applyTo(destinationDirectoryGroup);
		destinationDirectoryGroup.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setPageComplete(isValid());
			}
		});
		String userHome = System.getProperty("user.home");
		if(userHome != null){
			Path p = new Path(userHome);
			destinationDirectoryGroup.setDefaultValue(p.append("/git").toString());
		}

    	createWorkingSetGroup();
    	appSelector.retrieveProjects(getContainer());
	
		restoreWidgetValues();
		setPageComplete(false);
		Dialog.applyDialogFont(workArea);
	}

	public List<FeedHenryApplication> getSelectedApplications(){
		return appSelector.getSelectedApplications();
	}
	
	public String getWorkingPath(){
		return destinationDirectoryGroup.getValue();
	}
	
	public IWorkingSet[] getSelectedWorkingSets(){
		return workingSetGroup.getSelectedWorkingSets();
	}

	@Override
	public void selectionChanged(List<FeedHenryApplication> newSelection) {
		setPageComplete(isValid());
	}
	
	private void createWorkingSetGroup() {
		String[] workingSetIds = new String[] {"org.eclipse.ui.resourceWorkingSetPage",  //$NON-NLS-1$
		"org.eclipse.wst.jsdt.ui.JavaWorkingSetPage"};  //$NON-NLS-1$
    	workingSetGroup = new WorkingSetGroup((Composite)getControl(), currentSelection, workingSetIds);
	}
	
	private boolean isValid(){
		List<FeedHenryApplication> selected = getSelectedApplications();
		if(selected == null || selected.isEmpty()){
			setErrorMessage("Please select a project to import");
			return false;
		}
		String dest = getWorkingPath();
		if(dest== null || dest.isEmpty() ){
			setErrorMessage("Specify a destination directory");
			return false;
		}
		if( !DirectorySelectionGroup.isValidDirectory(new File(dest))){
			setErrorMessage(NLS.bind("{0} is not a valid directory", dest));
			return false;
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		for (FeedHenryApplication app : selected) {
			if(app.findEclipseProject() != null ){
				setErrorMessage(NLS.bind("Application {0} already exist in the workspace",app.getTitle()));
				return false;
			}
			final IStatus nameStatus= workspace.validateName(app.getEclipseProjectName(), IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				return false;
			}
			final IProject handle= workspace.getRoot().getProject(app.getEclipseProjectName());
			IPath prjPath = new Path(dest);
			prjPath = prjPath.append(app.getEclipseProjectName());
			
			File prjDirFile = prjPath.toFile();
			if(prjDirFile.isDirectory() && prjDirFile.list().length > 0){
				setErrorMessage(NLS.bind("\"{0}\" directory already exists and is not empty",prjPath.toString()));
				return false;
			}
			final IStatus locationStatus= workspace.validateProjectLocation(handle, prjPath);
			if (!locationStatus.isOK()) {
				setErrorMessage(locationStatus.getMessage());
				return false;
			}
		}
			
		setErrorMessage(null);
		return true;
	}
	
	private void restoreWidgetValues() {
		destinationDirectoryGroup.restoreHistory(getDialogSettings());
		
	}
	
	void saveWidgetValues(){
		destinationDirectoryGroup.saveHistory(getDialogSettings());
	}

}
