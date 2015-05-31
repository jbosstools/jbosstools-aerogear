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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.feedhenry.ui.internal.FHErrorHandler;
import org.jboss.tools.feedhenry.ui.internal.util.HttpUtil;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
/**
 * Wrapper to FeedHenry Platform APIs
 *
 */
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
	
	public List<FeedHenryProject> listProjects(IProgressMonitor monitor) throws FeedHenryException{
		if(monitor == null ){
			monitor = new NullProgressMonitor();
		}
		SubMonitor sm = SubMonitor.convert(monitor, "Retrieve FeedHenry project list", 100);
		String json = doGetAPICall("/box/api/projects", sm.newChild(50));
		if(json == null ){
			return null;
		}
		JsonValue value = JsonValue.readFrom(json);
		if(value.isArray()){
			List<FeedHenryProject> projects = new ArrayList<FeedHenryProject>();
			JsonArray array = value.asArray();
			sm.setWorkRemaining(array.size());
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
				sm.worked(1);
			}
			return projects;
		}
		return null;
	}
	
	public FeedHenryApplication importBareRepo(String projectId, String appTitle, String appTemplateType, IProgressMonitor monitor) throws FeedHenryException{
		final String url =  "box/api/projects/" + projectId + "/apps";
		if(monitor == null ){
			monitor = new NullProgressMonitor();
		}
		SubMonitor sm = SubMonitor.convert(monitor,"Create FeedHenry Project",100);
		
		JsonObject payload = new JsonObject();
		payload.add("title", appTitle);
		payload.add("connections", new JsonArray());
		JsonObject template = new JsonObject();
		template.add("type", appTemplateType);
		template.add("initaliseRepo", false);
		payload.add("template", template);
		sm.worked(10);
		String response = doPostAPICall(url, payload.toString(), sm.newChild(70));
		
		JsonValue value = JsonValue.readFrom(response);
		sm.worked(25);
		FeedHenryApplication fha = new FeedHenryApplication();
		JsonObject object = value.asObject();
		fha.setTitle(object.get("title").asString());
		fha.setType(object.get("type").asString());
		fha.setRepoUrl(object.get("internallyHostedRepoUrl").asString());
		fha.setGuid(object.get("guid").asString());
		sm.worked(5);
		return fha;
		
	}
	
	private String doPostAPICall(String api, String payload, IProgressMonitor monitor) throws FeedHenryException{
		SubMonitor sm = SubMonitor.convert(monitor,100);
		 DefaultHttpClient httpClient = new DefaultHttpClient();
		 HttpUtil.setupProxy(httpClient);
		 HttpPost post = new HttpPost(this.fhURL.toString()+ api);
		 post.addHeader("X-FH-AUTH-USER", this.apikey);
		 post.addHeader("Content-Type", "application/json");
		 post.setEntity(new StringEntity(payload,ContentType.APPLICATION_JSON));
		 HttpResponse response;
		 try{
			 if(monitor.isCanceled()){
				 throw new OperationCanceledException();
			 }
			 response = httpClient.execute(post);
			 sm.worked(50);
			 int status = response.getStatusLine().getStatusCode();
			 HttpEntity entity = response.getEntity();
			 InputStream stream = entity.getContent();
			 Long contentLength = entity.getContentLength();
			 sm.setWorkRemaining((int)(contentLength/1024));
			 

			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 byte[] buffer = new byte[1024];
			 int length = 0;
			 while ((length = stream.read(buffer)) != -1) {
				 baos.write(buffer, 0, length);
				 sm.worked(1);
			 }
			 String json = new String(baos.toByteArray());
			 if( status != 200 &&  status != 201){
				 throw new FeedHenryException(status, response.getStatusLine().getReasonPhrase());
			 }

			 return json;

		 }catch(IOException e){
				FeedHenryException fe = new FeedHenryException(FHErrorHandler.ERROR_CONNECTION_API_CALL,
						NLS.bind("Unexpected error while communicating with {0}",fhURL.getHost()));
				fe.initCause(e);
				throw fe;
		 }
	}
	
	
	private String doGetAPICall(String api, IProgressMonitor monitor ) throws FeedHenryException{
		SubMonitor sm = SubMonitor.convert(monitor,100);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpUtil.setupProxy(httpClient);
		HttpGet get = new HttpGet(this.fhURL.toString() + api);
		get.addHeader("X-FH-AUTH-USER", this.apikey);
		HttpResponse response;
		
		try {
			if(monitor.isCanceled() ){
				throw new OperationCanceledException();
			}
			sm.worked(1);
			response = httpClient.execute(get);
			sm.worked(50);
			int status = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			Long contentLength = entity.getContentLength();
			sm.setWorkRemaining((int)(contentLength/1024));
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = stream.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
				sm.worked(1);
			}
			String json = new String(baos.toByteArray());
			if( status != 200 ){
				throw new FeedHenryException(status, response.getStatusLine().getReasonPhrase());
			}

			return json;
		} catch (IOException e) {
			FeedHenryException fe = new FeedHenryException(FHErrorHandler.ERROR_CONNECTION_API_CALL,
					NLS.bind("Unexpected error while communicating with {0}",fhURL.getHost()));
			fe.initCause(e);
			throw fe;
		}
	}
}
