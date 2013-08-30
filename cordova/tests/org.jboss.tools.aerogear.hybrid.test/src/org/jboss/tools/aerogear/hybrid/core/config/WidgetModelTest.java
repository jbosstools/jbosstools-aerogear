package org.jboss.tools.aerogear.hybrid.core.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("restriction")
public class WidgetModelTest {

	public static String  SIMPLE_WIDGET_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
					"<widget xmlns=\"http://www.w3.org/ns/widgets\" " +
					"xmlns:gap=\"http://phonegap.com/ns/1.0\" id=\"simple widget\">"+
					"</widget>";
	
	public static String WIDGET_WITH_PLUGIN_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<widget xmlns=\"http://www.w3.org/ns/widgets\" " +
			"xmlns:gap=\"http://phonegap.com/ns/1.0\" id=\"simple widget\">"+
			"<plugin name=\"test.plugin\" version=\"1.0\">"+
			"<param name=\"param1\" value=\"value1\" />"+
			"<param name=\"param2\" value=\"value2\" />"+
		     "</plugin>"+
			"</widget>";
	
	private Document loadXMLDocument(String xml)
			throws ParserConfigurationException, UnsupportedEncodingException,
			SAXException, IOException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		Document doc = builder.parse(is);
		return doc;
	}
	
	private Node getNamedSingleNode(Document doc, String nodeName) {
		NodeList nodes = doc.getElementsByTagName(nodeName );
		assertEquals(1,nodes.getLength());
		Node node = nodes.item(0);
		assertNotNull(node);
		return node;
	}
	
	
	
	private String getAtrributeValue(Node node, String attributeName){
		NamedNodeMap attribs = node.getAttributes();
		Node attribNode = attribs.getNamedItem(attributeName);
		assertNotNull(attribNode);
		return attribNode.getNodeValue();
	}
	
	@Test
	public void testSimpleWidgetRead() throws SAXException, IOException, ParserConfigurationException{
		
		Bundle b = FrameworkUtil.getBundle(WidgetModelTest.class);
		if (b != null ){ // Because of the extra security on OSGi creating a widget directly
			return;      // only be tested on a non-OSGi runtime
		}
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Widget widget = new Widget(doc.getDocumentElement());
		assertNotNull(widget);
		assertEquals(widget.getId(), "simple widget");
	}

	@Test
	public void testSimpleWidgetModelLoad() throws UnsupportedEncodingException, 
	ParserConfigurationException, SAXException, IOException{
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Bundle b = FrameworkUtil.getBundle(WidgetModelTest.class);
		boolean osgiRunning = b!=null;
		WidgetModel model = WidgetModel.getInstance();
		Widget mw = model.load(doc);
		assertNotNull(mw);

		// If there is no OSGi running lets go the extra mile and 
		// do a comparison 
		if(!osgiRunning){
			Widget widget = new Widget(doc.getDocumentElement());
			assertNotNull(widget);
			assertEquals(mw, widget);
		}
	}
	
	@Test
	public void testWidgetAttributes() throws UnsupportedEncodingException, 
	ParserConfigurationException, SAXException, IOException{
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Widget widget = WidgetModel.getInstance().load(doc);
		widget.setId("test.id");
		widget.setVersion("1.0");
		widget.setViewmodes("testmode");
		widget.setVersion("1.0.1");
		
		assertEquals("test.id", widget.getId());
		assertEquals("1.0.1", widget.getVersion());
		assertEquals("testmode", widget.getViewmodes());
		
		Node widgetNode = getNamedSingleNode(doc, "widget");
		assertEquals(widget.getId(), getAtrributeValue(widgetNode, WidgetModelConstants.WIDGET_ATTR_ID));
		assertEquals(widget.getVersion(), getAtrributeValue(widgetNode, WidgetModelConstants.WIDGET_ATTR_VERSION));
		assertEquals(widget.getViewmodes(), getAtrributeValue(widgetNode, WidgetModelConstants.WIDGET_ATTR_VIEWMODES));
		
	}
	
	@Test
	public void testWidgetTags() throws UnsupportedEncodingException,
	ParserConfigurationException, SAXException, IOException{
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Widget widget = WidgetModel.getInstance().load(doc);
		final String name = "test.name";
		final String shortname = "test.shortname";
		final String description = "test.description";
		
		widget.setName(name);
		widget.setDescription(description);
		widget.setShortname(shortname);
		
		assertEquals(name, widget.getName());
		assertEquals(description, widget.getDescription());
		assertEquals(shortname, widget.getShortname());
		
		Node nameNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_NAME);
		assertEquals(widget.getName(), nameNode.getTextContent());
		assertEquals(widget.getShortname(), getAtrributeValue(nameNode, WidgetModelConstants.NAME_ATTR_SHORT));

		Node descNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_DESCRIPTION);
		assertEquals(widget.getDescription(), descNode.getTextContent());
	}
	
	@Test
	public void testAuthor() throws UnsupportedEncodingException,
	ParserConfigurationException, SAXException, IOException{
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Widget widget = WidgetModel.getInstance().load(doc);
		Author author = WidgetModel.getInstance().createAuthor(widget);
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

		Node authorNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_AUTHOR);
		assertEquals(author.getName(), authorNode.getTextContent());
		assertEquals(author.getHref(), getAtrributeValue(authorNode, WidgetModelConstants.AUTHOR_ATTR_HREF));
		assertEquals(author.getEmail(), getAtrributeValue(authorNode, WidgetModelConstants.AUTHOR_ATTR_EMAIL));
		
	}
	
	@Test
	public void testContent() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException{
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Widget widget = WidgetModel.getInstance().load(doc);
		Content content = WidgetModel.getInstance().createContent(widget);
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
		
		Node contentNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_CONTENT);
		assertEquals(content.getSrc(), getAtrributeValue(contentNode, WidgetModelConstants.CONTENT_ATTR_SRC));
		assertEquals(content.getType(),getAtrributeValue(contentNode, WidgetModelConstants.CONTENT_ATTR_TYPE));
		assertEquals(content.getEncoding(), getAtrributeValue(contentNode, WidgetModelConstants.CONTENT_ATTR_ENCODING));
	}
	
	@Test
	public void testLicense() throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException{
		Document doc = loadXMLDocument(SIMPLE_WIDGET_XML);
		Widget widget = WidgetModel.getInstance().load(doc);
		License license = WidgetModel.getInstance().createLicense(widget);
		final String text = "test.text";
		final String href = "test.href";
		
		license.setHref(href);
		license.setText(text);
		assertEquals(text, license.getText());
		assertEquals(href, license.getHref());
		
		widget.setLicense(license);
		assertEquals(license, widget.getLicense());

		Node licenseNode = getNamedSingleNode(doc, WidgetModelConstants.WIDGET_TAG_LICENSE);
		assertEquals(license.getText(), licenseNode.getTextContent());
		assertEquals(license.getHref(), getAtrributeValue(licenseNode, WidgetModelConstants.LICENSE_ATTR_HREF));
		
	}
	
	
}
	
