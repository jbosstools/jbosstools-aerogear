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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.aerogear.hybrid.core.HybridProject;
import org.jboss.tools.aerogear.hybrid.core.config.Feature;
import org.jboss.tools.aerogear.hybrid.core.config.Widget;
import org.jboss.tools.aerogear.hybrid.core.config.WidgetModel;
import org.jboss.tools.aerogear.hybrid.core.platform.PlatformConstants;
import org.jboss.tools.aerogear.hybrid.core.util.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigXMLUpdateAction extends XMLConfigFileAction {
	
	private final HybridProject project;

	public ConfigXMLUpdateAction(HybridProject project, String parent, String value) {
		super(project.getProject().getFile(PlatformConstants.DIR_WWW+"/"+PlatformConstants.FILE_XML_CONFIG).getLocation().toFile(), parent,value);
		this.project = project;
	}

	@Override
	public void install() throws CoreException{
		Document doc = XMLUtil.loadXML(xml);
		Element element = doc.getDocumentElement();
		NodeList featureNodes = element.getElementsByTagName("feature");
		Element featureNode = null;
		if(featureNodes.getLength() == 1){
			featureNode = (Element) featureNodes.item(0);
		}
		if(featureNode == null ){// let parent handle it
			super.install();
		}else{
			Widget widget = this.project.getWidget();
			Feature feature = getExistingFeature(featureNode, widget);
			if(feature == null ){
				feature = WidgetModel.getInstance().createFeature(widget);
				feature.setName(featureNode.getAttribute("name"));
				String required = featureNode.getAttribute("required");
				if(required != null) {
					feature.setRequired(Boolean.parseBoolean(required));
				}
				widget.addFeature(feature);
			}
			NodeList nodes = featureNode.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node currNode = nodes.item(i);
				if(currNode.getNodeName().equals("param")){
					Element el = (Element)currNode;
					feature.addParam(el.getAttribute("name"), el.getAttribute("value"));
				}
			}
			WidgetModel.getInstance().save(widget, target);
		}
	}

	private Feature getExistingFeature(Element element, Widget widget) {
		String featureName = element.getAttribute("name");
		List<Feature> features = widget.getFeatures();
		if(features == null ) return null;
		for (Feature feature : features) {
			if(feature.getName().equals(featureName)){
				return feature;
			}
		}
		return null;
	}

	@Override
	public void unInstall() throws CoreException {
		super.unInstall();

	}

}
