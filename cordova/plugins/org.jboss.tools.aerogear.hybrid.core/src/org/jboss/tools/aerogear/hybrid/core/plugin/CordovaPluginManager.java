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
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getDependencies;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getFrameworks;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getLibFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getPlatformNode;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getResourceFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.getSourceFileNodes;
import static org.jboss.tools.aerogear.hybrid.core.plugin.CordovaPluginXMLHelper.stringifyNode;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.extensions.ProjectGenerator;
import org.jboss.tools.aerogear.hybrid.core.platform.AbstractPluginInstallationActionsFactory;
import org.jboss.tools.aerogear.hybrid.core.platform.IPluginInstallationAction;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.core.plugin.actions.ActionVariableHelper;
import org.jboss.tools.aerogear.hybrid.core.plugin.actions.ConfigXMLUpdateAction;
import org.jboss.tools.aerogear.hybrid.core.plugin.actions.CopyFileAction;
import org.jboss.tools.aerogear.hybrid.core.plugin.actions.DependencyInstallAction;
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
	 * @param overwrite
	 * @param monitor
	 * @throws CoreException <ul>
	 *<li>if plugin.xml is missing</li>
	 *<li>if plugins directory is missing on the project</li>
	 *<li>if an error occurs during installation</li>
	 *</ul>
	 */
	public void installPlugin(File directory, FileOverwriteCallback overwrite, IProgressMonitor monitor) throws CoreException{
		if(monitor == null )
			monitor = new NullProgressMonitor();
		File pluginFile = new File(directory, PlatformConstants.FILE_XML_PLUGIN);
		Assert.isTrue(pluginFile.exists());
		if(monitor.isCanceled())
			return;
		Document doc = XMLUtil.loadXML(pluginFile); 
		String id = CordovaPluginXMLHelper.getAttributeValue(doc.getDocumentElement(), "id");
		if(isPluginInstalled(id)){
			HybridCore.log(IStatus.WARNING, "Cordova Plugin ("+id+") is already installed, skipping.",null);
		}
		if( !pluginFile.exists() ){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Not a valid plugin directory, no plugin.xml exists"));
		}
		IResource dir = this.project.getProject().findMember("/"+PlatformConstants.DIR_PLUGINS);
		if(dir == null || dir.getLocation() == null ){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID,"project is missing the plugins directory"));
		}
		
		
		List<IPluginInstallationAction> actions = collectInstallActions(
				directory, doc, id, dir, overwrite);
		runActions(actions,false,overwrite,monitor);
	}

	/**
	 * Installs a Cordova plugin from a git repository. 
	 * This method delegates to {@link #installPlugin(File)} after cloning the
	 * repository to a temporary location to complete the installation of the 
	 * plugin. 
	 * <br/>
	 * If commit is not null the cloned repository will be checked out to 
	 * commit. 
	 * <br/>
	 * If subdir is not null it is assumed that the subdir path exists and installation 
	 * will be done from that location. 
	 * 
	 * @param uri
	 * @param commit 
	 * @param subdir
	 * @param overwrite
	 * @param monitor 
	 * @throws CoreException
	 */
	public void installPlugin(URI uri, String commit, String subdir,FileOverwriteCallback overwrite,IProgressMonitor monitor) throws CoreException{
		File tempRepoDirectory = new File(FileUtils.getTempDirectory(), "cordova_plugin_tmp_"+Long.toString(System.currentTimeMillis()));
		tempRepoDirectory.deleteOnExit();
		try {
			if(monitor.isCanceled())
				return;
			monitor.subTask("Clone plugin repository");
			Git git = Git.cloneRepository().setDirectory(tempRepoDirectory).setURI(uri.toString()).call();
			if(commit != null && !monitor.isCanceled()){
				git.checkout().setName(commit).call();
			}
			monitor.worked(1);
			SubProgressMonitor sm = new SubProgressMonitor(monitor, 1);
			sm.setTaskName("Installing to "+this.project.getProject().getName());
			File pluginDirectory = tempRepoDirectory;
			if(subdir != null ){
				pluginDirectory = new File(tempRepoDirectory, subdir);
				if(!pluginDirectory.isDirectory()){
					throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, subdir + " does not exist in this repo"));
				}
			}
			this.installPlugin(pluginDirectory,overwrite,sm);
		} catch (GitAPIException e) {
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Error cloning the plugin repository", e));
		} finally{
			monitor.done();
		}
	}
	/**
	 * Removes the plugin with given id
	 * @param id
	 * @param overwrite
	 * @param monitor
	 * 
	 * @throws CoreException
	 */
	public void unInstallPlugin(String id, IProgressMonitor monitor) throws CoreException{
		if(id == null || !isPluginInstalled(id))
			return;
		IResource dir = this.project.getProject().findMember("/"+PlatformConstants.DIR_PLUGINS+"/"+id);
		File pluginFile = new File(dir.getLocation().toFile(), PlatformConstants.FILE_XML_PLUGIN);
		if( !pluginFile.exists() ){
			throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID, "Not a valid plugin id , no plugin.xml exists"));
		}
		Document doc = XMLUtil.loadXML(pluginFile); 
		
		FileOverwriteCallback cb = new FileOverwriteCallback() {
			@Override
			public boolean isOverwriteAllowed(String[] files) {
				return true;
			}
		};
		IResource pluginsDir = this.project.getProject().findMember("/"+PlatformConstants.DIR_PLUGINS);
		List<IPluginInstallationAction> actions = collectInstallActions(
				dir.getLocation().toFile(),             // TODO: replace with values from .fetch.json
				doc, id, pluginsDir,cb);                           
		runActions(actions,true,cb, monitor);

	}
	
	/**
	 * Completes the installation of all the installed plugins in this HybridProject 
	 * to the given platform project location. 
	 * This installation involves modifying of necessary files and 
	 * copying/generation of the others.
	 * 
	 * @param platformProjectLocation
	 * @param platform
	 * @param overwrite
	 * @param monitor
	 * 
	 * @throws CoreException
	 */
	public void completePluginInstallationsForPlatform(File platformProjectLocation, String platform, FileOverwriteCallback overwrite, IProgressMonitor monitor) throws CoreException{
		List<CordovaPlugin> plugins  = getInstalledPlugins();
		ProjectGenerator generator = HybridCore.getPlatformProjectGenerator(platform);
		for (CordovaPlugin cordovaPlugin : plugins) {
 			completePluginInstallationToPlatform(cordovaPlugin, generator, platformProjectLocation, overwrite, monitor);
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
	public String getCordovaPluginJSContent(String platformId) throws CoreException{
		JsonArray moduleObjects = new JsonArray();
		
		List<CordovaPlugin> plugins =  getInstalledPlugins();
		for (CordovaPlugin cordovaPlugin : plugins) {
			List<PluginJavaScriptModule> modules = cordovaPlugin.getModules();
			for (PluginJavaScriptModule pluginJavaScriptModule : modules) {
				if( platformId == null || pluginJavaScriptModule.getPlatform() == null ||
						pluginJavaScriptModule.getPlatform().equals(platformId))
				{

					JsonObject obj = new JsonObject();
					obj.addProperty("file", (new Path("plugins")).append(cordovaPlugin.getId()).append(pluginJavaScriptModule.getSource()).toString());
					obj.addProperty("id", pluginJavaScriptModule.getName());
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
		}
		StringBuilder finalContents = new StringBuilder();
		finalContents.append("cordova.define('cordova/plugin_list', function(require, exports, module) {\n");
		Gson gson = new Gson();
	    finalContents.append("module.exports = ").append(gson.toJson(moduleObjects)).append("\n});");
	    
		return finalContents.toString();
	}
	
	/*
	 * Collects all the actions for first stage install/uninstall
	 */
	private List<IPluginInstallationAction> collectInstallActions(
			File directory, Document doc, String id, IResource dir, FileOverwriteCallback overwrite) {
		List<IPluginInstallationAction> actions = new ArrayList<IPluginInstallationAction>();
		NodeList dependencyNodes = getDependencies(doc.getDocumentElement());
		for (int i = 0; i < dependencyNodes.getLength(); i++) {
			Node dependencyNode = dependencyNodes.item(i);
			String dependencyId = getAttributeValue(dependencyNode, "id");
			String url = getAttributeValue(dependencyNode, "url");
			String commit = getAttributeValue(dependencyNode, "commit");
			String subdir = getAttributeValue(dependencyNode, "subdir");
			URI uri = null;
			if(url != null && !url.isEmpty()){
				uri = URI.create(url);
			}
			DependencyInstallAction action = new DependencyInstallAction(dependencyId, uri, commit, subdir, this.project, overwrite);
			actions.add(action);
		}
		File destination = new File(dir.getLocation().toFile(), id);
		
		CopyFileAction copy = new CopyFileAction(directory, destination);
		actions.add(copy);
		actions.addAll(collectAllConfigXMLActionsForSupportedPlatforms(doc));
		return actions;
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
		plugin.setFolder((IFolder)pluginxml.getParent().getAdapter(IFolder.class));
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
	
	private void runActions(final List<IPluginInstallationAction> actions, boolean runUnInstall, FileOverwriteCallback overwrite, IProgressMonitor monitor ) throws CoreException{
		PluginInstallActionsRunOperation op = new PluginInstallActionsRunOperation(actions, runUnInstall, overwrite,project.getProject());
		ResourcesPlugin.getWorkspace().run(op, monitor);
	}
	/*
	 * 1. collect common asset tags 
	 * 2. collect config tags except config.xml (which are handled during installation)
	 * 3. collect all js-module actions (for copying source files)
	 * 3. create cordova_plugin.js
	 * 4. collect all platform specific tags
	 * 	
	 */
	private void completePluginInstallationToPlatform(CordovaPlugin plugin, 
			ProjectGenerator generator, 
			File platformProject, FileOverwriteCallback overwrite,
			IProgressMonitor monitor) throws CoreException{
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
			allActions.addAll(getJSModuleActionsForPlatform(plugin, generator.getPlatformId(), actionFactory)); // add all js-module actions
			//We do not need to create this file 
			//with every plugin. TODO: find a better place
			allActions.add(actionFactory.getCreatePluginJSAction(this.getCordovaPluginJSContent(generator.getPlatformId())));
			allActions.addAll(collectActionsForPlatform(node, actionFactory));
		}
		runActions(allActions,false,overwrite,monitor);
	}
	
	private List<IPluginInstallationAction> getJSModuleActionsForPlatform(CordovaPlugin plugin,String platformId,AbstractPluginInstallationActionsFactory factory) {
		List<PluginJavaScriptModule> modules =  plugin.getModules(); 
		List<IPluginInstallationAction> actions = new ArrayList<IPluginInstallationAction>();
		for (PluginJavaScriptModule scriptModule : modules) {
			if(scriptModule.getPlatform() == null || scriptModule.getPlatform().equals(platformId)){
				IPluginInstallationAction action = factory.getJSModuleAction(scriptModule.getSource(), 
						plugin.getId(), scriptModule.getName());
				actions.add(action);
			}
		}
		return actions;
	}

	private List<IPluginInstallationAction> collectAllConfigXMLActionsForSupportedPlatforms(Document doc){
		List<ProjectGenerator> generators = HybridCore.getPlatformProjectGenerators();
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(doc.getDocumentElement());
		for (ProjectGenerator projectGenerator : generators) {
			Node platformNode = getPlatformNode(doc, projectGenerator.getPlatformId());
			if(platformNode != null)
				nodes.add(platformNode);
		}
		for(Node node: nodes){
			NodeList configFiles = getConfigFileNodes(node);
			for (int i = 0; i < configFiles.getLength(); i++) {
				Node current = configFiles.item(i);
				String target = getAttributeValue(current, "target");
				if(!target.endsWith(PlatformConstants.FILE_XML_CONFIG)){
					continue;
				}
				String parent = getAttributeValue(current, "parent");
				String resolvedValue = stringifyNode(current);
				try{
					resolvedValue = ActionVariableHelper.replaceVariables(this.project, resolvedValue);
				}
				catch(CoreException ex){
					HybridCore.log(IStatus.ERROR, "Error while resolving variables", ex);
				}
				IPluginInstallationAction action = new ConfigXMLUpdateAction(this.project, parent, resolvedValue);
				list.add(action);
			}
		}
		return list;
	}
	
	private List<IPluginInstallationAction> collectActionsForPlatform(Node node, AbstractPluginInstallationActionsFactory factory) throws CoreException{

		ArrayList<IPluginInstallationAction> actionsList = new ArrayList<IPluginInstallationAction>(); 
		actionsList.addAll(getSourceFilesActionsForPlatform(node, factory));
		actionsList.addAll(getResourceFileActionsForPlatform(node, factory));
		actionsList.addAll(getHeaderFileActionsForPlatform(node, factory));
		actionsList.addAll(getAssetActionsForPlatform(node, factory));
		actionsList.addAll(getConfigFileActionsForPlatform(node, factory));
		actionsList.addAll(getLibFileActionsForPlatform(node, factory)) ;
		actionsList.addAll(getFrameworkActionsForPlatform(node, factory ));
		return actionsList;
	}

	private List<IPluginInstallationAction> getFrameworkActionsForPlatform(Node node,
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
			String resolvedValue = stringifyNode(current);
			try{
				resolvedValue = ActionVariableHelper.replaceVariables(this.project, resolvedValue);
			}catch(CoreException e){
				HybridCore.log(IStatus.ERROR, "Error while resolving the variables", e);
			}
			IPluginInstallationAction action = factory.getConfigFileAction(target,parent, resolvedValue);
			list.add(action);
		}
		return list;
	}

	private List<IPluginInstallationAction> getHeaderFileActionsForPlatform(Node node,
			AbstractPluginInstallationActionsFactory factory) {
		ArrayList<IPluginInstallationAction> list = new ArrayList<IPluginInstallationAction>();
		NodeList headerFiles = CordovaPluginXMLHelper.getHeaderFileNodes(node);
		for (int i = 0; i < headerFiles.getLength(); i++) {
			Node current = headerFiles.item(i);
			String src = getAttributeValue(current, "src");
			String targetDir = getAttributeValue(current,"target-dir" );
			String id = CordovaPluginXMLHelper.getAttributeValue(node.getOwnerDocument().getDocumentElement(), "id");
			IPluginInstallationAction action = factory.getHeaderFileAction(src,targetDir,id);
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
			String id = CordovaPluginXMLHelper.getAttributeValue(node.getOwnerDocument().getDocumentElement(), "id");
			IPluginInstallationAction action = factory.getSourceFileAction(src, targetDir, framework,id, compilerFlags);
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
