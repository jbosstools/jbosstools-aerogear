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
package org.jboss.tools.aerogear.hybrid.ios.core.xcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.util.ExternalProcessUtility;
import org.jboss.tools.aerogear.hybrid.core.util.TextDetectingStreamListener;
import org.jboss.tools.aerogear.hybrid.ios.core.IOSCore;

/**
 * Wrapper around the xcodebuild command line tool.
 * @author Gorkem Ercan
 *
 */
public class XCodeBuild {
	
	private class SDKListParser implements IStreamListener{
		ArrayList<XCodeSDK> sdkList=new ArrayList<XCodeSDK>(5);
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			
			if(text.indexOf("-sdk")>0){
				text = text.replaceAll("[ a-zA-Z1-9.1-9]*:", "");
				text = text.replaceAll("-sdk [a-z]*[0-9]*.[0-9]*", "");
				String[] sdks = text.split("\n");
				for (String string : sdks) {
					String clean = string.trim();
					if(!clean.isEmpty()){
					sdkList.add(new XCodeSDK(clean));
					}
					
				}
				
			}
			
		}
	}
	
	private class XCodeVersionParser implements IStreamListener{
		String version;
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			if(text.startsWith("XCode")){
				version = text.substring("XCode".length()+1, text.indexOf('\n'));
			}
			
		}
		
	}
	
	
	/**
	 * Returns the actual folder where the build artifacts can be found.
	 * 
	 * @param xcodeProjectFolder
	 * @return folder with the build artifacts
	 */
	public static File getBuildDir(File xcodeProjectFolder){
		return new File(xcodeProjectFolder,"build");
	}
	
	public boolean runBuild(IProject project, File xcodeProject, 
			ILaunchConfiguration launchConfiguration,IProgressMonitor monitor)throws CoreException
	{
		try {
			monitor.beginTask("Running XCcode build", 10);
			// xcodebuild -project $PROJECT_NAME.xcodeproj -arch i386 -target
			// $PROJECT_NAME -configuration Release -sdk $SDK clean build
			// VALID_ARCHS="i386" CONFIGURATION_BUILD_DIR="$PROJECT_PATH/build"
			HybridProject hybridProject = HybridProject.getHybridProject(project);
			if(hybridProject == null ){
				throw new CoreException(new Status(IStatus.ERROR, IOSCore.PLUGIN_ID, "Not a hybrid mobile project, can not generate files"));
			}

			String name = hybridProject.getBuildArtifactAppName();

			StringBuilder cmdString = new StringBuilder("xcodebuild -project ");
			cmdString.append("\"").append(name).append(".xcodeproj").append("\"");

			cmdString.append(" -arch i386 -target ").append(name);
			//TODO: do we need to clean every time?
			cmdString.append(" -configuration Release -sdk iphonesimulator6.1 clean build VALID_ARCHS=\"i386\" CONFIGURATION_BUILD_DIR=");
			cmdString.append("\"").append(getBuildDir(xcodeProject).getPath()).append("\"");

			ExternalProcessUtility processUtility = new ExternalProcessUtility();
			if (monitor.isCanceled()) {
				return false;
			}
			monitor.worked(1);
			TextDetectingStreamListener listener = new TextDetectingStreamListener("** BUILD SUCCEEDED **");
			processUtility.execSync(cmdString.toString(), xcodeProject,
					listener, listener, monitor, null, launchConfiguration);
			return listener.isTextDetected();
		} finally {

			monitor.done();
		}

	}

	public List<XCodeSDK> showSdks() throws CoreException {
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		SDKListParser parser = new SDKListParser();
		processUtility.execSync("xcodebuild -showsdks ", 
				null, parser, parser, new NullProgressMonitor(), null, null);
		return parser.sdkList;
	}
	
	public String version() throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		XCodeVersionParser parser = new XCodeVersionParser();
		processUtility.execSync("xcodebuild -version", 
				null, parser, parser, new NullProgressMonitor(), null, null);
		return parser.version;
	}
	
}
