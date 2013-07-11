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
package org.jboss.tools.aerogear.hybrid.ui;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;

public class PlatformImage {
	
	private static final String ATTR_ICON = "icon";
	private static final String ATTR_PROJECT_GENERATOR="projectGenerator";

	public static final String EXTENSION_POINT_ID= "org.jboss.tools.aerogear.hybrid.ui.platformImages";

	private ImageDescriptor icon;
	private String projectGeneratorID;
	
	PlatformImage(IConfigurationElement configurationElement) {
		String iconPath = configurationElement.getAttribute(ATTR_ICON);
		icon= HybridUI.getImageDescriptor(configurationElement.getContributor().getName(), iconPath);
		projectGeneratorID = configurationElement.getAttribute(ATTR_PROJECT_GENERATOR);
		
	}

	public ImageDescriptor getIcon() {
		return icon;
	}

	public String getProjectGeneratorID() {
		return projectGeneratorID;
	}

}
