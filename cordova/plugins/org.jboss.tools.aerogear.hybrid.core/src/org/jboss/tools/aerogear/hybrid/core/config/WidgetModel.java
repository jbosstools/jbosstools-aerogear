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

import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.NS_W3C_WIDGET;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.NS_PHONEGAP_1_0;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_ACCESS;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_AUTHOR;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_CONTENT;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_FEATURE;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_PREFERENCE;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_PLUGIN;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_SPLASH;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_ICON;
import static org.jboss.tools.aerogear.hybrid.core.config.WidgetModelConstants.WIDGET_TAG_LICENSE;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Manager for all access to the config.xml (Widget) model. 
 * Model is strictly tied with the {@link Document} that 
 * it was created for. Reading, persistance etc of the Document 
 * object used to create Widget model are responsibility of the 
 * caller.
 *  
 * @author Gorkem Ercan
 *
 */
public class WidgetModel {

	public static final String[] ICON_EXTENSIONS = {"gif", "ico", "jpeg", "jpg", "png","svg" };
	
	private static volatile WidgetModel instance;
	private Map<Document , Widget> models = new HashMap<Document, Widget>();

	private WidgetModel(){
		//no instances
	}
	
	
	public static WidgetModel getInstance(){
		if (instance == null ){
			instance= new WidgetModel();
		}
		return instance;
	}

	public Widget load(Document document) {
		Widget root = models.get(document);
		if(root == null ){
			root = new Widget(document.getDocumentElement());
		}else{
			root.reload(document.getDocumentElement());
		}
		models.put(document, root);
		return root;
	}
	
	public void save(Widget root, File file) throws CoreException{
		try {
			Source source = new DOMSource(root.itemNode.getOwnerDocument());

			StreamResult result = new StreamResult(file);

			// Write the DOM document to the file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer xformer;
			xformer = transformerFactory.newTransformer();
			xformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	/**
	 * Creates an {@link Author} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Author
	 */
	public Author createAuthor(Widget widget){
		return createObject(widget, NS_W3C_WIDGET, WIDGET_TAG_AUTHOR, Author.class);
	}
	/**
	 * Creates a {@link Content} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return  new Content
	 */
	public Content createContent(Widget widget){
		return createObject(widget, NS_W3C_WIDGET, WIDGET_TAG_CONTENT, Content.class);
	}
	/**
	 * Creates a {@link Preference} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Preference 
	 */
	public Preference createPreference(Widget widget){
		return createObject(widget, NS_W3C_WIDGET, WIDGET_TAG_PREFERENCE, Preference.class);
	}
	/**
	 * Creates a {@link Feature} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Feature
	 */
	public Feature createFeature(Widget widget){
		return createObject(widget, NS_W3C_WIDGET, WIDGET_TAG_FEATURE, Feature.class);
	}
	/**
	 * Creates a {@link Access} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Access 
	 */
	public Access createAccess(Widget widget){
		return createObject(widget, NS_W3C_WIDGET, WIDGET_TAG_ACCESS, Access.class);
	}
	
	/**
	 * Creates a {@link Plugin} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Plugin 
	 */
	public Plugin createPlugin(Widget widget){
		return createObject(widget,NS_PHONEGAP_1_0,WIDGET_TAG_PLUGIN,Plugin.class);
	}
	
	/**
	 * Creates a {@link Plugin} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Splash 
	 */	
	public Icon createIcon(Widget widget){
		return createObject(widget,NS_W3C_WIDGET, WIDGET_TAG_ICON,Icon.class);
	}
	
	/**
	 * Creates a {@link Plugin} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new Splash 
	 */
	public Splash createSplash(Widget widget){
		return createObject(widget, NS_PHONEGAP_1_0, WIDGET_TAG_SPLASH, Splash.class);
	}
	
	/**
	 * Creates a {@link License} instance. This also creates the necessary 
	 * DOM elements on the {@link Document} associated with the widget.
	 * The new DOM elements are not inserted to the tree until 
	 * {@link Widget}'s proper set/add method is called
	 * 
	 * @param widget - parent widget
	 * @return new License
	 */
	public License createLicense(Widget widget){
		return createObject(widget, NS_W3C_WIDGET, WIDGET_TAG_LICENSE, License.class);
	}
	
	
	private <T extends AbstractConfigObject> T createObject(Widget widget, String namespace, String tag, Class<T> clazz ){
		Document doc = widget.itemNode.getOwnerDocument();
		if (doc == null )
			throw new IllegalStateException("Widget is not properly constructed");
		Element el = doc.createElementNS(namespace, tag);
		
		try {
			return clazz.getDeclaredConstructor(Node.class).newInstance(el);
		} catch (Exception e){
			HybridCore.log(IStatus.ERROR, "Error invoking the Node constructor for config model object", e);
		}
		return null;
	}
	
}
