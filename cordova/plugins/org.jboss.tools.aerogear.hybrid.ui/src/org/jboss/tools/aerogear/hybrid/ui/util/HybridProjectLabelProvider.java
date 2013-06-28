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
package org.jboss.tools.aerogear.hybrid.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;

public class HybridProjectLabelProvider extends LabelProvider{
	private ImageDescriptor DESC_OBJ_PROJECT;
	{
		DESC_OBJ_PROJECT = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof HybridProject ){
			HybridProject hp = (HybridProject) element;
			return hp.getProject().getName();
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element) {
		return DESC_OBJ_PROJECT.createImage();
	}
}