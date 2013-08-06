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
	private List<CordovaPlugin> plugins;
	
	public List<CordovaPlugin> retrievePluginList()
	{
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://registry.cordova.io/-/all");
		HttpResponse response;
		
	
		
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			JsonReader reader = new JsonReader(new InputStreamReader(stream));
			reader.beginObject();//start the Regsitry
			plugins = new ArrayList<CordovaPlugin>();
			while(reader.hasNext()){
				JsonToken token = reader.peek();
				switch (token) {
				case BEGIN_OBJECT:
					plugins.add(readPlugin(reader));
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

	private CordovaPlugin readPlugin(JsonReader reader) throws IOException {
		CordovaPlugin plugin = null;
		reader.beginObject();
		plugin = new CordovaPlugin();

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
				break;
			}

			default:
				reader.skipValue();
				break;
			}
		}
		reader.endObject();
		Assert.isNotNull(plugin);
		return plugin;
	}

	private void parseMaintainers(JsonReader reader, CordovaPlugin plugin) throws IOException{
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

	private void parseKeywords(JsonReader reader, CordovaPlugin plugin) throws IOException{
		reader.beginArray();
		while(reader.hasNext()){
			plugin.addKeyword(reader.nextString());
		}
		reader.endArray();
	}
}
