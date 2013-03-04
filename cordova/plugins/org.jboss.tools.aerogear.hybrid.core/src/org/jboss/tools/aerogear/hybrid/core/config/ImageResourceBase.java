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
package org.jboss.tools.aerogear.hybrid.core.config;

import org.w3c.dom.Node;


/**
 * Base for image configuration elements such as icons and splashes.
 * 
 * @author gorkem
 *
 */
public abstract class ImageResourceBase extends AbstractConfigObject {
	
	private String src;
	private int width = -1;
	private int height = -1;
	private String platform;
	private String density;
	
	ImageResourceBase(Node node){
		src = getNodeAttribute(node, "src");
		String s = getNodeAttribute(node, "width");
		if ( s != null ){
				width = Integer.parseInt(s);
		}
		s = getNodeAttribute(node, "height");
		if ( s!= null ){
			height = Integer.parseInt(s);
		}
		platform = getNodeAttribute(node, "platform");
		density = getNodeAttribute(node, "density");
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getDensity() {
		return density;
	}

	public void setDensity(String density) {
		this.density = density;
	}

}
