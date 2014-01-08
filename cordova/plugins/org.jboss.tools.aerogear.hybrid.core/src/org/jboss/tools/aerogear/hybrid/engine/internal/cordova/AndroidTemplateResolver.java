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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileTemplateResolver;
import org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils;

public class AndroidTemplateResolver extends
		HybridMobileTemplateResolver {
	
	private IPath library;
	
	public AndroidTemplateResolver(IPath libraryRoot) {
		super(libraryRoot);
		this.library = libraryRoot;
	}

	public static final String DIR_LIBS = "libs";
	public static final String DIR_RES = "res";
	public static final String DIR_SRC = "src";
	
	public static final String FILE_JAR_CORDOVA = "cordova.jar";
	public static final String FILE_XML_ANDROIDMANIFEST = "AndroidManifest.xml";

	HashMap<IPath, URL> files = new HashMap<IPath, URL>();

	
	@Override
	public void initialize(HybridProject project) {
		if(!files.isEmpty())
			files.clear();
		initFiles(project);
	}
	
	private void initFiles(HybridProject project) {
		String name = project.getBuildArtifactAppName();
		String packageName;
		try {
			Widget widgetModel = WidgetModel.getModel(project).getWidgetForRead();
			packageName = widgetModel.getId();
		} catch (CoreException e) {
			//Something is terribly wrong abort
			return;
		}
		IPath distroRoot = getSelectedDistroRoot();
		IPath templatePrjRoot = distroRoot.append("bin/templates/project");
		files.put(new Path(DIR_LIBS +"/" + FILE_JAR_CORDOVA), getEngineFile(distroRoot.append("framwework/cordova.jar")));
		
		files.put(new Path(DIR_RES),getEngineFile(templatePrjRoot.append(DIR_RES)));
		files.put(new Path(FILE_XML_ANDROIDMANIFEST), getEngineFile(templatePrjRoot.append(FILE_XML_ANDROIDMANIFEST)));
		files.put(new Path(DIR_SRC).append(packageName.replace('.', '/')).append(name+".java"), 
				getEngineFile(templatePrjRoot.append("Activity.java")));
		files.put(new Path("assets/www/cordova.js"), getEngineFile(distroRoot.append("framework/assets/www/cordova.js")));
		
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
			HybridCore.log(IStatus.ERROR, "missing Android engine file " + file.toString(), null );
		}
		return FileUtils.toURL(file);
	}
	
	private IPath getSelectedDistroRoot(){
		return library;
	}


}
