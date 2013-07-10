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
import java.util.List;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.aerogear.hybrid.core.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.ui.HybridUI;
import org.jboss.tools.aerogear.hybrid.ui.PlatformImage;

public class ProjectGeneratorLabelProvider extends BaseLabelProvider implements ILabelProvider{
	private HashMap<String, Image> imageCache = new HashMap<String, Image>();
	@Override
	public Image getImage(Object element) {
		ProjectGenerator generator = (ProjectGenerator)element;
		Image img = imageCache.get(generator);
		if(img != null ){
			return img;
		}
		List<PlatformImage> images = HybridUI.getPlatformImages();
		for (PlatformImage platformImage : images) {
			if(generator.getID().equals(platformImage.getProjectGeneratorID())){
				img = platformImage.getIcon().createImage();
				imageCache.put(generator.getID(), img);
				return img;
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		ProjectGenerator generator = (ProjectGenerator)element;
		return generator.getPlatform();
	}

}
