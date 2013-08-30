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
package org.jboss.tools.aerogear.hybrid.core.plugin;


import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getAssets;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getAttributeValue;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getConfigFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getAllConfigFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getFrameworks;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getLibFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getPlatformNode;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getResourceFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getSourceFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.stringifyNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.extensions.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractPluginInstallationActionsFactory;
import org.jboss.tools.aerogear.hybrid.core.platform.IPluginInstallationAction;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.core.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


/**
 * Manages the Cordova plugins for a project. 
 * 
 * @author Gorkem Ercan
 *
 */
public class CordovaPluginManager {
	
	private final HybridProject project;
	private List<CordovaPlugin> installedPlugins = new ArrayList<CordovaPlugin>();
	
	public CordovaPluginManager(HybridProject project){
		this.project = project;
	}
	
	/**
	 * Installs a Cordova plugin to {@link HybridProject} from directory.
	 * A plugins installation is a two step process. This method triggers the 
	 * first step where Cordova Plugins is installed to HybridProject. 
	 * 
	 * @see #completePluginInstallationsForPlatform(File, String)
	 * @param directory
	 * @throws CoreException <ul>
	 *<li>if plugin.xml is missing</li>
	 *<li>if plugins directory is missing on the project</li>
	 *<li>if an error occurs during installation</li>
	 *</ul>
	 */
	public void installPlugin(File directory) throws CoreException{
		File pluginFile = new File(directory, PlatformConstants.FILE_XML_PLUGIN);
		Document doc = XMLUtil.loadXML(pluginFile); 
		String id = CordovaPluginXMLHelper.getAttributeValue(doc.getDocumentElement(), "id");
		if(isPluginInstalled(id))
			return;
		if( !pluginFile.exists() ){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Not a valid plugin directory, no plugin.xml exists"));
		}
		IResource dir = this.project.getProject().findMember("/"+PlatformConstants.DIR_PLUGINS);
		if(dir == null || dir.getLocation() == null ){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID,"project is missing the plugins directory"));
		}
		List<IPluginInstallationAction> actions = new ArrayList<IPluginInstallationAction>();
		
		File destination = new File(dir.getLocation().toFile(), id);
		
		CopyFileAction copy = new CopyFileAction(directory, destination);
		actions.add(copy);
		actions.addAll(collectAllConfigXMLActions(doc));
	
		
		runActions(actions);
		this.project.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}
	
	/**
	 * Completes the installation of all the installed plugins in this HybridProject 
	 * to the given platform project location. 
	 * This installation involves modifying of necessary files and 
	 * copying/generation of the others.
	 * 
	 * @param platformProjectLocation
	 * @param platform
	 * 
	 * @throws CoreException
	 */
	public void completePluginInstallationsForPlatform(File platformProjectLocation, String platform) throws CoreException{
		List<CordovaPlugin> plugins  = getInstalledPlugins();
		ProjectGenerator generator = HybridCore.getPlatformProjectGenerator(platform);
		for (CordovaPlugin cordovaPlugin : plugins) {
 			completePluginInstallationToPlatform(cordovaPlugin, generator, platformProjectLocation);
		}
	}
	
	/**
	 * Return unmodifiable list of currently installed plugins. 
	 * 
	 * @return list of installedPlugins
	 * @throws CoreException
	 */
	public List<CordovaPlugin> getInstalledPlugins() throws CoreException{
		updatePluginList();
		return Collections.unmodifiableList(installedPlugins);
	}
	
	/**
	 * Checks if the given plugin with pluginId is installed for the project.
	 * 
	 * @param pluginId
	 * @return true if the plugin is installed
	 */
	public boolean isPluginInstalled(String pluginId){
		if(pluginId == null ) return false;
		try{
			List<CordovaPlugin> plugins = getInstalledPlugins();
			for (CordovaPlugin cordovaPlugin : plugins) {
				if(cordovaPlugin.getId().equals(pluginId))
					return true;
			}
		}
		catch(CoreException e){
			return false;
		}
		return false;
	}
	/**
	 * Constructs the contents for the cordova_plugin.js from the list of 
	 * installed plugins. 
	 * 
	 * @return 
	 * @throws CoreException
	 */
	public String getCordovaPluginJSContent() throws CoreException{
		JsonArray moduleObjects = new JsonArray();
		
		List<CordovaPlugin> plugins =  getInstalledPlugins();
		for (CordovaPlugin cordovaPlugin : plugins) {
			List<PluginJavaScriptModule> modules = cordovaPlugin.getModules();
			for (PluginJavaScriptModule pluginJavaScriptModule : modules) {
				JsonObject obj = new JsonObject();
				obj.addProperty("file", (new Path("plugins")).append(cordovaPlugin.getId()).append(pluginJavaScriptModule.getSource()).toString());
				obj.addProperty("name", pluginJavaScriptModule.getName());
				if(pluginJavaScriptModule.isRuns()) {
					obj.addProperty("runs", true);
				}
				if( pluginJavaScriptModule.getClobbers() != null ){
					List<String> clobbers = pluginJavaScriptModule.getClobbers();
					JsonArray clobbersArray = new JsonArray();
					for (String string : clobbers) {
						clobbersArray.add(new JsonPrimitive(string));
					}
					obj.add("clobbers", clobbersArray);
				}
				if( pluginJavaScriptModule.getMerges() != null ){
					List<String> merges = pluginJavaScriptModule.getMerges();
					JsonArray mergesArray = new JsonArray();
					for (String string : merges) {
						mergesArray.add(new JsonPrimitive(string));
					}
					obj.add("merges", mergesArray);
				}
				moduleObjects.add(obj);
			}
		}
		StringBuilder finalContents = new StringBuilder();
		finalContents.append("cordova.define('cordova/plugin_list', function(require, exports, module) {\n");
		Gson gson = new Gson();
	    finalContents.append("module.exports = ").append(gson.toJson(moduleObjects)).append("\n});");
	    
		return finalContents.toString();
	}

	private void updatePluginList() throws CoreException {
		IResourceVisitor visitor = new IResourceVisitor() {
			
			@Override
			public boolean visit(IResource resource) throws CoreException {
				if(resource.getType() == IResource.FOLDER){
					IFolder folder = (IFolder) resource.getAdapter(IFolder.class);
					IFile file = folder.getFile(PlatformConstants.FILE_XML_PLUGIN);
					if(file.exists()){
						addInstalledPlugin(file);
					}
				}
				return true;
			}
		};
		IFolder plugins = this.project.getProject().getFolder(PlatformConstants.DIR_PLUGINS);
		synchronized (installedPlugins) {
			plugins.accept(visitor,IResource.DEPTH_ONE,false);
		}
	}
	
	private void addInstalledPlugin(IFile pluginxml) throws CoreException{
		CordovaPlugin plugin = CordovaPluginXMLHelper.createCordovaPlugin(pluginxml.getContents());
		int index = installedPlugins.indexOf(plugin);
		if(index>-1){
			installedPlugins.set(index, plugin);
		}else{
			installedPlugins.add(plugin);
		}
	}

	private File getPluginHomeDirectory(CordovaPlugin plugin) throws CoreException{
		IProject prj = this.project.getProject();
		IPath path = prj.getLocation().append(PlatformConstants.DIR_PLUGINS).append(plugin.getId());
		File f = path.toFile();
		if(f.exists())
			return f;
		throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Plugin folder does not exist"));
	}
	
	private void runActions(final List<IPluginInstallationAction> actions ){
		Stack<IPluginInstallationAction> executed = new Stack<IPluginInstallationAction>();
		boolean rollback = false;
		for (IPluginInstallationAction action : actions) {
			try {
				action.install();
				executed.push(action);
			} catch (CoreException e) {
				HybridCore.log(IStatus.ERROR, "Error while installing plugin", e);
				rollback = true;
				break;
			}
		}
		if (rollback) {
			while (!executed.empty()) {
				IPluginInstallationAction action = executed.pop();
				try {
					action.unInstall();
				} catch (CoreException e) {
					HybridCore.log(IStatus.ERROR,
							"Error rolling back install action", e);
				}
			}
		}
	}
	/*
	 * 1. collect common asset tags 
	 * 2. collect config tags except config.xml (which are handled during installation)
	 * 3. collect all js-module actions (for copying source files)
	 * 3. create cordova_plugin.js
	 * 4. collect all platform specific tags
	 * 	
	 */
	private void completePluginInstallationToPlatform(CordovaPlugin plugin, ProjectGenerator generator, File platformProject) throws CoreException{
		if(generator == null ) return;
			
		File pluginHome = getPluginHomeDirectory(plugin);
		File pluginFile = new File(pluginHome, PlatformConstants.FILE_XML_PLUGIN);
		Document doc = XMLUtil.loadXML(pluginFile); 
		//TODO: check  supported engines
		ArrayList<IPluginInstallationAction> allActions = new ArrayList<IPluginInstallationAction>();
		
		Node node = getPlatformNode(doc, generator.getPlatformId());
		if( node != null ){
			AbstractPluginInstallationActionsFactory actionFactory = generator.getPluginInstallationActionsFactory(this.project.getProject(), 
					pluginHome, platformProject);
			allActions.addAll(getAssetActionsForPlatform(doc.getDocumentElement(),actionFactory ));// add common assets
			allActions.addAll(getConfigFileActionsForPlatform(doc.getDocumentElement(), actionFactory)); // common config changes
			allActions.addAll(getJSModuleActionsForPlatform(plugin, actionFactory)); // add all js-module actions
			//We do not need to create this file 
			//with every plugin. TODO: find a better place
			allActions.add(actionFactory.getCreatePluginJSAction(this.getCordovaPluginJSContent()));
			allActions.addAll(collectActionsForPlatform(node, actionFactory));
		}
		runActions(allActions);
	}
	
	private List<IPluginInstallationAction> getJSModuleActionsForPlatform(CordovaPlugin plugin,AbstractPluginInstallationActionsFactory factory) {
		List<PluginJavaScriptModule> modules =  plugin.getModules(); 
		List<IPluginInstallationAction> actions = new ArrayList<IPluginInstallationAction>();
		for (PluginJavaScriptModule scriptModule : modules) {
			IPluginInstallationAction action = factory.getJSModuleAction(scriptModule.getSource(), plugin.getId());
			actions.add(action);
		}
		return actions;
	}

	private List<IPluginInstallationAction> collectAllConfigXMLActions(Document doc){
		NodeList configFiles = getAllConfigFileNodes(doc.getDocumentElement());
		ArrayList<IPluginInstallationAction> configList = new ArrayList<IPluginInstallationAction>();
		for (int i = 0; i < configFiles.getLength(); i++) {
			Node current = configFiles.item(i);
			String target = getAttributeValue(current, "target");
			if(!target.endsWith(PlatformConstants.FILE_XML_CONFIG)){
				continue;
			}
			String parent = getAttributeValue(current, "parent");
			String value = stringifyNode(current);
			IPluginInstallationAction action = new ConfigXMLUpdateAction(this.project, parent, value);
			configList.add(action);
		}
		return configList;
	}
	
	private List<IPluginInstallationAction> collectActionsForPlatform(Node node, AbstractPluginInstallationActionsFactory factory) throws CoreException{

		ArrayList<IPluginInstallationAction> actionsList = new ArrayList<IPluginInstallationAction>(); 
		actionsList.addAll(getSourceFilesActionsForPlatform(node, factory));
		actionsList.addAll(getResourceFileActionsForPlatform(node, factory));
		actionsList.addAll(getHeaderFileActionsForPlatform(node, factory));
		actionsList.addAll(getAssetActionsForPlatform(node, factory));
		actionsList.addAll(getConfigFileActionsForPlatform(node, factory));
		actionsList.addAll(getLibFileActionsForPlatform(node, factory)) ;
		actionsList.addAll(getFrameworkActionsForPlatfrom(node, factory ));
		return actionsList;
	}

	private List<IPluginInstallationAction> getFrameworkActionsForPlatfrom(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList frameworks = getFrameworks(node);
		for( int i =0; i< frameworks.getLength(); i++){
			Node current = frameworks.item(i);
			String src = getAttributeValue(current, "src");
			String weak = getAttributeValue(current, "weak");
			IPluginInstallationAction action = factory.getFrameworkAction(src,weak);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction> getLibFileActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList libFiles = getLibFileNodes(node);
		for(int i = 0; i<libFiles.getLength(); i++){
			Node current = libFiles.item(i);
			String src = getAttributeValue(current, "src");
			String arch = getAttributeValue(current, "arch");
			IPluginInstallationAction action = factory.getLibFileAction(src,arch);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction>  getConfigFileActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList configFiles = getConfigFileNodes(node);
		for (int i = 0; i < configFiles.getLength(); i++) {
			Node current = configFiles.item(i);
			String target = getAttributeValue(current, "target");
			if(target.endsWith(PlatformConstants.FILE_XML_CONFIG)){//config.xmls are handled on #collectAllConfigXMLActions
				continue;
			}
			String parent = getAttributeValue(current, "parent");
			String value = stringifyNode(current);
			IPluginInstallationAction action = factory.getConfigFileAction(target,parent, value);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction> getHeaderFileActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList headerFiles = CordovaPluginXMLHelper.getHeaderFileNodes(node);
		for (int i = 0; i < headerFiles.getLength(); i++) {
			String src = getAttributeValue(headerFiles.item(i), "src");
			IPluginInstallationAction action = factory.getHeaderFileAction(src);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction> getResourceFileActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList resourceFiles = getResourceFileNodes(node);
		for (int i = 0; i < resourceFiles.getLength(); i++) {
			String src = getAttributeValue(resourceFiles.item(i), "src");
			IPluginInstallationAction action = factory.getResourceFileAction(src);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction> getSourceFilesActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList sourceFiles = getSourceFileNodes(node);
		for (int i = 0; i < sourceFiles.getLength(); i++) {
			Node current = sourceFiles.item(i);
			String src = getAttributeValue(current, "src");
			String targetDir = getAttributeValue(current,"target-dir" );
			String framework = getAttributeValue(current,"framework" );
			String compilerFlags = getAttributeValue(current, "compiler-flags");
			IPluginInstallationAction action = factory.getSourceFileAction(src, targetDir, framework, compilerFlags);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction> getAssetActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList assets = getAssets(node);
		for (int i = 0; i < assets.getLength(); i++) {
			Node current = assets.item(i);
			String src = getAttributeValue(current, "src");
			String target = getAttributeValue(current, "target");
			IPluginInstallationAction action = factory.getAssetAction(src,target);
			list.add(action);
		}
		return list;
	}
		
}
