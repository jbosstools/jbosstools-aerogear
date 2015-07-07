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

package org.jboss.tools.feedhenry.ui.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.jboss.tools.feedhenry.ui.FHPlugin;

/**
 * Integrates the Feedhenry SDK to a given project by adding 
 * feedhenry.js and fhconfig.js files to the project.
 * 
 * <p>Supports cordova applications only</p>
 * 
 */
public class IntegrateFeedhenrySDKOperation extends WorkspaceModifyOperation {

	private final IProject project;
	
	public IntegrateFeedhenrySDKOperation(IProject project) {
		super(project);
		this.project = project;
	}
	
	@Override
	protected void execute(IProgressMonitor monitor)
			throws CoreException, InvocationTargetException, InterruptedException {
		monitor.beginTask("Integrate SDK to Project ", 100);
		if(canIntegrate()){
			IFolder wwwFolder = project.getFolder("www");
			IFile feedhenryJSFile = wwwFolder.getFile("feedhenry.js");
			IFile fhconfigFile = wwwFolder.getFile("fhconfig.json");
			if(!fhconfigFile.exists()){
				integrateFHConfigFile(fhconfigFile, monitor);
			}
			if(!feedhenryJSFile.exists()){
				integrateFeedhenryJSFile(feedhenryJSFile,monitor);
			}
		}
		monitor.done();
		
	}
	
	private void integrateFHConfigFile(IFile file,IProgressMonitor monitor) throws CoreException{
		SubMonitor sm = SubMonitor.convert(monitor,100);
		try {
			InputStream stream = FileLocator.openStream(FHPlugin.getDefault().getBundle(), new Path("/res/templates/fhconfig.json"), false);
			sm.worked(10);
			file.create(stream, true, sm.newChild(90));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, 
					NLS.bind("Unable to copy fhconfig.json template to project {0}",project.getName()),e));
		}
		finally {
			sm.done();
		}
	}
	
	private void integrateFeedhenryJSFile(IFile file, IProgressMonitor monitor) throws CoreException{
		SubMonitor sm = SubMonitor.convert(monitor,100);
		try{
			InputStream stream = FileLocator.openStream(FHPlugin.getDefault().getBundle(), new Path("/res/templates/feedhenry.js"), false);
			sm.worked(10);
			file.create(stream, true, sm.newChild(90));
		}catch(IOException e){
			throw new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, 
					NLS.bind("Unable to copy feedhenry.js to project {0}",project.getName()),e));

		}
		finally{
			sm.done();
		}
		
	}
	
	private boolean canIntegrate(){
		IFolder wwwFolder = project.getFolder("www");
		return wwwFolder.isAccessible();
	}

}
