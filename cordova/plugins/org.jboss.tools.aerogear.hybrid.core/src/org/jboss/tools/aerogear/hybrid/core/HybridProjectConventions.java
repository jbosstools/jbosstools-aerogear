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
package org.jboss.tools.aerogear.hybrid.core;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Provides utilities for checking against Hybrid mobile projec conventions
 * such as the naming syntax
 * @author Gorkem Ercan
 *
 */
public class HybridProjectConventions {
	
	/**
	 * Validates if a project name is valid.
	 * 
	 * @param name
	 * @return
	 */
	public static IStatus validateProjectName(String name ){
		if(name == null || name.trim().isEmpty() )
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Project name must be specified");
		Pattern pattern  = Pattern.compile("[_a-zA-z][_a-zA-Z0-9]*");
		if(!pattern.matcher(name).matches()){
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, name + " is not a valid application name");
		}
		return Status.OK_STATUS;

	}

	/**
	 * Validates if an application id is valid for use.
	 * 
	 * @param id
	 * @return
	 */
	public static IStatus validateProjectID(String id ){
		if(id == null || id.trim().isEmpty() )
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Application ID must be specified");

		Pattern pattern  = Pattern.compile("([a-zA-Z][a-zA-Z\\d_]*[\\.])+[a-zA-Z_][a-zA-Z\\d_]*");
		if( !pattern.matcher(id).matches()){
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, id + " is not a valid application id");
		}
		return Status.OK_STATUS;
	}
	
	public static IStatus validateApplicationName(String name ){
		if( name == null || name.trim().isEmpty() )
			return new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Application name must be specified");
		
		if( name.length() >=25 ){
			// iTunes Store recommendation which is valid for other stores as well.
			// http://developer.apple.com/library/mac/#documentation/LanguagesUtilities/Conceptual/iTunesConnect_Guide/18_BestPractices/BestPractices.html
			return new Status(IStatus.WARNING, HybridCore.PLUGIN_ID,
					"Application names are recommended to have fewer than 25 characters for best presentation.");
		}
		return Status.OK_STATUS;
	}
	
}
