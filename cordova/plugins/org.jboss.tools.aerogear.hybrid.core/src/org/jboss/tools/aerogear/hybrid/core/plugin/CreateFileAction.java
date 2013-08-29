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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.platform.IPluginInstallationAction;

public class CreateFileAction implements IPluginInstallationAction {
	
	private File target;
	private String content;
	
	public CreateFileAction(String content, File target ){
		this.content = content;
		this.target = target;
	}

	@Override
	public void install() throws CoreException {
		try {
			FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content.getBytes()), target);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Error while creating file " + target, e));
		}

	}

	@Override
	public void unInstall() throws CoreException {
		if(target.exists()){
			if(!target.delete()){
				throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Unable to delete file " + target));
			}
		}
	}

}
