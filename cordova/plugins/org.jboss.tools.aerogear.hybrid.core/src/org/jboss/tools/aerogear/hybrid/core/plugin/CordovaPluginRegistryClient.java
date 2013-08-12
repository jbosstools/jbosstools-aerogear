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


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.runtime.Assert;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class CordovaPluginRegistryClient {
	
	private long updated;
	private List<CordovaPluginInfo> plugins;
	private String registry;
	
	public CordovaPluginRegistryClient(String url) {
		this.registry = url;
	}
	
	public CordovaPlugin getCordovaPlugin(String name) {

		HttpClient client = new DefaultHttpClient();
		String url = registry.endsWith("/") ? registry + name : registry + "/"
				+ name;
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			JsonReader reader = new JsonReader(new InputStreamReader(stream));
			CordovaPlugin plugin = new CordovaPlugin();
			readPluginInfo(reader, plugin);
			return plugin;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public List<CordovaPluginInfo> retrievePluginInfos()
	{
		HttpClient client = new DefaultHttpClient();
		String url = registry.endsWith("/") ? registry+"-/all" : registry+"/-/all";
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			JsonReader reader = new JsonReader(new InputStreamReader(stream));
			reader.beginObject();//start the Registry
			plugins = new ArrayList<CordovaPluginInfo>();
			while(reader.hasNext()){
				JsonToken token = reader.peek();
				switch (token) {
				case BEGIN_OBJECT:
					CordovaPluginInfo info = new CordovaPluginInfo();
					readPluginInfo(reader, info);
					plugins.add(info);
					break;
				case NAME:
					String name = reader.nextName();
					if(name.equals("_updated")){
						long newUpdate = reader.nextLong();
						if(newUpdate == this.updated){//No changes 
							return plugins;
						}
						
					}
					break;
				default:
					Assert.isTrue(false, "Unexpected token: " + token);
					break;
				}
				
			}
			reader.endObject();
			
			return plugins;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	private void readPluginInfo(JsonReader reader, CordovaPluginInfo plugin ) throws IOException {
		Assert.isNotNull(plugin);
		reader.beginObject();

		while (reader.hasNext()) {
			JsonToken token = reader.peek();
			switch (token) {
			case NAME: {
				String name = reader.nextName();
				if ("name".equals(name)) {
					plugin.setName(reader.nextString());
					break;
				}
				if ("description".equals(name)) {
					plugin.setDescription(reader.nextString());
					break;
				}
				if ("keywords".equals(name)) {
					parseKeywords(reader, plugin);
					break;
				}
				if("maintainers".equals(name)){
					parseMaintainers(reader,plugin);
					break;
				}
				if("dist-tags".equals(name)){
					parseLatestVersion(reader,plugin);
					break;
				}
				if("versions".equals(name) && plugin instanceof CordovaPlugin) { 
					parseDetailedVersions(reader, (CordovaPlugin)plugin);       
					break;
				}
				if("dist".equals(name) && plugin instanceof CordovaPluginVersion ){
					parseDistDetails(reader, (CordovaPluginVersion) plugin);
					break;
				}
				if("license".equals(name) && plugin instanceof CordovaPluginVersion ){
					CordovaPluginVersion v = (CordovaPluginVersion) plugin;
					v.setLicense(reader.nextString());
					break;
				}
				break;
			}

			default:
				reader.skipValue();
				break;
			}
		}
		reader.endObject();
	}

	private void parseDistDetails(JsonReader reader, CordovaPluginVersion plugin) throws IOException{
		reader.beginObject();
		JsonToken token = reader.peek();
		while(token != JsonToken.END_OBJECT){
			switch (token) {
			case NAME:
				String name = reader.nextName();
				if("shasum".equals(name)){
					plugin.setDistributionSHASum(reader.nextString());
					break;
				}
				if("tarball".equals(name)){
					plugin.setDistributionTarball(reader.nextString());
					break;
				}
				break;

			default:
				reader.skipValue();
				break;
			}
			token = reader.peek();
		}
		reader.endObject();
	}

	private void parseDetailedVersions(JsonReader reader,
			CordovaPlugin plugin) throws IOException{
		reader.beginObject();//versions
		JsonToken token = reader.peek();
		while( token != JsonToken.END_OBJECT ){
			switch (token) {
			case NAME:
				CordovaPluginVersion version = new CordovaPluginVersion();
				version.setVersionNumber(reader.nextName());
				readPluginInfo(reader, version);
				plugin.addVersion(version);
				break;

			default:
				reader.skipValue();
				break;
			}
			token = reader.peek();
		}
		reader.endObject();
	}

	private void parseLatestVersion(JsonReader reader, CordovaPluginInfo plugin) throws IOException{
		reader.beginObject();
		JsonToken token = reader.peek();
		while ( token != JsonToken.END_OBJECT){
			switch (token) {
			case NAME:
				String tag = reader.nextName();
				if("latest".equals(tag)){
					plugin.setLatestVersion(reader.nextString());
				}
				break;

			default:
				reader.skipValue();
				break;
			}
			token = reader.peek();
		}
		reader.endObject();
	}

	private void parseMaintainers(JsonReader reader, CordovaPluginInfo plugin) throws IOException{
		reader.beginArray();
		String name=null, email = null;
		JsonToken token = reader.peek();
		
		while( token != JsonToken.END_ARRAY ){
			switch (token) {
			case BEGIN_OBJECT:
				reader.beginObject();
				name = email = null;
				break;
			case END_OBJECT:
				reader.endObject();
				plugin.addMaintainer(email, name);
				break;
			case NAME:
				String tagName = reader.nextName();
				if("name".equals(tagName)){
					name = reader.nextString();
					break;
				}
				if("email".equals(tagName)){
					email = reader.nextString();
					break;
				}
			default:
				Assert.isTrue(false, "Unexpected token");
				break;
			}
			token =reader.peek();
		}
		reader.endArray();
	}

	private void parseKeywords(JsonReader reader, CordovaPluginInfo plugin) throws IOException{
		reader.beginArray();
		while(reader.hasNext()){
			plugin.addKeyword(reader.nextString());
		}
		reader.endArray();
	}
}
