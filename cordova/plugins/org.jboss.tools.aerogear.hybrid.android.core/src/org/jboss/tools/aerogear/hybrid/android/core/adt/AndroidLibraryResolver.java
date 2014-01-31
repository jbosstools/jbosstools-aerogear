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
package org.jboss.tools.aerogear.hybrid.android.core.adt;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileLibraryResolver;
import org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils;
import org.jboss.tools.aerogear.hybrid.core.internal.util.HybridMobileStatus;

public class AndroidLibraryResolver extends
		HybridMobileLibraryResolver {

	public static final String DIR_LIBS = "libs";
	public static final String DIR_RES = "res";
	public static final String DIR_SRC = "src";
	
	public static final String FILE_JAR_CORDOVA = "cordova.jar";
	public static final String FILE_XML_ANDROIDMANIFEST = "AndroidManifest.xml";

	HashMap<IPath, URL> files = new HashMap<IPath, URL>();
	
	private void initFiles() {
		Assert.isNotNull(libraryRoot, "Library resolver is not initialized. Call init before accessing any other functions.");
		IPath templatePrjRoot = libraryRoot.append("bin/templates/project");
		IPath cordovaJar = libraryRoot.append("framework").append(NLS.bind("cordova-{0}.jar",version));
		files.put(new Path(DIR_LIBS +"/" + FILE_JAR_CORDOVA), getEngineFile(cordovaJar));	
		files.put(new Path(DIR_RES),getEngineFile(templatePrjRoot.append(DIR_RES)));
		files.put(new Path(FILE_XML_ANDROIDMANIFEST), getEngineFile(templatePrjRoot.append(FILE_XML_ANDROIDMANIFEST)));
		files.put(new Path(DIR_SRC).append(VAR_PACKAGE_NAME.replace('.', '/')).append(VAR_APP_NAME+".java"), 
				getEngineFile(templatePrjRoot.append("Activity.java")));
		files.put(new Path("assets/www/cordova.js"), getEngineFile(libraryRoot.append("framework/assets/www/cordova.js")));
		
	}


	@Override
	public URL getTemplateFile(IPath destination) {
		if(files.isEmpty()) initFiles();
		Assert.isNotNull(destination);
		Assert.isTrue(!destination.isAbsolute());
		return files.get(destination);
	}

	@Override
	public IStatus isLibraryConsistent() {
		if(needsPreCompilation()){
			return new Status(IStatus.WARNING, HybridCore.PLUGIN_ID, "Library for Android needs to be precompiled");
		}
		if(files.isEmpty()) initFiles();
		Iterator<IPath> paths = files.keySet().iterator();
		while (paths.hasNext()) {
			IPath key = paths.next();
			URL url = files.get(key);
			if(url != null  ){
				File file = new File(url.getFile());
				if( file.exists()){
					continue;
				}
			}
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, NLS.bind("Library for Android platform is not compatible with this tool. File for path {0} is missing.",key.toString()));
		}
		return Status.OK_STATUS;
	}
	
	public void preCompile(IProgressMonitor monitor) throws CoreException{
		AndroidSDK sdk = AndroidProjectUtils.selectBestValidTarget();
		AndroidSDKManager sdkManager = AndroidSDKManager.getManager();
		File projectDir = libraryRoot.append("framework").toFile();
		if(!projectDir.isDirectory()){
			throw new CoreException(HybridMobileStatus.newMissingEngineStatus(null, "Library for the Active Hybrid Mobile Engine for Android is incomplete. No framework directory is present."));
		}
		sdkManager.updateProject(sdk, null, true, projectDir,monitor);
		BuildDelegate buildDelegate = new BuildDelegate();
		if(monitor.isCanceled())
			return;
		buildDelegate.buildLibraryProject(projectDir, monitor);
	}
	
	public boolean needsPreCompilation(){
		IPath cordovaJar = libraryRoot.append("framework").append(NLS.bind("cordova-{0}.jar",version));
		return !cordovaJar.toFile().exists();
	}

	private URL getEngineFile(IPath path){
		File file = path.toFile();
		if(!file.exists()){
			HybridCore.log(IStatus.ERROR, "missing Android engine file " + file.toString(), null );
		}
		return FileUtils.toURL(file);
	}

}
