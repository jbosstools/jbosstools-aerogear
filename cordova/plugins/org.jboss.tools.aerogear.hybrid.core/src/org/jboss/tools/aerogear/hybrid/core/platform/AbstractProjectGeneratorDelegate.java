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
package org.jboss.tools.aerogear.hybrid.core.platform;

import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.toURL;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.directoryCopy;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.osgi.framework.Bundle;
/**
 * Abstract class for all the native project generators delegates.
 * 
 * @author Gorkem Ercan
 *
 */
public abstract class AbstractProjectGeneratorDelegate {

	private final static String ASSEMBLY_ROOT = "/proj_gen/";
	
	private IProject project;
	private File generationRoot;

	/**
	 * Constructs a project generator. 
	 */
	public AbstractProjectGeneratorDelegate(){

	}
	
	/**
	 * Initializes this ProjectGenerator. init must be called before calling the 
	 * generateNow. generationFoler can be null. If generationFolder is null generation folder 
	 * defaults to a folder created under the {@link Bundle} dataFile folder.
	 * 
	 * @param project
	 * @param generationFolder
	 */
	public void init(IProject project, File generationFolder )
	{
		this.project = project;
		this.generationRoot = generationFolder;
		if(generationRoot == null ){
			generationRoot = new File(getTempGenerationDirectory(), project.getName());
		}
	}
	/**
	 * Starts the target platform project generation.
	 * 
	 * @param monitor
	 * @return the location of the generated project
	 * @throws CoreException
	 */
	public File generateNow(IProgressMonitor monitor) throws CoreException{
		if(project == null || generationRoot == null )
			throw new IllegalStateException("Project generator delegate is not initializes properly");
		long start = System.currentTimeMillis();
		try {
			if(!generationRoot.exists() && !generationRoot.mkdirs() ){
				throw new CoreException(new Status(IStatus.ERROR,HybridCore.PLUGIN_ID, "Can not create the destination directory for project generation at "+generationRoot.toString() ));
			}
			monitor.beginTask("Generate Native Project", 40);
			generateNativeFiles();
			monitor.worked(10);
			IFolder folder = getProject().getFolder("/"+PlatformConstants.DIR_WWW);
			if ( !folder.exists() ){
				throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "No www directory. Can not generate target without www directory"));
			}
			File targetWWW = getPlatformWWWDirectory();
			Assert.isNotNull(targetWWW,"Platform implementation must return a file location for www directory");
			if( !targetWWW.exists() && !targetWWW.mkdirs() ){
				throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, " Unable to create www directory for native project "));
			}
			directoryCopy( toURL(folder.getLocation().toFile()), toURL(targetWWW));
			monitor.worked(10);
			folder = getProject().getFolder("/"+ PlatformConstants.DIR_MERGES+"/"+getTargetShortName());
			if (folder.exists()){
				directoryCopy(folder.getLocationURI().toURL() , toURL(targetWWW));
			}
			monitor.worked(10);
			replaceCordovaPlatformFiles();
		}
		catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, " Unable to generate native project ",e));
		}
		finally{
			monitor.done();
		}
		HybridCore.trace(getTargetShortName()+ " project generated in " + Long.toString(System.currentTimeMillis() - start) +" ms.");
		return getDestination();
		
	}
	
	
	/**
	 * Template method to be implemented by the platform implementations. 
	 * Platform implementations should generate native project files 
	 * and the Cordova Library. This method is called before moving the 
	 * web artifacts in www directory.
	 * @throws IOException
	 */
	protected abstract void generateNativeFiles() throws CoreException;
	
	/**
	 * Returns the short name to be used for defining the target platform 
	 * such as <i>ios, android</i> etc.
	 * @return
	 */
	protected abstract String getTargetShortName();
	
	/**
	 * Template method to be implemented by the platform implementations. 
	 * This method is called after the web artifacts are copied to the proper location 
	 * on the generated project to give platform implementation a chance to replace 
	 * platform specific Apache Cordova artifacts such as the cordova.js
	 *  
	 * @throws IOException
	 */
	protected abstract void replaceCordovaPlatformFiles() throws IOException; 
	
	/**
	 * Returns the platform specific location of the www directory. 
	 * This is used to merge and copy project resources to native project.
	 * @return File that points to the www directory on native project
	 */
	protected abstract File getPlatformWWWDirectory();
	
	
	protected File getDestination(){
		return new File(generationRoot,getTargetShortName());
	}
	protected String getProjectName(){
		return project.getName();
	}
	
	protected IProject getProject(){
		return project;
	}
	
	protected File getTempGenerationDirectory(){
		Bundle bundle = HybridCore.getContext().getBundle();
		return bundle.getDataFile(ASSEMBLY_ROOT);
	}
}
