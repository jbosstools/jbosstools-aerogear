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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.ui.util.HybridProjectContentProvider;
import org.jboss.tools.aerogear.hybrid.ui.util.HybridProjectLabelProvider;
import org.eclipse.swt.layout.RowLayout;

public class NativeProjectDestinationPage extends WizardPage implements IOverwriteQuery{

	private static int DESTINATION_HISTORY_LENGTH = 5;

	private Combo destinationCombo;
	private String[] destinationHistory = new String[0];
	private Table platformTable;
	private CheckboxTableViewer platformsTableViewer;
	private Table prjTable;
	private IStructuredSelection initialSelection;
	
	private CheckboxTableViewer projectsTableViewer;

	protected NativeProjectDestinationPage(String pageName, IStructuredSelection initialSelection) {
		super(pageName);
		this.initialSelection = initialSelection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Group grpSelectProjects = new Group(container, SWT.NONE);
		grpSelectProjects.setLayout(new GridLayout(2, false));
		grpSelectProjects.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		grpSelectProjects.setText("Select Projects:");
		
		projectsTableViewer = CheckboxTableViewer.newCheckList(grpSelectProjects, SWT.BORDER | SWT.FULL_SELECTION);
		prjTable = projectsTableViewer.getTable();
		prjTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite projectButtons = new Composite(grpSelectProjects, SWT.NONE);
		projectButtons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		RowLayout rl_projectButtons = new RowLayout(SWT.VERTICAL);
		rl_projectButtons.center = true;
		rl_projectButtons.fill = true;
		rl_projectButtons.justify = true;
		rl_projectButtons.pack = false;
		projectButtons.setLayout(rl_projectButtons);
		
		Button btnSelectAll = new Button(projectButtons, SWT.NONE);
		btnSelectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectsTableViewer.setAllChecked(true);
				setPageComplete(validatePage());
			}
		});
		btnSelectAll.setText("Select All");
		
		Button btnDeselectAll = new Button(projectButtons, SWT.NONE);
		btnDeselectAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectsTableViewer.setAllChecked(false);
				setPageComplete(validatePage());
			}
		});
		btnDeselectAll.setText("Deselect All");
		projectsTableViewer.setContentProvider(new HybridProjectContentProvider());
		projectsTableViewer.setLabelProvider(new HybridProjectLabelProvider());
		projectsTableViewer.setInput(HybridCore.getHybridProjects());
		
		Group grpAvailablePlatforms = new Group(container, SWT.NONE);
		grpAvailablePlatforms.setLayout(new GridLayout(2, false));
		grpAvailablePlatforms.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		grpAvailablePlatforms.setText("Select Platforms:");
		
		platformsTableViewer = CheckboxTableViewer.newCheckList(grpAvailablePlatforms, SWT.BORDER | SWT.FULL_SELECTION);
		platformsTableViewer.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				setPageComplete(validatePage());
			}
		});
		platformTable = platformsTableViewer.getTable();
		platformTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite platformButtons = new Composite(grpAvailablePlatforms, SWT.NONE);
		platformButtons.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		RowLayout rl_platformButtons = new RowLayout(SWT.VERTICAL);
		rl_platformButtons.justify = true;
		rl_platformButtons.fill = true;
		rl_platformButtons.center = true;
		platformButtons.setLayout(rl_platformButtons);
		
		Button btnSelectAll_1 = new Button(platformButtons, SWT.NONE);
		btnSelectAll_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				platformsTableViewer.setAllChecked(true);
				setPageComplete(validatePage());
			}
		});
		btnSelectAll_1.setText("Select All");
		
		Button btnDeselectAll_1 = new Button(platformButtons, SWT.NONE);
		btnDeselectAll_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				platformsTableViewer.setAllChecked(false);
				setPageComplete(validatePage());
			}
		});
		btnDeselectAll_1.setText("Deselect All");
		platformsTableViewer.setContentProvider(new ProjectGeneratorContentProvider());
		platformsTableViewer.setLabelProvider(new ProjectGeneratorLabelProvider());
		platformsTableViewer.setInput(HybridCore.getPlatformProjectGenerators());
		
		Group grpDestination = new Group(container, SWT.NONE);
		grpDestination.setText("Destination:");
		grpDestination.setLayout(new GridLayout(3, false));
		grpDestination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblDirectory = new Label(grpDestination, SWT.NONE);
		lblDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDirectory.setText("Directory:");
		
		destinationCombo = new Combo(grpDestination, SWT.NONE);
		destinationCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnBrowse = new Button(grpDestination, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				chooseDirectory();
			}
		});
		btnBrowse.setText("Browse...");
		setupFromInitialSelection();
		setPageComplete(validatePage());
		
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
						projectsTableViewer.setChecked(hybrid, true);
					}
				}
			}
		}
	}

	@Override
	public String queryOverwrite(String pathString) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void chooseDirectory(){
		DirectoryDialog dialog = new DirectoryDialog(this.getShell() );
		dialog.setText("Select Destination");
		dialog.setMessage("Select a destination directory");
		String directory = dialog.open();
		if(directory != null ){
			this.addToDestinationCombo(directory);
			setPageComplete(validatePage());
		}
	}
	
	private void addToDestinationCombo(String directory){
			ArrayList<String> l = new ArrayList<String>(Arrays.asList(destinationHistory));
			l.remove(directory);
			l.add(directory);
			if(l.size()>DESTINATION_HISTORY_LENGTH){
				l.remove(DESTINATION_HISTORY_LENGTH);
			}
			destinationHistory = l.toArray(new String[l.size()]);
			destinationCombo.removeAll();
			for (int i = 0; i < destinationHistory.length; i++) {
				destinationCombo.add(destinationHistory[i], i);
			}
			if(destinationHistory.length>0)
				destinationCombo.select(0);
	}
 	
	private boolean validatePage(){
		TableItem[] items = projectsTableViewer.getTable().getItems();
		if(items== null || items.length <1 ){
			setMessage("No suitable projects are available", ERROR);
			return false;
		}
		Object[] selectedProjects = projectsTableViewer.getCheckedElements();
		if(selectedProjects.length <1 ){
			setMessage("No projects are selected. Please select projects to export" ,ERROR);
			return false;
		}
		Object[] selection = platformsTableViewer.getCheckedElements();
		if(selection.length < 1){
			setMessage("No platform is selected. Please select a platform", ERROR);
			return false;
		}
		String destination = destinationCombo.getText();
		if(destination == null || destination.isEmpty()){
			setMessage("Specify a destination directory", ERROR);
			return false;
		}
		File dstFile = new File(destination);
		if(!isValidDirectory(dstFile)){
			setMessage("Specified destination is not a valid directory",ERROR );
			return false;
		}
		setMessage(null, NONE);
		return true;	
	}

	private boolean isValidDirectory(File dstFile) {
		try {
			if(dstFile.getCanonicalPath().isEmpty()){
				return false;
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public List<ProjectGenerator> getSelectedPlatforms(){
		Object[] checked = platformsTableViewer.getCheckedElements();
		ArrayList<ProjectGenerator> list = new ArrayList<ProjectGenerator>(checked.length);
		for (int i = 0; i < checked.length; i++) {
			list.add((ProjectGenerator)checked[i]);
		}
		return list;
	}
	
	public String getDestinationDirectory(){
		return destinationCombo.getText();
	}
	
	public List<HybridProject> getSelectedProjects(){
		Object[] checked = projectsTableViewer.getCheckedElements();
		ArrayList<HybridProject> list = new ArrayList<HybridProject>(checked.length);
		for (int i = 0; i < checked.length; i++) {
			list.add((HybridProject)checked[i]);
		}
		return list;
	}
}
