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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaPluginRegistryManager;
import org.jboss.tools.aerogear.hybrid.core.plugin.registry.CordovaRegistryPluginInfo;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.jboss.tools.aerogear.hybrid.ui.wizard.export.DirectorySelectionGroup;

public class CordovaPluginSelectionPage extends WizardPage {

	static final int PLUGIN_SOURCE_REGISTRY =1;
	static final int PLUGIN_SOURCE_GIT =2;
	static final int PLUGIN_SOURCE_DIRECTORY =3;
	
	private HybridProject fixedProject;
	private IStructuredSelection initialSelection;
	private TabFolder tabFolder;
	private TabItem registryTab;
	private CordovaPluginCatalogViewer catalogViewer;
	private TabItem gitTab;
	private TabItem directoryTab;
	private DirectorySelectionGroup destinationDirectoryGroup;
	private Text textProject;
	private Group grpRepositoryUrl;
	private Text gitUrlTxt;
	private final CordovaPluginRegistryManager client = new CordovaPluginRegistryManager(CordovaPluginRegistryManager.DEFAULT_REGISTRY_URL);
	private InstalledPluginFilter installedPluginsFilter;
	

	protected CordovaPluginSelectionPage(String pageName,IStructuredSelection selection) {
		this(pageName);
		this.initialSelection = selection;
	}
	/**
	 * If constructed with a {@link HybridProject} this page does not 
	 * present a project selection UI and all operations are assumed to be
	 * fixed to the passed project.
	 * @param pageName
	 * @param project
	 */
	protected CordovaPluginSelectionPage(String pageName, HybridProject project){
		this(pageName);
		this.fixedProject= project;
	}
	
	private CordovaPluginSelectionPage(String pageName){
		super(pageName);
		setImageDescriptor(HybridUI.getImageDescriptor(HybridUI.PLUGIN_ID, CordovaPluginWizard.IMAGE_WIZBAN));
	}
	

	@SuppressWarnings("restriction")
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		if( fixedProject == null){//let user select a project
			createProjectGroup(container);
		}
		
		tabFolder = new TabFolder(container, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tabFolder);
		
