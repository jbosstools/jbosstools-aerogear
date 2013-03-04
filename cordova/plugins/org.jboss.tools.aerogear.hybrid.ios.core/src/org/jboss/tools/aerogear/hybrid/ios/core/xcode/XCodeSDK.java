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
package org.jboss.tools.aerogear.hybrid.ios.core.xcode;

public class XCodeSDK {
	private String description; 

	XCodeSDK(String definitionString){
		this.description = definitionString;
	}
	
	public String getDescription(){
		return description;
	}
	
	public boolean isIOS(){
		return description.contains("iOS");
	}
	
	public boolean isSimulator(){
		return description.contains("Simulator");
	}
	
	public String getVersion(){
		String[] tokens = description.split(" ");
		for (String string : tokens) {
			if ( Character.isDigit(string.charAt(0)))
				return string;
		}
		return "";
	}
	
}
