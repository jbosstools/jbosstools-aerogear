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
package org.jboss.tools.aerogear.hybrid.engine.internal.cordova;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ecf.filetransfer.IncomingFileTransferException;
import org.eclipse.ecf.filetransfer.identity.FileCreateException;
import org.eclipse.ecf.filetransfer.identity.FileIDFactory;
import org.eclipse.ecf.filetransfer.identity.IFileID;
import org.eclipse.ecf.filetransfer.service.IRetrieveFileTransfer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.engine.HybridMobileEngine;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;

import com.github.zafarkhaja.semver.Version;

public class CordovaEngineProvider {
	
	private static final Version MIN_VERSION = Version.forIntegers(3, 0, 0);
	private static final String ENGINE_NAME = "Apache Cordova";
	/**
	 * Engine id for the engine provided by the Apache cordova project.
	 */
	public static final String CORDOVA_ENGINE_ID = "cordova";

	private static HashMap<String, Ref> downloadeableVersionsCache = new HashMap<String, Ref>();
	private static Map<String,HybridMobileEngine> engineList;
	
	private Platform[] platforms = new Platform[] {new Platform("ios","https://git-wip-us.apache.org/repos/asf?p=cordova-ios.git" ),
												   new Platform("android","https://git-wip-us.apache.org/repos/asf?p=cordova-android.git"),
												   new Platform("wp8", "https://git-wip-us.apache.org/repos/asf?p=cordova-wp8.git"),
												   new Platform("blaackberry10","https://git-wip-us.apache.org/repos/asf?p=cordova-blackberry.git"),
												   new Platform("firefoxos","https://git-wip-us.apache.org/repos/asf?p=cordova-firefoxos.git"),
												   new Platform("windows8", "https://git-wip-us.apache.org/repos/asf?p=cordova-windows.git")
												  };

	private class Platform{
		String id;
		String uri;
		public Platform(String id, String uri){
			this.id = id;
			this.uri = uri;
		}
	}
	
	/**
	 * List of engines that are locally available. This is the list of engines that 
	 * can be used by the projects.
	 * 
	 * @return 
	 */
	public List<HybridMobileEngine> getAvailableEngines() {
		initEngineList();
		ArrayList<HybridMobileEngine> list = new ArrayList<HybridMobileEngine>();
		list.addAll(engineList.values());
		return list;
	}

	
	private void resetEngineList(){
		engineList = null;
	}
	private void initEngineList() {
		if(engineList != null ) 
			return;
		engineList = new HashMap<String, HybridMobileEngine>();
		
		File libFolder = getLibFolder();
		if( !libFolder.isDirectory()){
			//engine folder does not exist
			return;
		}
		File[] platformDirs = libFolder.listFiles();
		for (int i = 0; i < platformDirs.length; i++) {
			if(platformDirs[i].isDirectory() && !platformDirs[i].getName().equals(PlatformConstants.DIR_WWW)){
				String currentPlatform = platformDirs[i].getName();
				File engineDir = new File(platformDirs[i],CORDOVA_ENGINE_ID);
				if(engineDir.isDirectory()){
					File[] engines = engineDir.listFiles();
					for (int j = 0; j < engines.length; j++) {
						if(!engines[j].isDirectory())
							continue;
						String version = engines[j].getName();
						Version v = Version.valueOf(version);
						if(v.greaterThanOrEqualTo(MIN_VERSION)){//check the minimum supported version
							if(engineList.containsKey(version)){
								engineList.get(version).addPlatform(currentPlatform);
							}else{
								engineList.put(version, createEngine(version, currentPlatform));
							}
						}
					}
					
				}
			}
			
		}
	}
	
	/**
	 * User friendly name of the engine
	 * 
	 * @return
	 */
	public String getName(){
		return ENGINE_NAME;
	}

	public HybridMobileEngine getEngine(String version ){
		initEngineList();
		return engineList.get(version);
	}
	
