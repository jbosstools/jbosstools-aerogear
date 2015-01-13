/*******************************************************************************
 * Copyright (c) 2014,2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class FeedHenry {
	private URL fhURL;
	private String apikey;

	public FeedHenry setFeedHenryURL(URL url){
		this.fhURL = url;
		return this;
	}
	
	public FeedHenry setAPIKey(String key) {
		this.apikey = key;
		return this;
	}
	
	public List<FeedHenryProject> listProjects(){
		String json = doAPICall("/box/api/projects");
		if(json == null ){
			return null;
		}
		JsonValue value = JsonValue.readFrom(json);
		if(value.isArray()){
			List<FeedHenryProject> projects = new ArrayList<FeedHenryProject>();
			JsonArray array = value.asArray();
			for (int i = 0; i < array.size(); i++) {
				FeedHenryProject fhp = new FeedHenryProject();
				JsonObject project = array.get(i).asObject();
				fhp.setTitle(project.get("title").asString());
				fhp.setGuid(project.get("guid").asString());
				JsonArray apps = project.get("apps").asArray();
				List<FeedHenryApplication> fhapps = new ArrayList<FeedHenryApplication>();
				for (int j = 0; j < apps.size(); j++) {
					JsonObject app = apps.get(j).asObject();
					FeedHenryApplication fha = new FeedHenryApplication();
					fha.setTitle(app.get("title").asString());
					fha.setType(app.get("type").asString());
					fha.setRepoUrl(app.get("internallyHostedRepoUrl").asString());
					fha.setGuid(app.get("guid").asString());
					fhapps.add(fha);
				}
				fhp.setApplications(fhapps);
				projects.add(fhp);
			}
			return projects;
		}
		return null;
	}
	
	private String doAPICall(String api ){
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUtil.setupProxy(httpClient);
		HttpGet get = new HttpGet(this.fhURL.toString() + api);
		get.addHeader("X-FH-AUTH-USER", this.apikey);
		HttpResponse response;
		
		try {
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = stream.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			String json = new String(baos.toByteArray());
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
