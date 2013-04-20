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
package org.jboss.tools.aerogear.android.ui;

import static org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidLaunchConstants.ATTR_AVD_NAME;
import static org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidLaunchConstants.ATTR_LOGCAT_FILTER;
import static org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidLaunchConstants.VAL_DEFAULT_LOGCAT_FILTER;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidSDKManager;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectLaunchConfigConstants;

public class AndroidSimOptionsTab extends AbstractLaunchConfigurationTab {
	private Text textProject;
	private Text logFilterTxt;
	private Listener dirtyListener;
	private Combo AVDCombo;
	
	private class DirtyListener implements Listener{
		@Override
		public void handleEvent(Event event) {
			setDirty(true);
			updateLaunchConfigurationDialog();
		}
	}
	
	private class ProjectLabelProvider extends LabelProvider{
		private ImageDescriptor DESC_OBJ_PROJECT;
		{
			DESC_OBJ_PROJECT = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
		}
		@Override
		public String getText(Object element) {
			if (element instanceof HybridProject ){
				HybridProject hp = (HybridProject) element;
				return hp.getProject().getName();
			}
			return null;
		}
		
		@Override
		public Image getImage(Object element) {
			return DESC_OBJ_PROJECT.createImage();
		}
	}
	
	public AndroidSimOptionsTab() {
		this.dirtyListener = new DirtyListener();
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		comp.setLayout(new GridLayout(1, false));
		Group grpProject = new Group(comp, SWT.NONE);
		grpProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpProject.setText("Project");
		grpProject.setLayout(new GridLayout(3, false));
		
		Label lblProject = new Label(grpProject, SWT.NONE);
		lblProject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblProject.setText("Project:");
		
		textProject = new Text(grpProject, SWT.BORDER);
		textProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textProject.addListener(SWT.Modify, dirtyListener);
		
		Button btnProjectBrowse = new Button(grpProject, SWT.NONE);
		btnProjectBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementListSelectionDialog es = new ElementListSelectionDialog(getShell(), new ProjectLabelProvider());
				es.setElements(HybridCore.getHybridProjects().toArray());
				if (es.open() == Window.OK) {			
					HybridProject project = (HybridProject) es.getFirstResult();
					textProject.setText(project.getProject().getName());
				}		
			}
		});
		btnProjectBrowse.setText("Browse...");
		
		Group grpEmulator = new Group(comp, SWT.NONE);
		grpEmulator.setLayout(new GridLayout(2, false));
		grpEmulator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpEmulator.setText("Emulator");
		
		Label lblVirtualDeviceavd = new Label(grpEmulator, SWT.NONE);
		lblVirtualDeviceavd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVirtualDeviceavd.setText("Virtual Device (AVD):");
		
		AVDCombo = new Combo(grpEmulator, SWT.NONE);
		AVDCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		AVDCombo.add("", 0);
		AVDCombo.addListener(SWT.Selection, dirtyListener);
		
		
		Label lblLogFilter = new Label(grpEmulator, SWT.NONE);
		lblLogFilter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLogFilter.setText("Log Filter:");
		
		logFilterTxt = new Text(grpEmulator, SWT.BORDER);
		logFilterTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		logFilterTxt.addListener(SWT.Modify, dirtyListener);
		AndroidSDKManager sdk = new AndroidSDKManager();
		try {
			List<String> avds = sdk.listAVDs();
			for (String string : avds) {
				AVDCombo.add(string);
			}
		} catch (CoreException e1) {
			AVDCombo.removeAll();// let it fallback to default
		}
	}

	@Override
	public String getName() {
		return "Emulator";
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		String projectName =null;
		try {
			projectName = configuration.getAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, (String)null);
		} catch (CoreException e) {
			//not handled
		}
		if(projectName != null){
			textProject.setText(projectName);
		}
		
		try{
			String filter = configuration.getAttribute(ATTR_LOGCAT_FILTER, VAL_DEFAULT_LOGCAT_FILTER);
			logFilterTxt.setText(filter);
		}catch(CoreException e){
			logFilterTxt.setText("");
		}
		
		try{
			String avd = configuration.getAttribute(ATTR_AVD_NAME, "");
			int index = AVDCombo.indexOf(avd);
			if(index <0)
				index = 0;
			AVDCombo.select(index);
		}
		catch(CoreException e){
			
		}
		
		setDirty(false);
		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(HybridProjectLaunchConfigConstants.ATTR_BUILD_SCOPE, textProject.getText());
		String avd = AVDCombo.getText();
		if (avd != null && !avd.isEmpty()){
			configuration.setAttribute(ATTR_AVD_NAME, avd);
		}
		configuration.setAttribute(ATTR_LOGCAT_FILTER, logFilterTxt.getText());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, true);
		configuration.setAttribute(ATTR_LOGCAT_FILTER, VAL_DEFAULT_LOGCAT_FILTER);
	}

}
