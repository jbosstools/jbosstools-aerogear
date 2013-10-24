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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.aerogear.hybrid.android.core.AndroidCore;
import org.jboss.tools.aerogear.hybrid.core.HybridProjectConventions;
import org.jboss.tools.aerogear.hybrid.core.util.ExternalProcessUtility;
import org.jboss.tools.aerogear.hybrid.core.util.TextDetectingStreamListener;

/**
 * Wrapper around the Android CommandLine tools.
 * 
 * @author Gorkem Ercan
 *
 */
public class AndroidSDKManager {
	
	private String toolsDir;
	private String platformTools;
	
	private static class DeviceListParser implements IStreamListener{
		private StringBuffer buffer = new StringBuffer();
		
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			buffer.append(text);
		}
		
		public List<AndroidDevice> getDeviceList(){
			if (buffer == null || buffer.length() < 1)
				return null;
			
			StringReader reader = new StringReader(buffer.toString());
			BufferedReader read = new BufferedReader(reader);
			String line =null;
			ArrayList<AndroidDevice> list = new ArrayList<AndroidDevice>();
			try{
				while ((line = read.readLine()) != null) {
					if(line.isEmpty() || line.contains("List of devices attached"))
						continue;
					String[] values = line.split("\t");
					if(values.length == 2){
						AndroidDevice device = new AndroidDevice();
						device.setSerialNumber(values[0].trim());
						device.setEmulator(values[0].contains("emulator"));
						
						if("device".equals(values[1].trim())){
							device.setState(AndroidDevice.STATE_DEVICE);
						}
						else if("offline".equals(values[1].trim())){
							device.setState(AndroidDevice.STATE_OFFLINE);
						}
						list.add(device);
					}
					
				}
			}
			catch (IOException e) {
				AndroidCore.log(IStatus.ERROR, "Error parsing the Android device list", e);
				return null;
			}
			finally{
				try{
					read.close();
					reader.close();
				}catch(IOException e){/*ignored*/}
			}
			return list;
		}
		
		
	}
	
	private static class AVDListParser implements IStreamListener{
		private static final String PREFIX_NAME = "Name:";
		private StringBuffer buffer = new StringBuffer();
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			buffer.append(text);
		}
		
		public List<String> getAVDList(){
			if (buffer == null || buffer.length() < 1)
				return null;

			StringReader reader = new StringReader(buffer.toString());
			BufferedReader read = new BufferedReader(reader);
			String line =null;
			ArrayList<String> list = new ArrayList<String>();
			try{
				while ((line = read.readLine()) != null) {
					int idx = line.indexOf(PREFIX_NAME);
					if(idx > -1){
						list.add(line.substring(idx+PREFIX_NAME.length()).trim());
					}
				}
			}
			catch (IOException e) {
				AndroidCore.log(IStatus.ERROR, "Error parsing the AVD list", e);
				return null;
			}
			finally{
				try{
					read.close();
					reader.close();
				}catch(IOException e){/*ignored*/}
			}
			return list;
		}
		
	}
	
	private static class TargetListParser implements IStreamListener{
	
		private StringBuffer buffer = new StringBuffer();
		
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			buffer.append(text);
		}
		
		
		public List<AndroidSDK> getSDKList() {
			if (buffer == null || buffer.length() < 1)
				return null;

			StringReader reader = new StringReader(buffer.toString());
			BufferedReader read = new BufferedReader(reader);
			ArrayList<AndroidSDK> sdkList = new ArrayList<AndroidSDK>();

			String line = null;
			try {
				AndroidSDK sdk = null;
				while ((line = read.readLine()) != null) {
					final int scolIdx = line.indexOf(':');
					if (scolIdx < 0) {
						continue;
					}
					String[] pair = new String[2];
					pair[0] = line.substring(0, scolIdx).trim();
					pair[1] = line.substring(scolIdx + 1).trim();
					if ("id".equalsIgnoreCase(pair[0])) {
						sdk = new AndroidSDK();
						sdkList.add(sdk);
						int vIndex = pair[1].indexOf("or");
						sdk.setId(pair[1].substring(vIndex + "or".length())
								.replace("\"", ""));
					} else if ("Type".equalsIgnoreCase(pair[0])) {
						Assert.isNotNull(sdk);
						sdk.setType(pair[1].trim());
					} else if ("API level".equalsIgnoreCase(pair[0])) {
						Assert.isNotNull(sdk);
						sdk.setApiLevel(Integer.parseInt(pair[1]));
					}
				}
			} catch (IOException e) {
				AndroidCore.log(IStatus.ERROR, "Error parsing the SDK list", e);
			}
			finally{
				try{
					read.close();
					reader.close();
				}catch(IOException e){
					//ignored
				}
			}
			return sdkList;
		}
		
	}
	
	private static class CreateProjectResultParser implements IStreamListener{
		private StringBuffer buffer = new StringBuffer();
		
		@Override
		public void streamAppended(String text, IStreamMonitor monitor) {
			buffer.append(text);
			
		}
		/**
		 * Returns an error string or null if it is OK 				
		 * @return
		 */
		public String getErrorString(){
			String text = buffer.toString();
			if (text.startsWith("Error:"))
			{
				StringReader reader = new StringReader(text);
				BufferedReader read = new BufferedReader(reader);
				try {
					String line = read.readLine();
					if(line==null){
						return "";
					}
					return line.substring(7);
				} catch (IOException e) {
					AndroidCore.log(IStatus.ERROR, "Error parsing the create project command result", e);
				}
				finally{
					try{
						read.close();
						reader.close();
					}catch(IOException e){
						//ignored
					}
				}
			}
			return null;
		}
	}
	
	
	
	public AndroidSDKManager() {
		String sdkDir = AndroidCore.getSDKLocation();
		if(sdkDir == null )
			throw new IllegalStateException("No SDK is defined to work with the Android SDK Manager");
		
		if(!sdkDir.endsWith(File.separator)){
			sdkDir = sdkDir+ File.separator;
		}
		toolsDir = sdkDir+ "tools" + File.separator;
		platformTools = sdkDir +"platform-tools" + File.separator;
	}
	
	
	public void createProject(AndroidSDK target, String projectName, 
			File path, String activity, String packageName) throws CoreException{
		IStatus status = HybridProjectConventions.validateProjectName(projectName);
		if(!status.isOK())
			throw new CoreException(status);
		//activity class name matches the project name
		status = HybridProjectConventions.validateProjectName(activity);
		if(!status.isOK())
			throw new CoreException(status);
		
		status = HybridProjectConventions.validateProjectID(packageName);
		if(!status.isOK())
			throw new CoreException(status);
	
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		StringBuilder command = new StringBuilder();
		command.append(getAndroidCommand());
		command.append(" create project");
		command.append(" --target ").append(target.getId());
		command.append(" --path ").append('"').append(path.getPath()).append('"');
		command.append(" --name ").append('"').append(projectName).append('"');
		command.append(" --activity ").append(activity);
		command.append(" --package ").append(packageName);

		CreateProjectResultParser parser = new CreateProjectResultParser();
		processUtility.execSync(command.toString(), null, parser, parser, new NullProgressMonitor(), null, null);
		if( parser.getErrorString() != null ){
			throw new CoreException(new Status(IStatus.ERROR,AndroidCore.PLUGIN_ID,"Error creating the Android project: "+ parser.getErrorString()));
		}
	}
	
	public void startADBServer() throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		processUtility.execSync(getADBCommand()+" start-server",null, null, null, new NullProgressMonitor(), null, null);
	}
	
	public void killADBServer() throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		processUtility.execSync(getADBCommand()+" kill-server",null, null, null, new NullProgressMonitor(), null, null);
	}
	
	public List<String> listAVDs() throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		AVDListParser parser = new AVDListParser();
		processUtility.execSync(getAndroidCommand()+" list avd", null, parser, parser, 
				new NullProgressMonitor(), null, null);
		return parser.getAVDList();
	}
	
	public List<AndroidSDK> listTargets() throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		TargetListParser parser = new TargetListParser();
		processUtility.execSync(getAndroidCommand()+" list target", 
				null, parser, parser, new NullProgressMonitor(), null, null);
		return parser.getSDKList();
	}
	
	public List<AndroidDevice> listDevices() throws CoreException{
		initADBServer();
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		DeviceListParser parser = new DeviceListParser();
		processUtility.execSync(getADBCommand()+" devices", null, parser, parser, 
				new NullProgressMonitor(), null, null);
		List<AndroidDevice> devices = parser.getDeviceList();
		if(devices == null ){
			devices = Collections.emptyList();
		}
		return devices;
		
	}


	
	public void installApk(File apkFile, String serialNumber) throws CoreException{
		Assert.isNotNull(serialNumber);
		initADBServer();
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		StringBuilder command = new StringBuilder(getADBCommand());
		command.append(" -s ").append(serialNumber);
		command.append(" install");
		command.append(" -r ");
		command.append("\"").append(apkFile.getPath()).append("\"");
		TextDetectingStreamListener listener = new TextDetectingStreamListener("Success");
		processUtility.execSync(command.toString(), null,listener, listener, new NullProgressMonitor(), null, null);
		if (!listener.isTextDetected()){
			throw new CoreException(new Status(IStatus.ERROR, AndroidCore.PLUGIN_ID, "APK installation did not succeed"));
		}
	}
	
	public void waitForEmulator() throws CoreException{
		while(true){
			List<AndroidDevice> devices = this.listDevices();
			if(devices != null ){
				for (AndroidDevice androidDevice : devices) {
					if(androidDevice.isEmulator() && androidDevice.getState() == AndroidDevice.STATE_DEVICE)
						return;
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {

			}
		}
	}
	
	public void startApp(String component, String serialNumber) throws CoreException{
		initADBServer();
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		StringBuilder command = new StringBuilder(getADBCommand());
		command.append(" -s ").append(serialNumber);
		command.append(" shell am start");
		command.append(" -n ");
		command.append(component);
		processUtility.execSync(command.toString(), null, null, null,new NullProgressMonitor(), null, null);
		
	}
	
	public void logcat(String filter, IStreamListener outListener, IStreamListener errorListener, String serialNumber) throws CoreException{
		initADBServer();
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		StringBuilder command = new StringBuilder(getADBCommand());
		command.append(" -s ").append(serialNumber);
		command.append(" logcat ");
		if(filter !=null && !filter.isEmpty()){
			command.append(filter);
		}
		processUtility.execAsync(command.toString(), null, outListener, errorListener, null);
	}
	
	
	public void startEmulator(String avd) throws CoreException{
		ExternalProcessUtility processUtility = new ExternalProcessUtility();
		StringBuilder command = new StringBuilder(getEmulatorCommand());
		command.append(" -cpu-delay 0"); 
		command.append(" -no-boot-anim");
		command.append(" -avd ").append(avd);
		processUtility.execAsync(command.toString(), null, null, null, null);
	}
	
	private String getAndroidCommand(){
		if(isWindows()){
			return "cmd /c "+ toolsDir +"android";
		}
		return toolsDir+"android";
	}
	
	private String getADBCommand(){
		return platformTools+"adb";
	}
	
	private String getEmulatorCommand(){
		return toolsDir+"emulator";
	}
	
	private boolean isWindows(){
		String OS = System.getProperty("os.name","unknown");
		return OS.toLowerCase().indexOf("win")>-1;
	}
	
	private void initADBServer() throws CoreException {
		this.killADBServer();
		this.startADBServer();
	}
}
