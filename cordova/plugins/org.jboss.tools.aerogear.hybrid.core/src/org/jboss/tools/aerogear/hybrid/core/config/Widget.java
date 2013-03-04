/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.config;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class Widget extends AbstractConfigObject {

	
	static final String NS_PHONEGAP_1_0 = "http://phonegap.com/ns/1.0";
	private String id;
	private String version;
	private int versionCode;
	private String name;
	private String description;
	private Author author;
	private List<Preference> preferences;
	private List<Access> accesses;
	private List<Feature> features;
	private List<Plugin> plugins;
	private List<Icon> icons;
	private List<Splash> splashes;
	

	Widget (Node node) {
		id = getNodeAttribute(node, "id");
		version = getNodeAttribute(node, "version");
		
		String vcode = getNodeAttribute(node, "versionCode");
		if (vcode != null) {
			try {
				versionCode = Integer.parseInt(getNodeAttribute(node,
						"versionCode"));
			} catch (NumberFormatException nfe) {
				// TODO: handle invalid versionCode values
			}
		}
	
		
		loadName(node);
		loadDescription(node);
		loadAuthor(node);
		loadPreferences(node);
		loadAccess(node);
		loadFeatures(node);
		loadPlugins(node);
		loadIcons(node);
		loadSplashes(node);
		
	}


	private void loadSplashes(Node node) {
		splashes = new ArrayList<Splash>();
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagNameNS(NS_PHONEGAP_1_0, "splash");
		for( int i = 0; i< nodes.getLength(); i++ ){
			Splash splash = new Splash(nodes.item(i));
			splashes.add(splash);
		}
		
	}

	private void loadIcons(Node node) {
		icons = new ArrayList<Icon>();
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName("icon");
		for(int i=0; i < nodes.getLength() ; i++ ){
			Icon icon = new Icon(nodes.item(i));
			icons.add(icon);
		}
	}
	

	private void loadPlugins(Node node) {
		plugins = new ArrayList<Plugin>();
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagNameNS(NS_PHONEGAP_1_0,"plugin");
		for(int i= 0 ; i<nodes.getLength(); i++){
			Plugin plugin = new Plugin(nodes.item(i));
			plugins.add(plugin);
		}
	}

	private void loadFeatures(Node node) {
		features = new ArrayList<Feature>();
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName("feature");
		for(int i = 0; i < nodes.getLength() ; i++){
			Feature feature = new Feature(nodes.item(i));
			features.add(feature);
		}
		
	}

	private void loadAccess(Node node) {
		accesses = new ArrayList<Access>();
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName("access");
		for(int i = 0; i< nodes.getLength() ; i++){
			Access access = new Access(nodes.item(i));
			accesses.add(access);
		}
		
	}

	private void loadPreferences(Node node) {
		preferences = new ArrayList<Preference>();
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName("preference");
		for(int i = 0; i< nodes.getLength() ; i++){
			Preference preference = new Preference(nodes.item(i));
			preferences.add(preference);
		}
		
	}

	private void loadAuthor(Node node) {
		Element el = (Element)node;
		NodeList nodes = el.getElementsByTagName("author");
		if(nodes.getLength()>0){
			author = new Author(nodes.item(0));
		}
		
	}

	private void loadDescription(Node node) {
		description = getTextContent(node, "description");
	}

	private void loadName(Node node) {
		name =getTextContent(node, "name");
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}
	
	public List<Preference> getPreferences() {
		return preferences;
	}
	
	public List<Access> getAccesses() {
		return accesses;
	}
	
	public List<Feature> getFeatures() {
		return features;
	}
	
	public List<Plugin> getPlugins() {
		return plugins;
	}
	
	public List<Icon> getIcons() {
		return icons;
	}
	
	public List<Splash> getSplashes() {
		return splashes;
	}
}
