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
package org.jboss.tools.aerogear.hybrid.core.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.platform.IPluginInstallationAction;
/**
 * Action that copies a source file or directory to a target file or directory on install and 
 * deletes the file/directory on uninstall.
 * @author Gorkem Ercan
 *
 */
public class CopyFileAction implements IPluginInstallationAction {
	
	private final File source;
	private final File target;
	
	public CopyFileAction(File source, File target ){
		Assert.isNotNull(source);
		Assert.isNotNull(target);
		this.source = source;
		this.target = target;
	}

	@Override
	public void install() throws CoreException{
		try{
			if(source.isDirectory()){
				FileUtils.copyDirectory(source, target);
			}
			else if(target.exists() ){//source is a file
				if(target.isDirectory()){
					FileUtils.copyFileToDirectory(source, target);
					}
				else{
					FileUtils.copyFile(source, target);
				}
				
			}else if(FilenameUtils.getExtension(target.toString()).isEmpty() ){// it is likely a directory
				FileUtils.copyFileToDirectory(source, target);
			}else{
				FileUtils.copyFile(source, target);
			}
			
			
			
		}catch(IOException e ){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Error copying "+ source + " to "+ target, e));
		}
	}

	@Override
	public void unInstall() throws CoreException {
		File todelete = target;
		if(source.isFile() && target.isDirectory()){
			 todelete = new File(target, source.getName());
		}
		if(target.isDirectory()){
			try {
				FileUtils.deleteDirectory(todelete);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Error deleting file "+target, e));
			}
		}else
		if(!todelete.delete()){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Could not delete "+ target));
		}
	}

}
