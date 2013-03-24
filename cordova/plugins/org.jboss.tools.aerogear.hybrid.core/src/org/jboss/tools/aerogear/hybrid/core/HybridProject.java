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


import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.natures.HybridAppNature;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Handle for the mobile hybrid project types 
 * @author Gorkem Ercan
 *
 */
public class HybridProject {
	
	private IProject kernelProject;
	private Document configDocument;
	
	private HybridProject(IProject project) {
		this.kernelProject = project;
	} 
	
	/**
	 * Returns the {@link Widget} model for the config.xml
	 * 
	 * @return widget
	 * @throws CoreException
	 * 	<ul>
	 *   <li>if config.xml can not be parsed</li>
	 *   <li>its contents is not readable</li>
	 *   </ul>
	 *
	 */
	public Widget getWidget() throws CoreException{
		
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    DocumentBuilder db;

	    try{
	    	db = dbf.newDocumentBuilder();
	    	configDocument = db.parse( kernelProject.getFile(
	    			"/www/config.xml").getContents()); 
	    }
		catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Parser error when parsing config.xml", e));
		} catch (SAXException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Parsing error on config.xml", e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "IO error when parsing config.xml", e));
		} 
		
		 Widget widget = WidgetModel.getInstance().load(configDocument);
		 return widget;
	}

	/**
	 * Returns the underlying {@link IProject} instance.
	 * @return kernel project
	 */
	public IProject getProject(){
		return kernelProject;
	}
	
	/**
	 * Creates a hybrid project handle for the project. 
	 * Can return null if the given project is not a hybrid 
	 * mobile project.
	 * 
	 * @param project
	 * @return hybrid project or null
	 */
	public static HybridProject getHybridProject(IProject project) {
		if(project == null ) 
			return null;
		try {
			if (project.hasNature(HybridAppNature.NATURE_ID)) {
				return new HybridProject(project);
			}
		} catch (CoreException e) {
			// let it return null
		}
		return null;
	}
	
}
