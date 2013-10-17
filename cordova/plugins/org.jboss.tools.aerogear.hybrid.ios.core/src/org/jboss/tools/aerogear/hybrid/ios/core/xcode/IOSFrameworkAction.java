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
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.platform.IPluginInstallationAction;
import org.jboss.tools.aerogear.hybrid.ios.core.IOSCore;
import org.jboss.tools.aerogear.hybrid.ios.core.pbxproject.PBXFile;
import org.jboss.tools.aerogear.hybrid.ios.core.pbxproject.PBXProject;
import org.jboss.tools.aerogear.hybrid.ios.core.pbxproject.PBXProjectException;

public class IOSFrameworkAction implements IPluginInstallationAction {
	
	private final String path;
	private final File pbxFile;
	private final boolean weak;
	
	public IOSFrameworkAction(String path, boolean isWeak, File pbx){
		this.path = path;
		this.pbxFile = pbx;
		this.weak = isWeak;
	}

	@Override
	public String[] filesToOverwrite() {
		return null;
	}

	@Override
	public void install() throws CoreException {
		PBXProject project = new PBXProject(pbxFile);
		PBXFile file = new PBXFile(path);
		file.setWeak(weak);
		try {
			project.addFramework(file);
			project.persist();
		} catch (PBXProjectException e) {
			throw new CoreException(new Status(IStatus.ERROR, IOSCore.PLUGIN_ID, "Error while updating XCode project file", e));
		}catch(IOException e){
			throw new CoreException(new Status(IStatus.ERROR, IOSCore.PLUGIN_ID, "Error while saving updated XCode project file", e));
		}

	}

	@Override
	public void unInstall() throws CoreException {
		//TODO not yet implemented

	}

}
