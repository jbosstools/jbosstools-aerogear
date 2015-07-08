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
package org.jboss.tools.feedhenry.ui.cordova.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.thym.core.HybridCore;
import org.eclipse.thym.core.HybridProject;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferences;
import org.jboss.tools.feedhenry.ui.internal.FHAppLabelProvider;
import org.jboss.tools.feedhenry.ui.internal.FHApplicationContentProvider;
import org.jboss.tools.feedhenry.ui.internal.FHErrorHandler;
import org.jboss.tools.feedhenry.ui.model.FeedHenry;
import org.jboss.tools.feedhenry.ui.model.FeedHenryException;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;

public class FHProjectSelectionPage extends WizardPage {

	private TableViewer projectList;
	private ComboViewer localProject;
	private Text remoteName;
	private Button useDefaultAppName;
	private Text appName;
	private final IStructuredSelection initialSelection;

	protected FHProjectSelectionPage(IStructuredSelection selection) {
		super("FeedHenry Project Selection Page");
		setTitle("Create FeedHenry Application");
		setDescription("Create a new Application on FeedHenry platform");
		this.initialSelection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		final Composite workArea = new Composite(parent, SWT.NULL);
		setControl(workArea);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(workArea);
		GridDataFactory.fillDefaults().indent(10, 5).grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(workArea); 
		
		final Label localProjectLabel = new Label(workArea, SWT.NULL);
		localProjectLabel.setText("Source project:");
		GridDataFactory.fillDefaults().applyTo(localProjectLabel);
		
		final Combo projectCombo = new Combo(workArea, SWT.SINGLE | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(projectCombo);
		localProject = new ComboViewer(projectCombo);
		localProject.setContentProvider(ArrayContentProvider.getInstance());
		localProject.setInput(HybridCore.getHybridProjects());
		localProject.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof HybridProject ){
					return ((HybridProject)element).getProject().getName();
				}
				return super.getText(element);
			}
		});
		
		final Label projectLabel = new Label(workArea,SWT.NULL);
		projectLabel.setText("Select FeedHenry project: ");
		GridDataFactory.fillDefaults().span(2,1).grab(true, false).align(SWT.BEGINNING, SWT.FILL).applyTo(projectLabel);
		
		projectList = new TableViewer(workArea, SWT.SINGLE | SWT.FULL_SELECTION );
		projectList.setLabelProvider(new FHAppLabelProvider());
		projectList.setContentProvider(new FHApplicationContentProvider());
		GridDataFactory.fillDefaults().span(2, 1).grab(true,false).align(SWT.FILL, SWT.CENTER).hint(SWT.DEFAULT, 100).applyTo(projectList.getTable());
	
		
		Group appNameGrp = new Group(workArea, SWT.NULL);
		appNameGrp.setText("Application name");
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(appNameGrp);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(appNameGrp);
		
		useDefaultAppName = new Button(appNameGrp, SWT.CHECK);
		useDefaultAppName.setText("Use project name");
		useDefaultAppName.setSelection(true);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(useDefaultAppName);
		
		/*Label filler = */new Label(appNameGrp,SWT.NULL);
		
		Label appNameLabel = new Label(appNameGrp, SWT.NULL);
		appNameLabel.setText("Application name:");
		GridDataFactory.fillDefaults().applyTo(appNameLabel);
		
		appName = new Text(appNameGrp, SWT.SINGLE | SWT.BORDER);
		appName.setEnabled(false);
		useDefaultAppName.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				appName.setEnabled(!useDefaultAppName.getSelection());
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(appName);
		
		final Label remoteLabel = new Label(workArea, SWT.NULL);
		remoteLabel.setText("Git remote name:");
		GridDataFactory.fillDefaults().applyTo(remoteLabel);
		
		remoteName = new Text(workArea, SWT.SINGLE |SWT.BORDER);
		remoteName.setText("feedhenry");
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(remoteName);
		
		
		registerListeners();
		this.selectProjectFromSelection();
		Dialog.applyDialogFont(workArea);
		setPageComplete(false);
		
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible){
			getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					retrieveProjects();
				}
			});
		}
		super.setVisible(visible);
	}
	
	private void registerListeners(){
		ISelectionChangedListener validateListener = new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(isValid());
			}
		};
		projectList.addSelectionChangedListener(validateListener);
		localProject.addSelectionChangedListener(validateListener);
		remoteName.addListener(SWT.Modify, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setPageComplete(isValid());
			}
		});
	}
	
	private boolean isValid(){
		if(localProject.getSelection().isEmpty()){
			setErrorMessage("Please specify a project");
			return false;
		}
		if(projectList.getSelection().isEmpty()){
			setErrorMessage("Please specify a FeedHenry project");
			return false;
		}
		if( !useDefaultAppName.getSelection() && appName.getText().isEmpty()){
			setErrorMessage("Please specify an application name");
			return false;
		}
		if(remoteName.getText() == null || remoteName.getText().isEmpty()){
			setErrorMessage("Please specify a Git remote name for FeedHenry repository");
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	private void retrieveProjects() {
		try {
			getContainer().run(false, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					FeedHenry fh = new FeedHenry();
					FHPreferences prefs = FHPreferences.getPreferences();
					String feedHenryURL = prefs.getFeedHenryURL();
					if(feedHenryURL == null || feedHenryURL.isEmpty()){
						CoreException ce = new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, 
								FHErrorHandler.ERROR_INVALID_PREFERENCES, 
								"FeedHenry URL preference is empty. Specify FeedHenry URL on preferences.",null));
						throw new InvocationTargetException(ce);
					}
					try {
						if(monitor.isCanceled()){
							throw new OperationCanceledException();
						}
						projectList.setInput(fh.setFeedHenryURL(new URL(feedHenryURL))
								.setAPIKey(prefs.getUserAPIKey()).listProjects(monitor));
						
					} catch (MalformedURLException e) {
						CoreException ce = new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, FHErrorHandler.ERROR_INVALID_PREFERENCES, 
								NLS.bind("{0} is not a valid URL", feedHenryURL),e));
						throw new InvocationTargetException(ce);
					} catch (FeedHenryException e) {
						CoreException ce = new CoreException(new Status(IStatus.ERROR,FHPlugin.PLUGIN_ID, e.getCode(), e.getMessage(),e));
						throw new InvocationTargetException(ce);
					}	
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			if(FHErrorHandler.handle(e)){
				retrieveProjects();
			}	
		}
	}
	
	private void selectProjectFromSelection(){
		if(this.initialSelection != null && !this.initialSelection.isEmpty()){
			Iterator<?> iterator = initialSelection.iterator();
			while (iterator.hasNext()) {
				Object object = (Object) iterator.next();
				if(object instanceof IResource){
					IProject project = ((IResource)object).getProject();
					HybridProject hp = HybridProject.getHybridProject(project);
					if(hp != null ){
						localProject.setSelection(new StructuredSelection(hp));
					}
				}
			}
		}
	}

	/**
	 * Selected project that exists on the workspace.
	 * @return project
	 */
	HybridProject getHybridProject() {
		IStructuredSelection selection = (IStructuredSelection) localProject.getSelection();
		if(selection.isEmpty()) return null;
		return (HybridProject) selection.getFirstElement();
	}
		
	/**
	 * Selected project on the platform.
	 * @return FeedHenry platform project
	 */
	FeedHenryProject getFeedHenryProject() {
		IStructuredSelection selection = (IStructuredSelection)projectList.getSelection();
		if(selection.isEmpty())return null;
		return (FeedHenryProject) selection.getFirstElement();
	}

	/**
	 * Returns the application name. 
	 * If use default name is selected it 
	 * returns the name from selected project otherwise returns the 
	 * user specified name.
	 * 
	 * @return application name to be used
	 */
	String getApplicationName(){
		if(useDefaultAppName.getSelection()){
			return getHybridProject().getAppName();
		}
		return appName.getText();
	}

	/**
	 * The remote name to be used on git config
	 * 
	 * @return remote name
	 */
	String getRemoteName() {
		return remoteName.getText();
	}

}
