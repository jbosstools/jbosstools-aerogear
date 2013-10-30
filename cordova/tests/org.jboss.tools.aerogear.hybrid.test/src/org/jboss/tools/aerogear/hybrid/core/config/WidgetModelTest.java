package org.jboss.tools.aerogear.hybrid.core.config;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.aerogear.hybrid.test.TestProject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("restriction")
public class WidgetModelTest {

	private  TestProject project;
	
	@Before
	public void setUpTestProject(){
		project = new TestProject();
	}
	
	@After
	public void cleanProject() throws CoreException{
		if(this.project != null ){
			this.project.delete();
			this.project = null;
		}
	}
		
	private Document loadXMLDocument(InputStream xml)
			throws ParserConfigurationException, UnsupportedEncodingException,
			SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Document doc = builder.parse(xml);
		return doc;
	}
	
	private Node getNamedSingleNode(Document doc, String nodeName) {
		NodeList nodes = doc.getElementsByTagName(nodeName );
		assertEquals(1,nodes.getLength());
		Node node = nodes.item(0);
		assertNotNull(node);
		return node;
	}
	
	
	
	private String getAttributeValue(Node node, String attributeName){
		NamedNodeMap attribs = node.getAttributes();
		Node attribNode = attribs.getNamedItem(attributeName);
		assertNotNull(attribNode);
		return attribNode.getNodeValue();
	}
	
	@Test
	public void testSimpleWidgetRead() throws CoreException{
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget rm = model.getWidgetForRead();
		assertNotNull(rm);
	}

	@Test
	public void testSimpleWidgetEdit() throws CoreException{
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget em = model.getWidgetForEdit();
		assertNotNull(em);
	}
	
	@Test
	public void testWidgetAttributes() throws UnsupportedEncodingException, 
	ParserConfigurationException, SAXException, IOException, CoreException{
		
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();
		
		widget.setId("test.id");
		widget.setVersion("1.0");
		widget.setViewmodes("testmode");
		widget.setVersion("1.0.1");
		model.save();
		
		assertEquals("test.id", widget.getId());
		assertEquals("1.0.1", widget.getVersion());
		assertEquals("testmode", widget.getViewmodes());
		
		Document doc = getConfigXMLDocument();
		Node widgetNode = getNamedSingleNode(doc, "widget");
		
		assertEquals(widget.getId(), getAttributeValue(widgetNode, WidgetModelConstants.WIDGET_ATTR_ID));
		assertEquals(widget.getVersion(), getAttributeValue(widgetNode, WidgetModelConstants.WIDGET_ATTR_VERSION));
		assertEquals(widget.getViewmodes(), getAttributeValue(widgetNode, WidgetModelConstants.WIDGET_ATTR_VIEWMODES));
		
	}

	private Document getConfigXMLDocument()
			throws ParserConfigurationException, UnsupportedEncodingException,
			SAXException, IOException, CoreException {
		IFile file = project.getProject().getFile("/www/config.xml");
		Document doc = loadXMLDocument(file.getContents());
		return doc;
	}
	
	@Test
	public void testWidgetTags() throws UnsupportedEncodingException,
	ParserConfigurationException, SAXException, IOException, CoreException{
		
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();

		final String name = "test.name";
		final String shortname = "test.shortname";
		final String description = "test.description";
		
		widget.setName(name);
		widget.setDescription(description);
		widget.setShortname(shortname);
		model.save();
		
		assertEquals(name, widget.getName());
		assertEquals(description, widget.getDescription());
		assertEquals(shortname, widget.getShortname());
		
		
		Document doc = getConfigXMLDocument();
		Node nameNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_NAME);
		assertEquals(widget.getName(), nameNode.getTextContent());
		assertEquals(widget.getShortname(), getAttributeValue(nameNode, WidgetModelConstants.NAME_ATTR_SHORT));

		Node descNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_DESCRIPTION);
		assertEquals(widget.getDescription(), descNode.getTextContent());
	}
	
	@Test
	public void testAuthor() throws UnsupportedEncodingException,
	ParserConfigurationException, SAXException, IOException, CoreException{
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();

		Author author = model.createAuthor(widget);
		final String name = "test.name";
		final String href = "test.href";
		final String email = "test.email";
		
		author.setName(name);
		author.setHref(href);
		author.setEmail(email);
		
		assertEquals(name, author.getName());
		assertEquals(href, author.getHref());
		assertEquals(email, author.getEmail());
		
		widget.setAuthor(author);
		assertEquals(author, widget.getAuthor());
		model.save();

		Document doc = getConfigXMLDocument();
		Node authorNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_AUTHOR);
		assertEquals(author.getName(), authorNode.getTextContent());
		assertEquals(author.getHref(), getAttributeValue(authorNode, WidgetModelConstants.AUTHOR_ATTR_HREF));
		assertEquals(author.getEmail(), getAttributeValue(authorNode, WidgetModelConstants.AUTHOR_ATTR_EMAIL));
		
	}
	
	@Test
	public void testContent() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, CoreException{
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();

		Content content = model.createContent(widget);
		final String src = "test.src";
		final String encoding = "test.encoding";
		final String type = "test.type";
		
		content.setSrc(src);
		content.setType(type);
		content.setEncoding(encoding);
		assertEquals(src, content.getSrc());
		assertEquals(type, content.getType());
		assertEquals(encoding, content.getEncoding());
		
		widget.setContent(content);
		assertEquals(content, widget.getContent());
		model.save();
		
		Document doc = getConfigXMLDocument();
		Node contentNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_CONTENT);
		assertEquals(content.getSrc(), getAttributeValue(contentNode, WidgetModelConstants.CONTENT_ATTR_SRC));
		assertEquals(content.getType(),getAttributeValue(contentNode, WidgetModelConstants.CONTENT_ATTR_TYPE));
		assertEquals(content.getEncoding(), getAttributeValue(contentNode, WidgetModelConstants.CONTENT_ATTR_ENCODING));
	}
	
	@Test 
	public void testFeature() throws CoreException, IOException, ParserConfigurationException, SAXException{
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();
		
		Feature feature = model.createFeature(widget);
		String name = "org.aerogear.plugin.test";
		feature.setName(name);
		assertEquals(name, feature.getName());
		assertNotNull(feature.getParams());
		assertTrue(feature.getParams().isEmpty());
		assertFalse(feature.getRequired());
		widget.addFeature(feature);
		model.save();
		
		Document doc = getConfigXMLDocument();
		NodeList list = doc.getDocumentElement().getElementsByTagName("feature");
		for (int i = 0; i < list.getLength(); i++) {
			Node curr = list.item(i);
			String nameAtt = getAttributeValue(curr, WidgetModelConstants.FEATURE_ATTR_NAME);
			if(name.equals(nameAtt)){
				return;
			}
		}
		fail("Inserted feature is not persisted");
	}
	
	@Test
	public void testLicense() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, CoreException{
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();

		License license = model.createLicense(widget);
		final String text = "test.text";
		final String href = "test.href";
		
		license.setHref(href);
		license.setText(text);
		assertEquals(text, license.getText());
		assertEquals(href, license.getHref());
		
		widget.setLicense(license);
		assertEquals(license, widget.getLicense());
		model.save();

		Document doc = getConfigXMLDocument();
		Node licenseNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_LICENSE);
		assertEquals(license.getText(), licenseNode.getTextContent());
		assertEquals(license.getHref(), getAttributeValue(licenseNode, WidgetModelConstants.LICENSE_ATTR_HREF));
	}
	
	@Test
	public void testUpdateToXML() throws IOException, ParserConfigurationException, SAXException, IOException, CoreException, TransformerException{
		
		Document doc = getConfigXMLDocument();
		doc.getDocumentElement().setAttribute(WidgetModelConstants.WIDGET_ATTR_ID, "rev.id");
		doc.getDocumentElement().setAttribute(WidgetModelConstants.WIDGET_ATTR_VERSION, "rev.ver");
		IFile file = project.getProject().getFile("/www/config.xml");
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer xformer = transformerFactory.newTransformer();
		StringWriter stringWriter = new StringWriter();
		StreamResult result = new StreamResult(stringWriter);
		
		xformer.transform(new DOMSource(doc), result);
		file.setContents(new ByteArrayInputStream(stringWriter.getBuffer().toString().getBytes()),
				IResource.FORCE, new NullProgressMonitor());

		
		WidgetModel model = WidgetModel.getModel(project.hybridProject());
		Widget widget = model.getWidgetForEdit();
		
		assertEquals("rev.id",widget.getId());
		assertEquals("rev.ver", widget.getVersion());
		
		
	}
	
}
	
