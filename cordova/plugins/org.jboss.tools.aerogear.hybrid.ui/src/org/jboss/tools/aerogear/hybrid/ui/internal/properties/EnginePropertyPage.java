/*******************************************************************************
 * Copyright (c) 2013,2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.internal.properties;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileEngine;
import org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPlugin;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.jboss.tools.aerogear.hybrid.ui.internal.engine.AvailableCordovaEnginesSection;

public class EnginePropertyPage extends PropertyPage {
	
	private AvailableCordovaEnginesSection engineSection;
	
	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(control);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(control);
		
		engineSection = new AvailableCordovaEnginesSection();
		engineSection.createControl(control);
		
		engineSection.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setValid(isValid());
			}
		});
		noDefaultAndApplyButton();
		HybridProject hybridProject = getProject();
		HybridMobileEngine activeEngine = hybridProject.getActiveEngine();
		if(activeEngine != null){
			engineSection.setSelection(new StructuredSelection(activeEngine));
		}
		return control;
	}

	private HybridProject getProject() {
		IProject project = (IProject) getElement().getAdapter(IProject.class);
		HybridProject hybridProject = HybridProject.getHybridProject(project);
		return hybridProject;
	}
	
	@Override
	public boolean isValid() {
		if(engineSection.getSelection().isEmpty()){
			setErrorMessage("No engines have been selected");
			return false;
		}
		IStructuredSelection sel = (IStructuredSelection) engineSection.getSelection();
		HybridMobileEngine engine = (HybridMobileEngine) sel.getFirstElement();
		IStatus consistentStatus = engine.isLibraryConsistent();
		if(!consistentStatus.isOK()){
			setMessage(consistentStatus.getMessage(),consistentStatus.getSeverity());
			return consistentStatus.getSeverity() != IStatus.ERROR;
		}
		try {
			List<CordovaPlugin> installedPlugins = getProject().getPluginManager().getInstalledPlugins();
			for (CordovaPlugin cordovaPlugin : installedPlugins) {
				IStatus status = cordovaPlugin.isEngineCompatible(engine);
				if( !status.isOK())
				{
					setMessage(status.getMessage(), status.getSeverity());
					return status.getSeverity() != IStatus.ERROR;
				}
			}
		} catch (CoreException e) {
			HybridUI.log(IStatus.WARNING, "Error while checking engine and plug-in compatability ",  e);
		}
		setMessage(null);
		setErrorMessage(null);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		IStructuredSelection selection = (IStructuredSelection)engineSection.getSelection();
		HybridMobileEngine engine = (HybridMobileEngine) selection.getFirstElement();
		try {
			getProject().updateActiveEngine(engine);
		} catch (CoreException e) {
			ErrorDialog.openError(getShell(), 
					"Hybrid Mobile Engine Update failed", "Unable to update the active engine for the project "+ getProject().getProject().getName(), 
					e.getStatus());
			return false;
		}
		return true;
	}

}