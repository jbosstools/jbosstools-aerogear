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
package org.jboss.tools.aerogear.hybrid.engine.internal.cordova;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileTemplateResolver;
import org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils;

public class IosTemplateResolver extends HybridMobileTemplateResolver {
	
	private IPath library;
	private String version;
	
	public IosTemplateResolver(IPath libraryRoot, String version) {
		super(libraryRoot);
		this.library= libraryRoot;
		this.version = version;
	}

	HashMap<IPath, URL> files = new HashMap<IPath, URL>();
	
	

	@Override
	public void initialize(HybridProject project) {
		if(!files.isEmpty())
			files.clear();
		initFiles(project);
	}
	
	private void initFiles(HybridProject project) {
		String projectName = project.getBuildArtifactAppName();
		IPath distroRoot = getSelectedDistroRoot();
		IPath templatePrjRoot = distroRoot.append("bin/templates/project");
		if("3.0.0".equals(version)){
			files.put(new Path("cordova"), getEngineFile(distroRoot.append("bin/templates/project/cordova/")));
		}
		else{
			files.put(new Path("cordova"), getEngineFile(distroRoot.append("bin/templates/scripts/cordova/")));
		}
		files.put(new Path(projectName), getEngineFile(templatePrjRoot.append("__TESTING__")));
		files.put(new Path(projectName+"/"+projectName+"-Info.plist"), getEngineFile(templatePrjRoot.append("__TESTING__/__TESTING__-Info.plist")));
		files.put(new Path(projectName+"/"+projectName+"-Prefix.pch") , getEngineFile(templatePrjRoot.append("__TESTING__/__TESTING__-Prefix.pch")));
		files.put(new Path(projectName+".xcodeproj/project.pbxproj"), getEngineFile(templatePrjRoot.append("__TESTING__.xcodeproj/project.pbxproj")));
		files.put(new Path(projectName+"/Classes/AppDelegate.h"), getEngineFile(templatePrjRoot.append("__TESTING__/Classes/AppDelegate.h")));
		files.put(new Path(projectName+"/Classes/AppDelegate.m"), getEngineFile(templatePrjRoot.append("__TESTING__/Classes/AppDelegate.m")));
		files.put(new Path(projectName+"/Classes/MainViewController.h"), getEngineFile(templatePrjRoot.append("__TESTING__/Classes/MainViewController.h")));
		files.put(new Path(projectName+"/Classes/MainViewController.m"), getEngineFile(templatePrjRoot.append("__TESTING__/Classes/MainViewController.m")));
		files.put(new Path(projectName+"/main.m"), getEngineFile(templatePrjRoot.append("__TESTING__/main.m")));
		files.put(new Path("CordovaLib"), getEngineFile(distroRoot.append("CordovaLib")));
	}

	@Override
	public URL getTemplateFile(IPath destination) {
		Assert.isNotNull(destination);
		Assert.isTrue(!destination.isAbsolute());
		return files.get(destination);
	}
	
	
	private URL getEngineFile(IPath path){
		File file = path.toFile();
		if(!file.exists()){
			HybridCore.log(IStatus.ERROR, "missing iOS engine file " + file.toString(), null );
		}
		return FileUtils.toURL(file);
	}
	
	private IPath getSelectedDistroRoot(){
		return library;
	}
 
}
