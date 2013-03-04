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

public class Access extends AbstractConfigObject{
	
	private String origin;
	private boolean subdomains;
	private boolean browserOnly;
	
	Access(Node node){
		origin = getNodeAttribute(node, "origin");
		String str = getNodeAttribute(node, "subdomains");
		if(str != null && !str.isEmpty()){
			subdomains = Boolean.parseBoolean(str);
		}
		str = getNodeAttribute(node, "browserOnly");
		if(str != null && !str.isEmpty()){
			browserOnly = Boolean.parseBoolean(str);
		}
		
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public boolean isSubdomains() {
		return subdomains;
	}

	public void setSubdomains(boolean subdomains) {
		this.subdomains = subdomains;
	}

	public boolean isBrowserOnly() {
		return browserOnly;
	}

	public void setBrowserOnly(boolean browserOnly) {
		this.browserOnly = browserOnly;
	}

}
