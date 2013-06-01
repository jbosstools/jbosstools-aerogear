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
package org.jboss.tools.aerogear.hybrid.android.core.adt;


import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_ASSETS;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_LIBS;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_RES;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_SRC;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_VALUES;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_XML;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.FILE_JAR_CORDOVA;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.FILE_XML_ANDROIDMANIFEST;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.FILE_XML_STRINGS;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.directoryCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.fileCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.templatedFileCopy;
import static org.jboss.tools.aerogear.hybrid.core.util.FileUtils.toURL;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;
import org.jboss.tools.aerogear.hybrid.cordova.CordovaLibrarySupport;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractPlatformProjectGenerator;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AndroidProjectGenerator extends AbstractPlatformProjectGenerator{

	public AndroidProjectGenerator(IProject project, File generationFolder) {
		super(project, generationFolder);
	}


	@Override
	protected void generateNativeFiles() throws CoreException {
		
		AndroidSDKManager sdkManager = new AndroidSDKManager();
		
		HybridProject hybridProject = HybridProject.getHybridProject(getProject());
		if(hybridProject == null ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Not a hybrid mobile project, can not generate files"));
		}
		Widget widgetModel = hybridProject.getWidget();
		
		// Create the basic android project
		String packageName = widgetModel.getId();
		String name = hybridProject.getBuildArtifactAppName();

		List<AndroidSDK> targets = sdkManager.listTargets();
		if(targets == null || targets.isEmpty() ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "No Android targets were found, Please create a target"));
		}
		
		sdkManager.createProject(targets.get(0), name, getDestination(),name, packageName );
		
		try{
			File libsDir = new File(getDestination(),DIR_LIBS);
			if(!libsDir.exists()){
				throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Android SDK tools failed to create libs folder"));
			}
			//Move cordova library to libs
			URL sourcs = getTemplateFile("/templates/android/cordova.jar");
			URL dests = toURL(new File(libsDir, FILE_JAR_CORDOVA ));
			AndroidCore.log(IStatus.INFO, "Copying cordova android lib from " + sourcs + " to " + dests,  null);
 			fileCopy(sourcs, dests);
			directoryCopy(getTemplateFile("/templates/android/project/res/"),
					toURL(new File(getDestination(), DIR_RES )));
			
			IFile configFile = getProject().getFile(PlatformConstants.DIR_WWW+ "/" + PlatformConstants.FILE_XML_CONFIG);
			File xmldir = new File(getDestination(), DIR_RES + File.separator + DIR_XML +File.separator);
			if( !xmldir.exists() ){//only config.xml uses xml 
				xmldir.mkdirs();   //directory make sure it is created
			}
			fileCopy(toURL(configFile.getLocation().toFile()), 
					toURL(new File(xmldir, PlatformConstants.FILE_XML_CONFIG)));
			
			updateAppName(hybridProject.getAppName());
			
			// Copy templated files 
			Map<String, String> values = new HashMap<String, String>();
			values.put("__ID__", packageName);
			values.put("__PACKAGE__", packageName);// yeap, cordova also uses two different names
			values.put("__ACTIVITY__", name);
			values.put("__APILEVEL__", Integer.toString(targets.get(0).getApiLevel()));
			
			templatedFileCopy(getTemplateFile("/templates/android/project/Activity.java"), 
					toURL(new File(getDestination(), File.separator+DIR_SRC+ File.separator+ 
							packageName.replace('.', File.separatorChar)+File.separator+name+".java")),
					values);
			templatedFileCopy(getTemplateFile("/templates/android/project/AndroidManifest.xml"), 
					toURL(new File(getDestination(), FILE_XML_ANDROIDMANIFEST)), values);
			
			}
		catch(IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Error generating the native android project", e));
		}
	}
	
	private void updateAppName( String appName ) throws CoreException{
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    DocumentBuilder db;

	    try{
	    	db = dbf.newDocumentBuilder();
	    	File strings = new File(getDestination(),DIR_RES+File.separator+DIR_VALUES+File.separator+ FILE_XML_STRINGS);
	    	Document configDocument = db.parse( strings); 
	    	XPath xpath = XPathFactory.newInstance().newXPath();
	    	
	    	try {
	    		XPathExpression expr = xpath.compile("//string[@name=\"app_name\"]");
				Node node = (Node) expr.evaluate( configDocument, XPathConstants.NODE);
				node.setTextContent(appName);
				
			    configDocument.setXmlStandalone(true);
			    
			    Source source = new DOMSource(configDocument);

			   
			    StreamResult result = new StreamResult(strings);

			    // Write the DOM document to the file
			    TransformerFactory transformerFactory = TransformerFactory
				    .newInstance();
			    Transformer xformer = transformerFactory.newTransformer();

			    xformer.transform(source, result);
				
			} catch (XPathExpressionException e) {//We continue because this affects the displayed app name
				                                  // which is not a show stopper during development
				AndroidCore.log(IStatus.ERROR, "Error when updating the application name", e);
			} catch (TransformerConfigurationException e) {
				AndroidCore.log(IStatus.ERROR, "Error when updating the application name", e);
			} catch (TransformerException e) {
				AndroidCore.log(IStatus.ERROR, "Error when updating the application name", e);
			}
	    	
	    }
		catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Parser error when parsing /res/values/strings.xml", e));
		} catch (SAXException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Parsing error on /res/values/strings.xml", e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "IO error when parsing /res/values/strings.xml", e));
		} 
	}

	@Override
	protected String getTargetShortName() {
		return "android";
	}

	@Override
	protected void replaceCordovaPlatformFiles() throws IOException {
		fileCopy(getTemplateFile("/templates/android/cordova.android.js"), 
				toURL(new File(getPlatformWWWDirectory(), PlatformConstants.FILE_JS_CORDOVA )));
	}

	private URL getTemplateFile(String path){
		Bundle bundle = CordovaLibrarySupport.getContext().getBundle();
		URL url = bundle.getEntry(path);
		Assert.isNotNull(url, "No template file resolved for path "+path);
		return url;
	}
	
	
	@Override
	protected File getPlatformWWWDirectory() {
		return new File(getDestination(), DIR_ASSETS + File.separator +PlatformConstants.DIR_WWW);
	}

}
