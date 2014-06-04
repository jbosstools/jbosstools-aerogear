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
package org.jboss.tools.aerogear.hybrid.core.plugin;
/**
 * Represents a Cordova plugin that is not currently installed on the project but 
 * is referenced by the config.xml
 * 
 * @author Gorkem Ercan
 *
 */
public class RestorableCordovaPlugin {
	private String id;
	private String version;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
