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
package org.jboss.tools.aerogear.hybrid.ui.wizard.export;

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.aerogear.hybrid.core.extensions.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.ui.PlatformImage;

public class ProjectGeneratorLabelProvider extends BaseLabelProvider implements ILabelProvider{
	private HashMap<String, Image> imageCache = new HashMap<String, Image>();
	@Override
	public Image getImage(Object element) {
		ProjectGenerator generator = (ProjectGenerator)element;
		Image img = imageCache.get(generator.getID());
		if(img != null ){
			return img;
		}
		ImageDescriptor imgDesc =PlatformImage.getIconFor(PlatformImage.ATTR_PROJECT_GENERATOR, generator.getID());
		if(imgDesc != null){
			img= imgDesc.createImage();
			imageCache.put(generator.getID(), img);
		}
		return img;
	}

	@Override
	public String getText(Object element) {
		ProjectGenerator generator = (ProjectGenerator)element;
		return generator.getPlatform();
	}

}
