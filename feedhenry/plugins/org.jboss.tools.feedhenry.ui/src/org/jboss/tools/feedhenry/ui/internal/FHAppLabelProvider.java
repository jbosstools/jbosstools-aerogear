/*******************************************************************************
 * Copyright (c) 2014,2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;

final class FHAppLabelProvider extends LabelProvider implements IFontProvider, IColorProvider{
	
	private IFilter disabledFilter;
	
	@Override
	public String getText(Object element) {
		if(element instanceof FeedHenryProject){
			FeedHenryProject p = (FeedHenryProject) element;
			return p.getTitle();
		}
		FeedHenryApplication app = (FeedHenryApplication) element;
		return app.getTitle();
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof FeedHenryProject ){
			ImageDescriptor desc = FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID, "/icons/obj16/feedhenry_16.png");
			return desc.createImage();
		}
		if(element instanceof FeedHenryApplication){
			FeedHenryApplication app = (FeedHenryApplication) element;
			if(FeedHenryApplication.APP_TYPE_CORDOVA_ADVANCED.equals(app.getType())){
				ImageDescriptor desc = FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID, "/icons/obj16/cordova_16.png");
				return desc.createImage();
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
		}
		return super.getImage(element);
	}

	@Override
	public Font getFont(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if(element instanceof FeedHenryApplication ){
			FeedHenryApplication app = (FeedHenryApplication) element;
			if(disabledFilter != null && disabledFilter.select(app)){
				return Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND);
			}
		}return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
	
	/*package*/ void setDisabledItemsFilter(IFilter filter){
		this.disabledFilter = filter;
	}
	
}