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
package org.jboss.tools.aerogear.hybrid.ios.core.simulator;

import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.directoryCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.toURL;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.util.ExternalProcessUtility;
import org.jboss.tools.aerogear.hybrid.ios.core.IOSCore;
import org.osgi.framework.Bundle;
/**
 * Wrapper around the native binary for controlling the iOS Simulator.
 * 
 * @author Gorkem Ercan
 *
 */
public class IOSSimulator {

	private File iosSim;
	private boolean tall;
	private boolean retina;
	private String family;
	private String[] environment;
	private String pathToBinary;
	private String sdkVersion;

	public IOSSimulator(){
		try {
			
			Bundle bundle = IOSCore.getContext().getBundle();
			File bundleDataDirectory = bundle.getDataFile("/");			
			iosSim = new File(bundleDataDirectory, "ios-sim");
			if (!iosSim.exists()) {// Copied earlier
				// TODO: this is likely to cause problems when the ios-sim
				// version changes
				// implement a solution for changing to a newer version of
				// ios-sim when plugin is updated
				//

				directoryCopy(bundle.getEntry("/ios-sim"),toURL( bundleDataDirectory));
			}
			if (iosSim.exists()){
				iosSim.setExecutable(true, false);
			}
			
		} catch (IOException e) {

		}

	}
	
	public void launch() throws CoreException{
		if(iosSim == null || !iosSim.exists() ){
			throw newException(IStatus.ERROR,"ios-sim binary is not extracted correctly");
		}
		StringBuilder cmdLine = new StringBuilder();
		cmdLine.append("\"").append(iosSim.getPath()).append("\" launch ");

		
		
		
		assert pathToBinary != null: "Path to the app binary to launch on the simulator is missing"; 
		cmdLine.append("\"").append(pathToBinary).append("\"");
		if( family != null && !family.isEmpty() ){
			cmdLine.append(" --family ").append(family);
		}
		if( sdkVersion != null ){
			cmdLine.append(" --sdk ").append(sdkVersion);
		}
		if(retina){
			cmdLine.append(" --retina");
		}
		if(tall){
			cmdLine.append(" --tall");
		}
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		processUtility.execAsync(cmdLine.toString(), iosSim.getParentFile(), null, null,environment);
	}

	public void setTall(boolean tall) {
		this.tall = tall;
	}

	public void setRetina(boolean retina) {
		this.retina = retina;
	}

	public void setFamily(String family) {
		this.family = family;
	}
	
	public void setSdkVersion(String version){
		this.sdkVersion = version;
	}
	
	private CoreException newException(int severity, String message ){
		return new CoreException(new Status(severity,IOSCore.PLUGIN_ID,message));
	}
	/**
	 * The environment variables set in the process
	 * @param envp
	 */
	public void setProcessEnvironmentVariables(String[] envp) {
		this.environment = envp;
		
	}

	public void setPathToBinary(String pathToBinary) {
		this.pathToBinary = pathToBinary;
	}
}
