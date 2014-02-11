/*******************************************************************************
 * Copyright (c) 2013, 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.aerogear.hybrid.core.plugin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidPluginInstallationActionsFactory;
import org.jboss.tools.aerogear.hybrid.android.core.adt.AndroidPluginInstallationActionsFactory.AndroidSourceFileAction;
import org.jboss.tools.aerogear.hybrid.core.plugin.actions.CopyFileAction;
import org.jboss.tools.aerogear.hybrid.core.plugin.actions.XMLConfigFileAction;
import org.jboss.tools.aerogear.hybrid.test.TestUtils;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InstallActionsTest {
	
	@Test
	public void testCopyFileActionInstallNUninstall() throws FileNotFoundException, IOException, CoreException{
		File source = TestUtils.createTempFile(TestUtils.FILE_PLAIN);
		File target = new File( TestUtils.getTempDirectory(),"some/other/dir" );
		CopyFileAction action = new CopyFileAction(source, target);
		action.install();
		File targetFile = new File(target, source.getName());
		assertTrue(targetFile.exists());
		action =  new CopyFileAction(source, target);
		action.unInstall();
		assertFalse(targetFile.exists());
	}
	
	@Test
	public void testCopyFileActionNullValues(){
		try{
			new CopyFileAction(null, null);
		}catch(AssertionFailedException e){
			return;
		}
		assertTrue("CopyFileAction was created with null values no exception is thrown", true);
	}
	
	@SuppressWarnings("restriction")
	@Test
	public void testAndroidSourceFileActionTest() throws FileNotFoundException, IOException, CoreException{
		File source = TestUtils.createTempFile(TestUtils.FILE_PLAIN);
		File d1 = new File(TestUtils.getTempDirectory(),"some");
		File d2 = new File(d1, "other");
		File target = new File(d2, "dir");
		AndroidPluginInstallationActionsFactory.AndroidSourceFileAction action = new AndroidSourceFileAction(source, target);
		action.install();
		File targetFile = new File(target, source.getName());
		assertTrue(targetFile.exists());
		action = new AndroidSourceFileAction(source, target); 
		action.unInstall();
		assertFalse(targetFile.exists());
		assertFalse(d2.exists());
		assertFalse(d1.exists());
	}
	
	@Test
	public void testXMLConfigFileActionInstall() throws FileNotFoundException, IOException, CoreException, SAXException,
	ParserConfigurationException, XPathExpressionException{
		
		final File target= TestUtils.createTempFile("AndroidManifest.xml");
		final String xml = "<config-file target=\"AndroidManifest.xml\" parent=\"/manifest\">"
		+ "<uses-permission android:name=\"android.permission.BLUETOOTH\" />"
		+ "<uses-permission android:name=\"android.permission.BLUETOOTH_ADMIN\" />"
		+ "</config-file >";
		final String parentExpression = "/manifest";
		
		XMLConfigFileAction action = new XMLConfigFileAction(target, parentExpression, xml);
		action.install();
		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(target);
		Document node = db.parse(new InputSource(new StringReader(xml)));
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node parentNode = (Node)xpath.evaluate(parentExpression,doc, XPathConstants.NODE);
		assertNotNull(parentNode);
		
		NodeList alienChildren = node.getDocumentElement().getChildNodes();
		
		Node[] importedNodes = new Node[alienChildren.getLength()];
		for (int i = 0; i < alienChildren.getLength(); i++) {
			importedNodes[i] = doc.importNode(alienChildren.item(i), true);
		}
		
		
		NodeList childNodes = parentNode.getChildNodes();
		int found = 0;
		for(int i=0; i<childNodes.getLength(); i++){
			Node current = childNodes.item(i);
			for (int j = 0; j < importedNodes.length; j++) {
				if(current.isEqualNode(importedNodes[j])){
 					found++;
				}
			}
		}
		assertEquals( importedNodes.length, found); // found all imported nodes
	}
	
	@Test	
	public void testXMLConfigFileActionUnInstall() throws IOException,
			CoreException, ParserConfigurationException, SAXException,
			XPathException {
		final File target = TestUtils.createTempFile("AndroidManifest.xml");
		final String xml = "<config-file target=\"AndroidManifest.xml\" parent=\"/manifest\">"
				+ "<uses-permission android:name=\"android.permission.INTERNET\" />"
				+ "<uses-permission android:name=\"android.permission.RECEIVE_SMS\" />"
				+ "</config-file >";
		final String parentExpression = "/manifest";

		XMLConfigFileAction action = new XMLConfigFileAction(target,
				parentExpression, xml);
		action.unInstall();

		DocumentBuilder db = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = db.parse(target);
		Document node = db.parse(new InputSource(new StringReader(xml)));

		XPath xpath = XPathFactory.newInstance().newXPath();
		Node parentNode = (Node) xpath.evaluate(parentExpression, doc,
				XPathConstants.NODE);
		assertNotNull(parentNode);

		NodeList alienChildren = node.getDocumentElement().getChildNodes();

		Node[] importedNodes = new Node[alienChildren.getLength()];
		for (int i = 0; i < alienChildren.getLength(); i++) {
			importedNodes[i] = doc.importNode(alienChildren.item(i), true);
		}

		NodeList childNodes = parentNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node current = childNodes.item(i);
			for (int j = 0; j < importedNodes.length; j++) {
				assertFalse("Found a node that is not suppposed to be here", current.isEqualNode(importedNodes[j]));
			}
		}
	}

}
