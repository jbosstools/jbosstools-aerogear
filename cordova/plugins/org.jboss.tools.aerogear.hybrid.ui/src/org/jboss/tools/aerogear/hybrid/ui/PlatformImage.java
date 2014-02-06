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

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
/**
 * Proxy object for the platformImages extension point.
 * 
 * @author Gorkem Ercan
 *
 */
public class PlatformImage {
	
	private static final String ATTR_ICON = "icon";
	public static final String ATTR_PLATFFORM_SUPPORT="platformSupport";
	public static final String ATTR_PROJECT_BUILDER="projectBuilder";
	public static final String EXTENSION_POINT_ID= "org.jboss.tools.aerogear.hybrid.ui.platformImages";

	private ImageDescriptor icon;
	private String projectGeneratorID;
	private String projectBuilderID;
	
	PlatformImage(IConfigurationElement configurationElement) {
		String iconPath = configurationElement.getAttribute(ATTR_ICON);
		icon= HybridUI.getImageDescriptor(configurationElement.getContributor().getName(), iconPath);
		projectGeneratorID = configurationElement.getAttribute(ATTR_PLATFFORM_SUPPORT);
		projectBuilderID = configurationElement.getAttribute(ATTR_PROJECT_BUILDER);
		
	}

	public ImageDescriptor getIcon() {
		return icon;
	}

	public String getProjectGeneratorID() {
		return projectGeneratorID;
	}

	public String getProjectBuilderID() {
		return projectBuilderID;
	}
	
	public static ImageDescriptor getIconFor(String attribute, String id ){
		List<PlatformImage> images = HybridUI.getPlatformImages();
		for (PlatformImage platformImage : images) {			
			if(attribute.equals(ATTR_PLATFFORM_SUPPORT) && id.equals(platformImage.getProjectGeneratorID())){
				return platformImage.getIcon();
			}
			if(attribute.equals(ATTR_PROJECT_BUILDER) && id.equals(platformImage.getProjectBuilderID())){
				return platformImage.getIcon();
			}
		}
		return null;
	}

}