	/**
	 * Helper method for creating engines.. Clients should not 
	 * use this method but use {@link #getAvailableEngines()} or 
	 * {@link #getEngine(String)}. This method is left public 
	 * mainly to help with testing.
	 * 
	 * @param version
	 * @param platforms
	 * @return
	 */
	public HybridMobileEngine createEngine(String version, String... platforms ){
		HybridMobileEngine engine = new HybridMobileEngine();
		engine.setId(CORDOVA_ENGINE_ID);
		engine.setName(ENGINE_NAME);
		engine.setVersion(version);
		for (int i = 0; i < platforms.length; i++) {
			engine.addPlatform(platforms[i]);
		}
		return engine;
	}
	
	public static File getLibFolder(){
		IPath path = new Path(FileUtils.getUserDirectory().toString());
		path = path.append(".cordova").append("lib");
		return path.toFile();
	}
	
	/**
	 * Returns a list of all the tags on the Apache repository. This method does not 
	 * filter any of the tags. It is up to the clients to filter.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public String[] getDownloadableVersions() throws CoreException{
		if(downloadeableVersionsCache.isEmpty()){
			try {
				Collection<Ref> refs = Git.lsRemoteRepository().setRemote("https://git-wip-us.apache.org/repos/asf/cordova-ios.git").setHeads(false).setTags(true).call();
				for (Iterator<Ref> iterator = refs.iterator(); iterator.hasNext();) {
					Ref ref =  iterator.next();
					String[] parts = ref.getName().split("/");
					Version v = Version.valueOf(parts[parts.length-1]);
					if(v.greaterThanOrEqualTo(MIN_VERSION)){
						downloadeableVersionsCache.put(v.toString(),ref);
					}
				}
			} catch (GitAPIException e) {
				throw new CoreException(new Status(IStatus.ERROR, HybridCore.PLUGIN_ID,"Unable to retrieve downloadable versions list",e));
			}
		}
		return downloadeableVersionsCache.keySet().toArray(new String[downloadeableVersionsCache.size()]);
	}
	
	public void downloadEngine(String version, IProgressMonitor monitor, String... platforms){
		if(monitor == null ){
			monitor = new NullProgressMonitor();
		}
		
		IRetrieveFileTransfer transfer = HybridCore.getDefault().getFileTransferService();
		IFileID remoteFileID;

		int platformSize = platforms.length;
		Object lock = new Object();
		int incompleteCount = platformSize;
		monitor.beginTask("Download Cordova Engine "+version, platformSize *100 +1);
		monitor.worked(1);
		for (int i = 0; i < platformSize; i++) {
			Platform p = getPlatform(platforms[i]);
			Assert.isNotNull(p);
			try {
				URI uri = URI.create(p.uri +";a=snapshot;h=" + version + ";sf=tgz");
				remoteFileID = FileIDFactory.getDefault().createFileID(transfer.getRetrieveNamespace(), uri);
				SubProgressMonitor sm = new SubProgressMonitor(monitor, 100);
				if(monitor.isCanceled()){
					return;
				}
				transfer.sendRetrieveRequest(remoteFileID, new EngineDownloadReceiver(version, platforms[i], lock, sm), null);
			} catch (FileCreateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IncomingFileTransferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		synchronized (lock) {
			while(incompleteCount >0){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				incompleteCount--;
				System.out.println("COMPLETED ONE " + incompleteCount + " MORE TO GO");
			}
		}
		monitor.done();
		resetEngineList();
	}
	
	private Platform getPlatform(String platformId){
		for (int i = 0; i < platforms.length; i++) {
			if(platforms[i].id.equals(platformId))
				return platforms[i];
		}
		return null;
	}
	
	/**
	 * Check if the platform is supported by this provider.
	 * 
	 * @param platformId
	 * @return
	 */
	public boolean isSupportedPlatform(String platformId){
		Assert.isNotNull(platformId);
		return getPlatform(platformId) != null;
	}

}
