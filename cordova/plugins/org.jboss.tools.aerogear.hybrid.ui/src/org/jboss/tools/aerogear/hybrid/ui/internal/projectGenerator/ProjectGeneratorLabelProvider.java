/*******************************************************************************
 * Copyright (c) 2013,2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.aerogear.hybrid.ui.internal.projectGenerator;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.aerogear.hybrid.core.extensions.PlatformSupport;
import org.jboss.tools.aerogear.hybrid.ui.PlatformImage;

public class ProjectGeneratorLabelProvider extends BaseLabelProvider implements ILabelProvider{
	private HashMap<String, Image> imageCache = new HashMap<String, Image>();
	@Override
	public Image getImage(Object element) {
		PlatformSupport platform = (PlatformSupport)element;
		Image img = imageCache.get(platform.getID());
		if(img != null ){
			return img;
		}
		ImageDescriptor imgDesc =PlatformImage.getIconFor(PlatformImage.ATTR_PLATFFORM_SUPPORT, platform.getID());
		if(imgDesc != null){
			img= imgDesc.createImage();
			imageCache.put(platform.getID(), img);
		}
		return img;
	}

	@Override
	public String getText(Object element) {
		PlatformSupport generator = (PlatformSupport)element;
		return generator.getPlatform();
	}

}
