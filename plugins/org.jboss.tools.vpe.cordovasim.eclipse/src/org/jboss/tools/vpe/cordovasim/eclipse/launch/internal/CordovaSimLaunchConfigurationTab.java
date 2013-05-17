/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.WorkingDirectoryBlock;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.CordovaSimLaunchConstants;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 */
@SuppressWarnings("restriction")
public class CordovaSimLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab {

	private Image image = Activator.getImageDescriptor("icons/cordovasim_16.png").createImage();
	private WidgetListener defaultListener = new WidgetListener();
	private Text projectText;
	private Button useDefaultRootFolderCheckbox;
	private Text rootFolderText;
	private Button rootFolderButton;
	private Button useDefaultStartPageCheckbox;
	private Text startPageText;
	private Button startPageButton;
	private Button useDefaultPortCheckbox;
	private Text portText;
	
	public CordovaSimLaunchConfigurationTab() {
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_BOTH);
		((GridLayout) comp.getLayout()).verticalSpacing = 0;
		
		createProjectEditor(comp);
		createRootFolderEditor(comp);
		createStartPageEditor(comp);
		createServerEditor(comp);
				
		setControl(comp);
	}


	private void createProjectEditor(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Project:", 2, 1, GridData.FILL_HORIZONTAL);
		projectText = SWTFactory.createSingleText(group, 1);
		projectText.addModifyListener(defaultListener);
		Button peojectButton = createPushButton(group, "&Browse...", null); 
		peojectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleProjectButtonSelected();				
			}
		});
	}
	
	private void createRootFolderEditor(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Root folder:", 2, 1, GridData.FILL_HORIZONTAL);
		useDefaultRootFolderCheckbox = SWTFactory.createCheckButton(group, "Use default", null, true, 2);
		rootFolderText = SWTFactory.createSingleText(group, 1);
		rootFolderText.addModifyListener(defaultListener);
		rootFolderButton = createPushButton(group, "&Browse...", null); 
		rootFolderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleRootFolderButtonSelected();				
			}
		});
		useDefaultRootFolderCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean useDefaultRootFolder = ((Button) e.widget).getSelection();
				rootFolderText.setEnabled(!useDefaultRootFolder);
				rootFolderButton.setEnabled(!useDefaultRootFolder);
				IProject project = getSelectedProject();
				if (useDefaultRootFolder) {
					setSelectedRootFolder(project, null);
				} else {
					rootFolderText.setEnabled(true);
					rootFolderButton.setEnabled(true);
				}
				updateLaunchConfigurationDialog();
			}
		});
	}
	
	private void createStartPageEditor(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Start page:", 2, 1, GridData.FILL_HORIZONTAL);
		useDefaultStartPageCheckbox = SWTFactory.createCheckButton(group, "Use default", null, true, 2);
		startPageText = SWTFactory.createSingleText(group, 1);
		startPageText.addModifyListener(defaultListener);
		startPageButton = createPushButton(group, "&Browse...", null); 
		startPageButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleStartPageButtonSelected();				
			}
		});
		useDefaultStartPageCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean useDefaultStartPage = ((Button) e.widget).getSelection();
				IProject project = getSelectedProject();
				IContainer rootFolder = getActualRootFolder(project);
				if (useDefaultStartPage) {
					setSelectedStartPage(project, rootFolder, null);
				} else {
					startPageText.setEnabled(true);
					startPageButton.setEnabled(true);
				}
				updateLaunchConfigurationDialog();
			}
		});
	}
	
	private void createServerEditor(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Server port:", 2, 1, GridData.FILL_HORIZONTAL);
		useDefaultPortCheckbox = SWTFactory.createCheckButton(group, "Use default", null, true, 2);
		SWTFactory.createLabel(group, "Port:", 1);
		portText = SWTFactory.createSingleText(group, 1);
		portText.addModifyListener(defaultListener);
		useDefaultPortCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean useDefaultPort = ((Button) e.widget).getSelection();
				setSelectedPort(useDefaultPort ? null : 4400);
			}
		});
	}
	
	protected void handleProjectButtonSelected() {
		IProject project = getSelectedProject();     

		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(),
				WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		dialog.setTitle("Project Selection");
		dialog.setMessage("Select a project to constrain your search.");
		List<IProject> openPojects = new ArrayList<IProject>();
		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (p.isOpen()) {
				openPojects.add(p);
			}
		}
		dialog.setElements(openPojects.toArray(new IProject[0]));
		if (project != null) {
			dialog.setInitialSelections(new IProject[] {project});
		}
		
		dialog.setDialogBoundsSettings(getDialogBoundsSettings(Activator.PLUGIN_ID + ".PROJECT_DIALOG"),
				Dialog.DIALOG_PERSISTSIZE);
		
		dialog.open();
		IProject newProject = (IProject) dialog.getFirstResult();
		if (newProject != null) {
			setSelectedProject(newProject);
		}
	}
	
	protected void handleRootFolderButtonSelected() {
		final IProject project = getSelectedProject();      
		IContainer rootFolder = getActualRootFolder(project);
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setTitle("Root Folder Selection");
		dialog.setMessage("&Choose the folder to be the root:");
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setDialogBoundsSettings(getDialogBoundsSettings(Activator.PLUGIN_ID + ".ROOT_FOLDER_LOCATION_DIALOG"),
				Dialog.DIALOG_PERSISTSIZE);
		dialog.setInitialSelection(rootFolder);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element == project) {
					return true;
				} else if (element instanceof IFolder && CordovaSimLaunchParametersUtil.getRelativePath(project, (IFolder) element) != null) {
					return true;
				} else {
					return false;
				}
			}
		});

		dialog.open();
		Object result = dialog.getFirstResult();	
		if (result instanceof IContainer) {
			IContainer newRootContainer = (IContainer)result;
			setSelectedRootFolder(project, newRootContainer);
			String containerName = CordovaSimLaunchParametersUtil.getRelativePath(project, newRootContainer).toString();
			rootFolderText.setText(containerName);
		}
	}
	
	protected void handleStartPageButtonSelected() {
		IProject project = getSelectedProject();
		IContainer rootFolder = getActualRootFolder(project);
		IResource startPage = getActualStartPage(project, rootFolder);
		
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setTitle("Start Page Selection");
		dialog.setMessage("&Choose the file to be the start page:");
		dialog.setInput(rootFolder);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setDialogBoundsSettings(getDialogBoundsSettings(Activator.PLUGIN_ID + ".ROOT_START_PAGE_LOCATION_DIALOG"),
				Dialog.DIALOG_PERSISTSIZE);
		dialog.setInitialSelection(startPage);
		dialog.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IFile) {
					String extension = ((IFile) element).getFileExtension();
					return "html".equals(extension) || "htm".equals(extension);
				}
				return true;
			}
		});

		dialog.open();
		Object result = dialog.getFirstResult();
		if (result instanceof IFile) {
			IFile newStartPageFile = (IFile) result;
			setSelectedStartPage(project, rootFolder, newStartPageFile);
		}
	}

	/**
	 * Returns the {@link IDialogSettings} for the given id
	 * 
	 * @param id the id of the dialog settings to get
	 * @return the {@link IDialogSettings} to pass into the {@link ContainerSelectionDialog}
	 */
	IDialogSettings getDialogBoundsSettings(String id) {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(id);
		if (section == null) {
			section = settings.addNewSection(id);
		} 
		return section;
	}
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IWorkbenchWindow workbenchWindow = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (workbenchWindow != null) {
			IWorkbenchPage activePage = workbenchWindow.getActivePage();
			if (activePage != null) {
				ISelection selection = activePage.getSelection();
				
				IProject project = CordovaSimLaunchConfigurationAutofillUtil.getProjectToRun(selection);
				CordovaSimLaunchConfigurationAutofillUtil.fillLaunchConfiguraion(configuration, project);
			}
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		IProject project = null;
		try {
			String projectString = configuration.getAttribute(CordovaSimLaunchConstants.PROJECT, (String) null);
			project = CordovaSimLaunchParametersUtil.getProject(projectString);
		} catch (CoreException e) {
		}
		setSelectedProject(project);
		
		IContainer rootFolder = null;
		try {
			String rootFolderString = configuration.getAttribute(CordovaSimLaunchConstants.ROOT_FOLDER, (String) null);
			rootFolder = CordovaSimLaunchParametersUtil.getRootFolder(project, rootFolderString);
		} catch (CoreException e) {
		}
		setSelectedRootFolder(project, rootFolder);
		rootFolder = getActualRootFolder(project);
		IResource startPage = null;
		try {
			String startPageString = configuration.getAttribute(CordovaSimLaunchConstants.START_PAGE, (String) null);
			startPage = CordovaSimLaunchParametersUtil.getStartPage(rootFolder, startPageString);
		} catch (CoreException e) {
		}
		setSelectedStartPage(project, rootFolder, startPage);
		
		Integer port = null;
		try {
			if (configuration.hasAttribute(CordovaSimLaunchConstants.PORT)) {
				port = configuration.getAttribute(CordovaSimLaunchConstants.PORT, 0);				
			}
		} catch (CoreException e) {
		}
		setSelectedPort(port);
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CordovaSimLaunchConstants.PROJECT, projectText.getText());
		
		String rootFolderString = null;
		if (!useDefaultRootFolderCheckbox.getSelection()) {
			rootFolderString = rootFolderText.getText();
		}
		configuration.setAttribute(CordovaSimLaunchConstants.ROOT_FOLDER, rootFolderString);
		
		String startPageString = null;
		if (!useDefaultStartPageCheckbox.getSelection()) {
			startPageString = startPageText.getText();
		}
		configuration.setAttribute(CordovaSimLaunchConstants.START_PAGE, startPageString);
		
		Integer port = getSelectedPort();
		if (port == null) {
			configuration.removeAttribute(CordovaSimLaunchConstants.PORT);
		} else {
			configuration.setAttribute(CordovaSimLaunchConstants.PORT, port);			
		}
	}

	@Override
	public String getName() {
		return "&Main";
	}
	
	@Override
	public Image getImage() {
		return image;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		image.dispose();
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		
		String projectString = projectText.getText();
		IProject project;
		try {
			project = CordovaSimLaunchParametersUtil.validateAndGetProject(projectString);
		} catch (CoreException e) {
			setErrorMessage(e.getStatus().getMessage());
			return false;
		}
		
		IContainer rootFolder;
		if (!useDefaultRootFolderCheckbox.getSelection()) {
			String rootFolderString = rootFolderText.getText();
			try {
				rootFolder = CordovaSimLaunchParametersUtil.validateAndGetRootFolder(project, rootFolderString);
			} catch (CoreException e) {
				setErrorMessage(e.getStatus().getMessage());
				return false;
			}
		} else {
			rootFolder = CordovaSimLaunchParametersUtil.getDefaultRootFolder(project);
			if (rootFolder == null) {
				setErrorMessage("Cannot find default root folder for selected project.");
				return false;
			}
		}
		
		IResource startPage;
		if (!useDefaultStartPageCheckbox.getSelection()) {
			String startPageString = startPageText.getText();
			try {
				startPage = CordovaSimLaunchParametersUtil
						.validateAndGetStartPage(rootFolder, startPageString);
			} catch (CoreException e) {
				setErrorMessage(e.getStatus().getMessage());
				return false;
			}
		} else {
			startPage = CordovaSimLaunchParametersUtil.getDefaultStartPage(project, rootFolder);
			if (startPage == null) {
				setErrorMessage("Cannot find default start page for selected project.");
				return false;				
			}
		}
		
		if (!useDefaultPortCheckbox.getSelection()) { 
			String portString = portText.getText();
			try {
				CordovaSimLaunchParametersUtil.validatePortNumber(portString);
			} catch (CoreException e) {
				setErrorMessage(e.getStatus().getMessage());
				return false;
			}
		}
		
		return true;
	}
	
	private IProject getSelectedProject() {
		String projectString = projectText.getText();
		return CordovaSimLaunchParametersUtil.getProject(projectString);
	}
	
	private void setSelectedProject(IProject project) {
		projectText.setText(project != null ? project.getName() : "");
		IContainer rootFolder = getSelectedRootFolder(project);
		if (rootFolder == null) {
			setSelectedRootFolder(project, null); // restore defaults
			rootFolder = CordovaSimLaunchParametersUtil.getDefaultRootFolder(project);
		}
		if (getSelectedStartPage(project, rootFolder) == null) {
			setSelectedStartPage(project, rootFolder, null);
		}
	}
	
	private IContainer getSelectedRootFolder(IProject project) {
		if (!useDefaultRootFolderCheckbox.getSelection()) {
			String rootFolderString = rootFolderText.getText();
			return CordovaSimLaunchParametersUtil.getRootFolder(project, rootFolderString);
		} else {
			return null;
		}
	}
	
	private void setSelectedRootFolder(IProject project, IContainer rootFolder) {
		boolean useDefaultRootFolder = (rootFolder == null);
		useDefaultRootFolderCheckbox.setSelection(useDefaultRootFolder);
		rootFolderText.setEnabled(!useDefaultRootFolder);
		rootFolderButton.setEnabled(!useDefaultRootFolder);
		
		IContainer actualRootFolder;
		if (useDefaultRootFolder) {
			actualRootFolder = CordovaSimLaunchParametersUtil.getDefaultRootFolder(project);
		} else {
			actualRootFolder = rootFolder;
		}
		IPath rootFolderRelative = CordovaSimLaunchParametersUtil.getRelativePath(project, actualRootFolder);
		rootFolderText.setText(rootFolderRelative != null ? rootFolderRelative.toString() : "");
	}
	
	private IResource getSelectedStartPage(IProject project, IContainer rootFolder) {
		if (useDefaultStartPageCheckbox.getSelection()) {
			return null;
		} else {
			String startPageString = startPageText.getText();
			return CordovaSimLaunchParametersUtil.getStartPage(rootFolder, startPageString);
		}
	}
	
	private void setSelectedStartPage(IProject project, IContainer rootFolder, IResource startPage) {
		boolean useDefaultStartPage = (startPage == null);
		useDefaultStartPageCheckbox.setSelection(useDefaultStartPage);
		startPageText.setEnabled(!useDefaultStartPage);
		startPageButton.setEnabled(!useDefaultStartPage);
		
		IResource actualStartPage;
		if (useDefaultStartPage) {
			actualStartPage = CordovaSimLaunchParametersUtil.getDefaultStartPage(project, rootFolder);
		} else {
			actualStartPage = startPage;
		}
		IPath startPagePath = CordovaSimLaunchParametersUtil.getRelativePath(rootFolder, actualStartPage);
		startPageText.setText(startPagePath != null ? startPagePath.toString() : "");
	}
	
	private IResource getActualStartPage(IProject project, IContainer rootFolder) {
		IResource startPage = getSelectedStartPage(project, rootFolder);
		if (startPage == null) {
			startPage = CordovaSimLaunchParametersUtil.getDefaultStartPage(project, rootFolder);
		}
		return startPage;
	}

	private IContainer getActualRootFolder(IProject project) {
		IContainer rootFolder = getSelectedRootFolder(project);
		if (rootFolder == null) {
			rootFolder = CordovaSimLaunchParametersUtil.getDefaultRootFolder(project);
		}
		return rootFolder;
	}
	
	private Integer getSelectedPort() {
		if (useDefaultPortCheckbox.getSelection()) {
			return null;
		} else {
			int port = 0;
			try {
				port = Integer.parseInt(portText.getText());
			} catch (NumberFormatException e) {
			}
			return port;
		}
	}
	
	private void setSelectedPort(Integer port) {
		boolean useDefaultPort = (port == null);
		
		useDefaultPortCheckbox.setSelection(useDefaultPort);
		portText.setEnabled(!useDefaultPort);
		portText.setText(port != null ? port.toString() : "");
	}
	
	private class WidgetListener implements ModifyListener, SelectionListener {
		
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {/*do nothing*/}
		
		public void widgetSelected(SelectionEvent e) {
			updateLaunchConfigurationDialog();
		}
	}
}

class CordovaSimWorkingDirectoryBlock extends WorkingDirectoryBlock {

	protected CordovaSimWorkingDirectoryBlock() {
		super("org.jboss.tools.vpe.cordovasim.eclipse.launch.internal.rootFolder");
	}

	@Override
	protected IProject getProject(ILaunchConfiguration configuration)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
}
