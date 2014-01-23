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


import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_LIBS;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_RES;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_SRC;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_VALUES;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.DIR_XML;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.FILE_JAR_CORDOVA;
import static org.jboss.tools.aerogear.hybrid.android.core.AndroidConstants.FILE_XML_STRINGS;
import static org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils.directoryCopy;
import static org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils.fileCopy;
import static org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils.templatedFileCopy;
import static org.jboss.tools.aerogear.hybrid.core.internal.util.FileUtils.toURL;

import java.io.File;
import java.io.IOException;
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

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileLibraryResolver;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractProjectGeneratorDelegate;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class AndroidProjectGenerator extends AbstractProjectGeneratorDelegate{

	private static final int REQUIRED_MIN_API_LEVEL = 17;

	public AndroidProjectGenerator(){
		super();
	}
	
	public AndroidProjectGenerator(IProject project, File generationFolder,String platform) {
		init(project, generationFolder,platform);
	}

	@Override
	protected void generateNativeFiles(HybridMobileLibraryResolver resolver) throws CoreException {
		
		AndroidSDKManager sdkManager = new AndroidSDKManager();
		
		HybridProject hybridProject = HybridProject.getHybridProject(getProject());
		if(hybridProject == null ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Not a hybrid mobile project, can not generate files"));
		}
		Widget widgetModel = WidgetModel.getModel(hybridProject).getWidgetForRead();
		
		// Create the basic android project
		String packageName = widgetModel.getId();
		String name = hybridProject.getBuildArtifactAppName();

		
		AndroidSDK target = selectTarget(sdkManager);
		File destinationDir = getDestination();
		IPath destinationPath = new Path(destinationDir.toString());
		if(getDestination().exists()){
			try {//Clean the android directory to avoid and "Error:" message 
				 // from the command line tools for using update. Otherwise all create
				// project calls will be recognized as failed.
				FileUtils.cleanDirectory(getDestination());
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Error cleaning android working direcrtory", e));
			}
		}
		
		sdkManager.createProject(target, name, getDestination(),name, packageName, new NullProgressMonitor());
		
		
		try{
			IPath cordovaJarPath = destinationPath.append(DIR_LIBS).append(FILE_JAR_CORDOVA);
			//Move cordova library to /libs/cordova.jar
 			fileCopy(resolver.getTemplateFile(cordovaJarPath.makeRelativeTo(destinationPath)), toURL(cordovaJarPath.toFile()));
 			
 			
 			// //res
 			IPath resPath = destinationPath.append(DIR_RES);
			directoryCopy(resolver.getTemplateFile(resPath.makeRelativeTo(destinationPath)),
					toURL(resPath.toFile()));
			
			IFile configFile = getProject().getFile(PlatformConstants.DIR_WWW+ "/" + PlatformConstants.FILE_XML_CONFIG);
			IPath xmlPath = resPath.append(DIR_XML);
			File xmldir = xmlPath.toFile();
			if( !xmldir.exists() ){//only config.xml uses xml 
				xmldir.mkdirs();   //directory make sure it is created
			}
			fileCopy(toURL(configFile.getLocation().toFile()), 
					toURL(xmlPath.append(PlatformConstants.FILE_XML_CONFIG).toFile()));
			
			updateAppName(hybridProject.getAppName());
			
			// Copy templated files 
			Map<String, String> values = new HashMap<String, String>();
			values.put("__ID__", packageName);
			values.put("__PACKAGE__", packageName);// yeap, cordova also uses two different names
			values.put("__ACTIVITY__", name);
			values.put("__APILEVEL__", Integer.toString(target.getApiLevel()));
			
			// /AndroidManifest.xml
			IPath andrManifestPath = destinationPath.append("AndroidManifest.xml");
			templatedFileCopy(resolver.getTemplateFile(andrManifestPath.makeRelativeTo(destinationPath)), 
					toURL(andrManifestPath.toFile()),
					values);
			// /src/${package_dirs}/Activity.java
			IPath activityPath = destinationPath.append(DIR_SRC).append(HybridMobileLibraryResolver.VAR_PACKAGE_NAME).append(HybridMobileLibraryResolver.VAR_APP_NAME+".java");
			templatedFileCopy(resolver.getTemplateFile(activityPath.makeRelativeTo(destinationPath)), 
					toURL(activityPath.toFile()),
					values);
			}
		catch(IOException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "Error generating the native android project", e));
		}
	}
	
	private AndroidSDK selectTarget(AndroidSDKManager sdkManager) throws CoreException {
		List<AndroidSDK> targets = sdkManager.listTargets();
		if(targets == null || targets.isEmpty() ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "No Android targets were found, Please create a target"));
		}
		AndroidSDK target = null;
		for (AndroidSDK androidSDK : targets) {
			if(androidSDK.getApiLevel() >= REQUIRED_MIN_API_LEVEL &&
					(target == null || androidSDK.getApiLevel() > target.getApiLevel())){
				target = androidSDK;
			}
		}
		if( target == null ){
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, 
					"Please install Android API " +REQUIRED_MIN_API_LEVEL +" or later. Use the Android SDK Manager to install or upgrade any missing SDKs to tools."));
		}
		return target;
	}

	private void updateAppName( String appName ) throws CoreException{
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    DocumentBuilder db;

	    try{
	    	db = dbf.newDocumentBuilder();
	    	IPath stringsPath = new Path(getDestination().toString()).append(DIR_RES).append(DIR_VALUES).append(FILE_XML_STRINGS);
	    	File strings = stringsPath.toFile();
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
	protected void replaceCordovaPlatformFiles(HybridMobileLibraryResolver resolver) throws IOException {
		IPath cordovaJSPath = new Path(getPlatformWWWDirectory().toString()).append(PlatformConstants.FILE_JS_CORDOVA);
		fileCopy(resolver.getTemplateFile(cordovaJSPath.makeRelativeTo(new Path(getDestination().toString()))), 
				toURL(cordovaJSPath.toFile()));
	}


	
	@Override
	protected File getPlatformWWWDirectory() {
		return AndroidProjectUtils.getPlatformWWWDirectory(getDestination());
	}

}
