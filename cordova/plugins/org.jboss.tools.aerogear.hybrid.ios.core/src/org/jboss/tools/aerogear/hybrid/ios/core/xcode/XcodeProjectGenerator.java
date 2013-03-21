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
package org.jboss.tools.aerogear.hybrid.ios.core.xcode;

import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.directoryCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.fileCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.toURL;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractPlatformProjectGenerator;
import org.jboss.tools.aerogear.hybrid.ios.core.IOSCore;
import org.osgi.framework.Bundle;

public class XcodeProjectGenerator extends AbstractPlatformProjectGenerator{
	
	public XcodeProjectGenerator(IProject project, File generationFolder) {
		super(project, generationFolder);
	}
	
	@Override
	protected void generateNativeFiles() throws IOException{
		
		//copyFilesFromBundle(IOSCore.getContext().getBundle(), "/templates/project/", getDestination());
		generateCordovaLib();
		
		Bundle bundle = IOSCore.getContext().getBundle();
		File destinationDir = getDestination();
		
		File prjdir = new File(destinationDir, getProjectName());
		if( !prjdir.exists() ){//create the project directory
			prjdir.mkdirs();
		}
		directoryCopy(bundle.getEntry("/templates/project/__TESTING__"), toURL(new File(destinationDir, getProjectName()))  );		
		fileCopy(bundle.getEntry("/templates/project/__TESTING__-Info.plist"), toURL(new File(destinationDir, getProjectName()+"/"+getProjectName()+"-Info.plist")));
		fileCopy(bundle.getEntry("/templates/project/__TESTING__-Prefix.pch"), toURL(new File(destinationDir, getProjectName()+"/"+getProjectName()+"-Prefix.pch")));
		directoryCopy(bundle.getEntry("/templates/project/__TESTING__.xcodeproj"), toURL(new File(destinationDir, getProjectName()+".xcodeproj")));	
		
		processFile(new File(getDestination(),getProjectName()+".xcodeproj/project.pbxproj"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/Classes/AppDelegate.h"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/Classes/AppDelegate.m"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/Classes/MainViewController.h"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/Classes/MainViewController.m"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/main.m"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/"+getProjectName()+"-Info.plist"), "__TESTING__", getProjectName());
		processFile(new File(getDestination(),getProjectName()+"/"+getProjectName()+"-Prefix.pch"), "__TESTING__", getProjectName());
		
		
	}
	
	private File generateCordovaLib() throws IOException{
		File cordovaLibDirectory = new File(getDestination(),"CordovaLib");
		if ( !cordovaLibDirectory.exists() ){
			directoryCopy(IOSCore.getContext().getBundle().getEntry("/templates/CordovaLib"), toURL(new File(getDestination(),"CordovaLib")));
		}
		return cordovaLibDirectory;
	}
	
	@Override
	protected String getTargetShortName(){
		return "ios";
	}

	@Override
	protected void replaceCordovaPlatformFiles() throws IOException{
		File cordovaLib = generateCordovaLib();
		fileCopy(toURL(new File(cordovaLib,"cordova.ios.js")), toURL(new File(getPlatformWWWDirectory(), "cordova.js")));
		
	}

	@Override
	protected File getPlatformWWWDirectory() {
		return new File(getDestination(), "www");
	}

	private void processFile(File file, String replaced, String replacement ) throws IOException{
		 	BufferedReader reader = null;
	        StringBuffer buffer = null;

	        reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	        buffer = new StringBuffer();
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	            buffer.append(line).append("\n"); //$NON-NLS-1$
	        }
	        reader.close();

	        String content = buffer.toString();
	        content = content.replace(replaced, replacement);
	        copyStreams(new ByteArrayInputStream(content.getBytes()), new FileOutputStream(file));
	        
	}
	
	 private void copyStreams(InputStream in, OutputStream out)
            throws IOException {
        int _byte = -1;
        while ((_byte = in.read()) != -1) {
            out.write(_byte);
        }
    }



}
