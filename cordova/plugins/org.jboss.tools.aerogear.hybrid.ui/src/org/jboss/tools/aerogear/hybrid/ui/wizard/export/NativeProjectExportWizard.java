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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractProjectGeneratorDelegate;

public class NativeProjectExportWizard extends Wizard implements IExportWizard {

	private NativeProjectDestinationPage pageOne;
	private IStructuredSelection initialSelection;
	
	public NativeProjectExportWizard() {
		setWindowTitle("Export Native Platform Project");
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initialSelection = selection;
	}

	@Override
	public boolean performFinish() {
		List<ProjectGenerator>generators = pageOne.getSelectedPlatforms();
		
		List<HybridProject> projects = pageOne.getSelectedProjects();
		ArrayList<AbstractProjectGeneratorDelegate> delegates = new ArrayList<AbstractProjectGeneratorDelegate>();
		
		//Collect delegates
		for (HybridProject project : projects) {
			for (ProjectGenerator generator: generators) {
				try{
					AbstractProjectGeneratorDelegate dlg = generator.createDelegate(project.getProject(), new File(pageOne.getDestinationDirectory(),project.getProject().getName()));
					delegates.add(dlg);
					
				}catch(CoreException e){
					HybridCore.log(IStatus.ERROR, "Error creating project generator delegate for " +generator.getPlatform(), e);
				}
			}
		}
		//Run all the delegates
		for (AbstractProjectGeneratorDelegate delegate : delegates) {
			try {
				delegate.generateNow(new NullProgressMonitor());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void addPages() {
		super.addPages();
		pageOne = new NativeProjectDestinationPage(getWindowTitle(), initialSelection);
		pageOne.setTitle("Export Native Platform Project");
		pageOne.setDescription("Exports a platform native project from a hybrid mobile project type");
		addPage( pageOne );
	}
	
}
