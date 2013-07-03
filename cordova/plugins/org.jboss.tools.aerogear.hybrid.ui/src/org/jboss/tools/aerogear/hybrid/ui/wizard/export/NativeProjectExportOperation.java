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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractProjectGeneratorDelegate;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;

public class NativeProjectExportOperation extends WorkspaceModifyOperation {

	private List<AbstractProjectGeneratorDelegate> generators;
	private MultiStatus status;
	

	public NativeProjectExportOperation( List<AbstractProjectGeneratorDelegate> delegates){
		this.generators = delegates;
		status = new MultiStatus(HybridUI.PLUGIN_ID,IStatus.OK, "", null);
	}
	
	@Override
	protected void execute(IProgressMonitor monitor) throws CoreException,
	InvocationTargetException, InterruptedException {
		try{
			int totalWork = generators.size();

			monitor.beginTask("Generate Native Projects", totalWork);
			for (AbstractProjectGeneratorDelegate generator : generators) {
				if(monitor.isCanceled())
					return;
				SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
				runSingle(subMonitor, generator);
			}
		}finally{
			monitor.done();
		}
	}

	private void runSingle(IProgressMonitor monitor,
			AbstractProjectGeneratorDelegate generator) {
		try{
			generator.generateNow(monitor);
		}catch(CoreException e){
			addToStatus(e);
		}
	}
	
	private void addToStatus(CoreException e){
		IStatus status = e.getStatus();
		String message = e.getLocalizedMessage();
		if(message == null || message.isEmpty()){
			message = "Error during native project export operation";
			status = new Status(IStatus.ERROR, HybridUI.PLUGIN_ID, message,e);
		}
		this.status.add(status);
		
	}
	
	public MultiStatus getStatus() {
		return status;
	}
}
