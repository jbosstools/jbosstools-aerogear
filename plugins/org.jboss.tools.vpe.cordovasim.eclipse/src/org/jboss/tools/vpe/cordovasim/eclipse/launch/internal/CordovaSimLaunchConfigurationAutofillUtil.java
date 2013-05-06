/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.launch.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ide.ResourceUtil;
import org.jboss.tools.vpe.cordovasim.eclipse.launch.CordovaSimLaunchConstants;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 *
 */
public class CordovaSimLaunchConfigurationAutofillUtil {
	public static void fillLaunchConfiguraion(ILaunchConfigurationWorkingCopy launchConfiguration, IProject project) {
		if (project != null) {
			launchConfiguration.setAttribute(CordovaSimLaunchConstants.PROJECT, project.getName());
		}
	}
	
	public static IProject getProjectToRun(ISelection selection) {
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object firstElement = structuredSelection.getFirstElement();
			IResource resource = ResourceUtil.getResource(firstElement);
			if (resource != null) {
				return resource.getProject();
			}
		}
		return null;
	}
}