		registryTab = new TabItem(tabFolder, SWT.NONE);
		registryTab.setText("Registry");
		catalogViewer = new CordovaPluginCatalogViewer();
		catalogViewer.createControl(tabFolder);
		catalogViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(validatePage());
			}
		});
		
		
		registryTab.setControl(catalogViewer.getControl());
		
		gitTab = new TabItem(tabFolder, SWT.NONE);
		gitTab.setText("Git");
		
		grpRepositoryUrl = new Group(tabFolder, SWT.NONE);
		grpRepositoryUrl.setText("Repository");
		gitTab.setControl(grpRepositoryUrl);
		grpRepositoryUrl.setLayout(new GridLayout(2, false));
		
		Label lblUrl = new Label(grpRepositoryUrl, SWT.NONE);
		lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUrl.setText("URL:");
		
		gitUrlTxt = new Text(grpRepositoryUrl, SWT.BORDER);
		gitUrlTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		directoryTab = new TabItem(tabFolder, SWT.NONE);
		directoryTab.setText("Directory");
		
		destinationDirectoryGroup = new DirectorySelectionGroup(tabFolder, SWT.NONE);
		destinationDirectoryGroup.setText("Plugin:");
		destinationDirectoryGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		destinationDirectoryGroup.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				setPageComplete(validatePage());
			}


		});
		directoryTab.setControl(destinationDirectoryGroup);
		tabFolder.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				setPageComplete(validatePage());
			}
		});
		setupFromInitialSelection();
		restoreWidgetValues();
		populatePluginInfos();
	}

	private void createProjectGroup(Composite container) {
		Group grpProject = new Group(container, SWT.NONE);
		grpProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpProject.setText("Project");
		grpProject.setLayout(new GridLayout(3, false));
		
		Label lblProject = new Label(grpProject, SWT.NONE);
		lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProject.setText("Project:");
		
		textProject = new Text(grpProject, SWT.BORDER);
		textProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textProject.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				setPageComplete(validatePage());
				if(getPluginSourceType()==PLUGIN_SOURCE_REGISTRY){
					updateInstalledPluginsFilter();
				}
			}
		});
		
		
		Button btnProjectBrowse = new Button(grpProject, SWT.NONE);
		btnProjectBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementListSelectionDialog es = new ElementListSelectionDialog(getShell(), WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
				es.setElements(HybridCore.getHybridProjects().toArray());
				es.setTitle("Project Selection");
				es.setMessage("Select a project to run");
				if (es.open() == Window.OK) {			
					HybridProject project = (HybridProject) es.getFirstResult();
					textProject.setText(project.getProject().getName());
				}		
			}
		});
		btnProjectBrowse.setText("Browse...");
	}

	private void populatePluginInfos() {
		try {
			this.getContainer().run(true, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					if(monitor.isCanceled())
						return;
					List<CordovaRegistryPluginInfo> infos=null;
					try {
						infos = client.retrievePluginInfos(monitor);
						if(infos == null){
							throw new CoreException(new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error while retrieving the Cordova Plugin Registry Catalog"));
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
					final Object[] pluginInfos = infos.toArray();
					Display display = getControl().getDisplay();
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							catalogViewer.getViewer().setInput(pluginInfos);
						}
					});
					monitor.done();
				}
			});
		} catch (InvocationTargetException e1) {
			if (e1.getTargetException() != null) {
				ErrorDialog.openError(getShell(), "Error Retrieving Plugin Catalog",null, 
						new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, "Error while retrieving the catalog for Cordova plugins", e1.getTargetException() ));
			}
		} catch (InterruptedException e1) {
		}
		
	}
	
	private void setupFromInitialSelection() {
		if(initialSelection != null && !initialSelection.isEmpty()){
			Iterator<?> selects = initialSelection.iterator();
			while (selects.hasNext()) {
				Object obj  = selects.next();
				if(obj instanceof IResource ){
					IResource res = (IResource)obj;
					IProject project = res.getProject();
					HybridProject hybrid = HybridProject.getHybridProject(project);
					if(hybrid != null ){
						textProject.setText(project.getName());
					}
				}
			}
		}
	}
	
	@Override
	public IWizardPage getNextPage() {
		if( getSelectedTabItem()!=registryTab){
			return null;
		}
		CordovaPluginWizard wiz = (CordovaPluginWizard) getWizard();
		RegistryConfirmPage confirmPage = wiz.getRegistryConfirmPage();
		confirmPage.setSelectedPlugins(getCheckedCordovaRegistryItems());
		return super.getNextPage();
	}
	
	
	
	public int getPluginSourceType(){
		TabItem selected = getSelectedTabItem();
		if(selected == gitTab )
			return PLUGIN_SOURCE_GIT;
		if(selected == directoryTab )
			return PLUGIN_SOURCE_DIRECTORY;
		return PLUGIN_SOURCE_REGISTRY; // defaults to registry;
	}
	
	public String getSelectedDirectory(){
		return this.destinationDirectoryGroup.getValue();
	}
	
	public String getSpecifiedGitURL(){
		return this.gitUrlTxt.getText();
	}
	
	public String getProjectName(){
		if(fixedProject != null ){
			return fixedProject.getProject().getName();
		}
		return textProject.getText();
	}
	
	private List<CordovaRegistryPluginInfo> getCheckedCordovaRegistryItems(){
		IStructuredSelection selection = catalogViewer.getSelection();
		if(selection == null || selection.isEmpty())
			return Collections.emptyList();
		return selection.toList();
	}
	
	private TabItem getSelectedTabItem(){
		TabItem[] selections = tabFolder.getSelection();
		Assert.isTrue(selections.length>0);
		return selections[0];
	}
	
	private boolean validatePage() {
		//Check project
		if (fixedProject == null) {
			String projectName = textProject.getText();
			if (projectName == null || projectName.isEmpty()) {
				setMessage("Specify a project", ERROR);
				return false;
			}
			List<HybridProject> projects = HybridCore.getHybridProjects();
			boolean projectValid = false;
			for (HybridProject hybridProject : projects) {
				if (hybridProject.getProject().getName().equals(projectName)) {
					projectValid = true;
					break;
				}
			}
			if (!projectValid) {
				setMessage(
						"Specified project is not a valid project for this operation",
						ERROR);
				return false;
			}
		}
		//Now tabs
		
		boolean valid = false;
		switch (getPluginSourceType()) {
		case PLUGIN_SOURCE_DIRECTORY:
			valid = validateDirectroyTab();
			break;
		case PLUGIN_SOURCE_GIT:	
			valid = validateGitTab(); 
			break;
		case PLUGIN_SOURCE_REGISTRY:
			valid = validateRegistryTab();
			break;
		}
		if(valid){
			setMessage(null,NONE);
		}
		return valid;
	}
	
	private boolean validateRegistryTab() {
		List<CordovaRegistryPluginInfo> infos = getCheckedCordovaRegistryItems();
		if (infos.isEmpty()){
			setMessage("Specify Cordova plugin(s) for installation", ERROR);
			return false;
		}
		return true;
	}
	@SuppressWarnings("restriction")
	private void updateInstalledPluginsFilter() {
		HybridProject project = null;
		if(fixedProject != null ){
			project = fixedProject;
		}else{
			project = HybridProject.getHybridProject(textProject.getText());
		}
		
		if(installedPluginsFilter == null ){
			installedPluginsFilter = new InstalledPluginFilter();
			installedPluginsFilter.setProject(project);
			catalogViewer.getViewer().addFilter(installedPluginsFilter);
		}else{
			catalogViewer.getViewer().removeFilter(installedPluginsFilter);
			installedPluginsFilter.setProject(project);
			catalogViewer.getViewer().addFilter(installedPluginsFilter);
		}
	}

	private boolean validateGitTab(){
		String url = gitUrlTxt.getText();
		if( url == null || url.isEmpty() ){
			setMessage("Specify a git repository for fetching the Cordova plugin",ERROR);
			return false;
		}
		try {
			new URI(url);
		} catch (URISyntaxException e) {
			setMessage("Specify a valid address",ERROR);
			return false;
		}
		return true;
	}
	
	private boolean validateDirectroyTab(){
		String directory = destinationDirectoryGroup.getValue();
		if(directory == null || directory.isEmpty() ){
			setMessage("Select the directory for the Cordova plugin",ERROR);
			return false;
		}
		File pluginFile = new File(directory);
		if(!DirectorySelectionGroup.isValidDirectory(pluginFile)){
			setMessage(directory +" is not a valid directory",ERROR);
			return false;
		}
		if(!pluginFile.isDirectory()){
			setMessage("Select an existing directory", ERROR);
			return false;
		}
		File pluginXML = new File(pluginFile, PlatformConstants.FILE_XML_PLUGIN);
		if(!pluginXML.isFile()){
			setMessage("Specified directory is not a valid plugin directory",ERROR);
			return false;
		}
		return true;
		
	}
	
	void saveWidgetValues(){
		IDialogSettings settings = getDialogSettings();
		if(settings != null ){
			destinationDirectoryGroup.saveHistory(settings);
		}
	}
	
	private void restoreWidgetValues(){
		IDialogSettings settings = getDialogSettings();
		if(settings != null ){
			destinationDirectoryGroup.restoreHistory(settings);
		}
	}
}
