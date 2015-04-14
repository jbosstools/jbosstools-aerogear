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
import org.eclipse.jface.resource.ImageRegistry;
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
	
	private static final String ICON_PATH_FEEDHENRY = "/icons/obj16/feedhenry_16.png";
	private static final String ICON_PATH_ANDROID = "/icons/obj16/android_16.png";
	private static final String ICON_PATH_IOS = "/icons/obj16/apple_16.png";
	private static final String ICON_PATH_CORDOVA = "/icons/obj16/cordova_16.png";
	private static final String ICON_PATH_NODEJS = "/icons/obj16/nodejs_16.png";
	private static final String ICON_PATH_HTML5 = "/icons/obj16/html5_16.png";
	private static final String ICON_PATH_WEBAPP = "/icons/obj16/web_application.png";
	private IFilter disabledFilter;
	private ImageRegistry images;
	private Image defaultImage;
	
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
			ImageDescriptor desc = FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID, ICON_PATH_FEEDHENRY);
			return desc.createImage();
		}
		if(element instanceof FeedHenryApplication){
			final FeedHenryApplication app = (FeedHenryApplication) element;
			String imagePath = null;
			final boolean isDisabled = disabledFilter != null && disabledFilter.select(app);
			switch (app.getType()) {
			case FeedHenryApplication.APP_TYPE_CORDOVA_ADVANCED:
				imagePath = ICON_PATH_CORDOVA;
				break;
			case FeedHenryApplication.APP_TYPE_NATIVE_ANDROID:
				imagePath = ICON_PATH_ANDROID;
				break;
			case FeedHenryApplication.APP_TYPE_CLOUD_NODEJS:
				imagePath = ICON_PATH_NODEJS;
				break;
			case FeedHenryApplication.APP_TYPE_WEB_BASIC: //intentional fall through
			case FeedHenryApplication.APP_TYPE_WEB_ADVANCED:
				imagePath = ICON_PATH_WEBAPP;
				break;
			case FeedHenryApplication.APP_TYPE_NATIVE_IOS:
				imagePath = ICON_PATH_IOS;
				break;
			case FeedHenryApplication.APP_TYPE_HYBRID:
				imagePath = ICON_PATH_HTML5;
				break;
			default:
				if(defaultImage == null){
					defaultImage = PlatformUI.getWorkbench().getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
					if(isDisabled){
						defaultImage = new Image(defaultImage.getDevice(), defaultImage, SWT.IMAGE_DISABLE);
					}
				}
				return defaultImage;
			}
			final String registryKey = isDisabled ? imagePath+"_disabled": imagePath;
			Image image = getImageRegistry().get(registryKey);
			if(image == null ){
				ImageDescriptor desc = FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID, imagePath);
				image = desc.createImage();
				if(isDisabled){
					image = new Image(image.getDevice(), image, SWT.IMAGE_DISABLE);
				}
				getImageRegistry().put(registryKey, image);
			}
			return image;
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
	
	private ImageRegistry getImageRegistry(){
		if(images == null ){
			images = new ImageRegistry();
		}
		return images;
	}
	@Override
	public void dispose() {
		super.dispose();
		if(defaultImage != null ){
			defaultImage.dispose();
		}
		if(images != null ){
			images.dispose();
		}
	}
	
}