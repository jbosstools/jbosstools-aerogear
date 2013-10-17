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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractNativeBinaryBuildDelegate;
import org.jboss.tools.aerogear.hybrid.core.util.ExternalProcessUtility;
import org.jboss.tools.aerogear.hybrid.core.util.TextDetectingStreamListener;
import org.jboss.tools.aerogear.hybrid.ios.core.IOSCore;
import org.jboss.tools.aerogear.hybrid.ios.core.simulator.IOSSimulatorLaunchConstants;

/**
 * Wrapper around the xcodebuild command line tool.
 * @author Gorkem Ercan
 *
 */
public class XCodeBuild extends AbstractNativeBinaryBuildDelegate{
	private ILaunchConfiguration launchConfiguration;
	
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
			if(text.toLowerCase().startsWith("xcode")){
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

	@Override
	public void buildNow(IProgressMonitor monitor) throws CoreException {

			if(monitor.isCanceled()){
				return;
			}
		
		try {
			monitor.beginTask("Build Cordova project for iOS", 10);
			//TODO: use extension point to create the generator.
			XcodeProjectGenerator creator = new XcodeProjectGenerator(getProject(),null,"ios");
			SubProgressMonitor generateMonitor = new SubProgressMonitor(monitor, 1);
			File xcodeProjectDir  = creator.generateNow(generateMonitor);
			
			monitor.worked(4);
			if(monitor.isCanceled()){
				return; 
			}

			// xcodebuild -project $PROJECT_NAME.xcodeproj -arch i386 -target
			// $PROJECT_NAME -configuration Release -sdk $SDK clean build
			// VALID_ARCHS="i386" CONFIGURATION_BUILD_DIR="$PROJECT_PATH/build"
			HybridProject hybridProject = HybridProject.getHybridProject(this.getProject());
			if(hybridProject == null ){
				throw new CoreException(new Status(IStatus.ERROR, IOSCore.PLUGIN_ID, "Not a hybrid mobile project, can not generate files"));
			}

			String name = hybridProject.getBuildArtifactAppName();

			StringBuilder cmdString = new StringBuilder("xcodebuild -project ");
			cmdString.append("\"").append(name).append(".xcodeproj").append("\"");

//			cmdString.append(" -arch i386 armv6 armv7 -target ").append(name);
			cmdString.append(" -target ").append(name);
			cmdString.append(" -configuration Release ");
		
			cmdString.append(" -sdk ").append(selectSDK());
			cmdString.append(" clean build ");
			cmdString.append("VALID_ARCHS=\"i386 armv6 armv7\"");
			cmdString.append(" CONFIGURATION_BUILD_DIR=").append("\"").append(getBuildDir(xcodeProjectDir).getPath()).append("\"");
			if(isRelease()){
				// We explicitly do not code sign until we have proper mechanisms to 
				// get the correct signing certificates.
				cmdString.append(" CODE_SIGN_IDENTITY=\"\" CODE_SIGNING_REQUIRED=NO");
			}

			ExternalProcessUtility processUtility = new ExternalProcessUtility();
			if (monitor.isCanceled()) {
				return;
			}
			monitor.worked(1);
			TextDetectingStreamListener listener = new TextDetectingStreamListener("** BUILD SUCCEEDED **");
			processUtility.execSync(cmdString.toString(), xcodeProjectDir,
					listener, listener, monitor, null, getLaunchConfiguration());
			if(!listener.isTextDetected()){
				throw new CoreException(new Status(IStatus.ERROR, IOSCore.PLUGIN_ID, "xcodebuild has failed"));
			}
			setBuildArtifact(new File(getBuildDir(xcodeProjectDir),name+".app"));
			if( !getBuildArtifact().exists()){
				throw new CoreException(new Status(IStatus.ERROR, IOSCore.PLUGIN_ID, "xcodebuild has failed: build artifact does not exist"));
			}
		} finally {
			monitor.done();
		}
	}

	private Object selectSDK() {
		if(isRelease()){
			return "iphoneos7.0";
		}
		XCodeSDK fallbackSDK = null;
		String launchConfigSDK=null;
		try {
			if (getLaunchConfiguration() != null) {
				launchConfigSDK = getLaunchConfiguration().getAttribute(
						IOSSimulatorLaunchConstants.ATTR_SIMULATOR_SDK_VERSION,
						(String) null);
			}
			List<XCodeSDK> sdks = this.showSdks();
			for (XCodeSDK sdk : sdks) {
				if (launchConfigSDK != null
						&& launchConfigSDK.equals(sdk.getIdentifierString())) {
					return launchConfigSDK;
				}
				if (sdk.isIOS() && sdk.isSimulator()) {
					if (fallbackSDK != null) {
						double sdkver = Double.parseDouble(sdk.getVersion());
						double fallbackVer = Double.parseDouble(fallbackSDK
								.getVersion());
						if (fallbackVer < sdkver) {
							fallbackSDK = sdk;
						}
					} else {
						fallbackSDK = sdk;
					}
				}
			}

			if (fallbackSDK != null) {
				return fallbackSDK.getIdentifierString();
			}
		} catch (CoreException e) {
			//ignored
		}
		return "iphonesimulator7.0";
	}

	public ILaunchConfiguration getLaunchConfiguration() {
		return launchConfiguration;
	}

	public void setLaunchConfiguration(ILaunchConfiguration launchConfiguration) {
		this.launchConfiguration = launchConfiguration;
	}
	
}
