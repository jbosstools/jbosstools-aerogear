package org.jboss.tools.aerogear.hybrid.test.ios.pbxproject;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.jboss.tools.aerogear.hybrid.ios.core.pbxproject.PBXFile;
import org.jboss.tools.aerogear.hybrid.ios.core.pbxproject.PBXProject;
import org.jboss.tools.aerogear.hybrid.ios.core.pbxproject.PBXProjectException;
import org.jboss.tools.aerogear.hybrid.test.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dd.plist.ASCIIPropertyListParser;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;

public class PBXProjectTest {
	private static final String DEFAULT_GROUP = "<group>";
	private static File pbxFile;
	private static final String SOURCE_FILE = "sourcecode.c.objc";
	private static final String HEADER_FILE = "sourcecode.c.h";
	
	@BeforeClass
	public static void setFiles() throws IOException{
		File tempDir = TestUtils.getTempDirectory();
		pbxFile = TestUtils.createTempFile("project.pbxproj");
	}
	
	@Test
	public void testPBXFileDefaults(){
		PBXFile pbxFile = new PBXFile("/my/test/file.m");
		assertEquals("4", pbxFile.getEncoding());
		assertEquals(SOURCE_FILE, pbxFile.getLastType());
		assertEquals(DEFAULT_GROUP, pbxFile.getSourceTree());
		assertEquals("Sources", pbxFile.getGroup());
		assertNull(pbxFile.getCompilerFlags());
		assertNotNull(pbxFile.getFileRef());
	}
	
	@Test
	public void testAddPluginFile() throws Exception{
		PBXProject project = new PBXProject(pbxFile);
		String testPath = "my/files/abc.h";
		PBXFile file = new PBXFile(testPath);
		project.addPluginFile(file);
		
		
		NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(project.getContent().getBytes());
		NSDictionary objects = (NSDictionary)dict.objectForKey("objects");
		NSDictionary fileRef = (NSDictionary) objects.objectForKey(file.getFileRef());
		assertNotNull(fileRef);
		NSString isa = (NSString)fileRef.get("isa");
		assertEquals("PBXFileReference",isa.getContent());
		NSString path = (NSString)fileRef.get("path");
		assertEquals(testPath, path.getContent());
		NSString lastType = (NSString)fileRef.get("lastKnownFileType");
		assertEquals(HEADER_FILE, lastType.getContent());
		NSString encoding = (NSString)fileRef.get("fileEncoding");
		assertEquals("4", encoding.getContent());
		NSString sourceTree = (NSString)fileRef.get("sourceTree");
		assertEquals(DEFAULT_GROUP, sourceTree.getContent());
		
		NSDictionary group = getGroupByName(objects, "Plugins");
		NSArray children = (NSArray) group.objectForKey("children");
		boolean groupFound = false;
		NSObject[] childs = children.getArray();
		for (int i = 0; i < childs.length; i++) {
			NSString str = (NSString)childs[i];
			if(str.getContent().equals(file.getFileRef())){
				groupFound = true;
				break;
			}
		}
		assertTrue("No entry found on the Plugins group",groupFound);
		
	}
	
	@Test
	public void testAddSourceFile() throws Exception{
		PBXProject project = new PBXProject(pbxFile);
		String testPath = "my/files/abcd.h";
		PBXFile file = new PBXFile(testPath);
		project.addSourceFile(file);
		

		NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(project.getContent().getBytes());
		NSDictionary objects = (NSDictionary)dict.objectForKey("objects");
		NSDictionary buildFile = (NSDictionary)objects.objectForKey(file.getUuid());
		assertNotNull(buildFile);
		NSString isa = (NSString) buildFile.get("isa");
		assertEquals("PBXBuildFile",isa.getContent());
		NSString fileRef = (NSString) buildFile.get("fileRef");
		assertEquals(file.getFileRef(), fileRef.getContent());
		
		NSDictionary phase = getPhase(objects, "PBXSourcesBuildPhase");
		NSArray files = (NSArray) phase.get("files");
		assertTrue(files.containsObject(new NSString(file.getUuid())));
		
	}
	
	@Test
	public void testAddFramework() throws Exception{
		PBXProject project = new PBXProject(pbxFile);
		String testPath = "my/files/abcd.h";
		PBXFile file = new PBXFile(testPath);
		project.addFramework(file);
		
		
		NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(project.getContent().getBytes());
		NSDictionary objects = (NSDictionary)dict.objectForKey("objects");
		NSDictionary fileRef = (NSDictionary) objects.objectForKey(file.getFileRef());
		assertNotNull(fileRef);
		NSString isa = (NSString)fileRef.get("isa");
		assertEquals("PBXFileReference",isa.getContent());
		NSString path = (NSString)fileRef.get("path");
		assertEquals(testPath, path.getContent());
		NSString lastType = (NSString)fileRef.get("lastKnownFileType");
		assertEquals(HEADER_FILE, lastType.getContent());
		NSString encoding = (NSString)fileRef.get("fileEncoding");
		assertEquals("4", encoding.getContent());
		NSString sourceTree = (NSString)fileRef.get("sourceTree");
		assertEquals(DEFAULT_GROUP, sourceTree.getContent());
		
		NSDictionary group = getGroupByName(objects, "Frameworks");
		NSArray children = (NSArray) group.objectForKey("children");
		assertTrue(children.containsObject(new NSString(file.getFileRef())));
		
		NSDictionary phase = getPhase(objects, "PBXFrameworksBuildPhase");
		NSArray files = (NSArray) phase.get("files");
		assertTrue(files.containsObject(new NSString(file.getUuid())));
	}
	
	@Test
	public void testAddResource() throws Exception{
		PBXProject project = new PBXProject(pbxFile);
		String testPath = "my/files/abcd.h";
		PBXFile file = new PBXFile(testPath);
		project.addResourceFile(file);
		
		NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(project.getContent().getBytes());
		NSDictionary objects = (NSDictionary)dict.objectForKey("objects");
		
		NSDictionary group = getGroupByName(objects, "Resources");
		NSArray children = (NSArray) group.objectForKey("children");
		assertTrue(children.containsObject(new NSString(file.getFileRef())));
		
		NSDictionary phase = getPhase(objects, "PBXResourcesBuildPhase");
		NSArray files = (NSArray) phase.get("files");
		assertTrue(files.containsObject(new NSString(file.getUuid())));
	}
	
	@Test
	public void testAddToLibrarySearchPaths() throws Exception{
		PBXProject project = new PBXProject(pbxFile);
		String testPath = "my/files/abcd.h";
		PBXFile file = new PBXFile(testPath);
		project.addToLibrarySearchPaths(file);
		
		System.out.print(project.getContent());

		NSDictionary dict = (NSDictionary)ASCIIPropertyListParser.parse(project.getContent().getBytes());
		NSDictionary objects = (NSDictionary)dict.objectForKey("objects");
		HashMap<String, NSObject> hashmap =  objects.getHashMap();	
		Collection<NSObject> values = hashmap.values();
		for (NSObject nsObject : values) {
			NSDictionary obj = (NSDictionary) nsObject;
			NSString isa = (NSString) obj.objectForKey("isa");
			if(isa != null && isa.getContent().equals("XCBuildConfiguration")){
				NSDictionary buildSettings = (NSDictionary) obj.objectForKey("buildSettings");
				assertTrue(buildSettings.containsKey("LIBRARY_SEARCH_PATHS"));
			}
		}

	}
	
	private static NSDictionary getGroupByName(NSDictionary objects, String name) throws PBXProjectException{
		HashMap<String, NSObject> map = objects.getHashMap();
		Collection<NSObject> values = map.values();
		for (NSObject nsObject : values) {
			NSDictionary obj = (NSDictionary)nsObject;
			NSString isa = (NSString) obj.objectForKey("isa");
			NSString nameString = (NSString) obj.objectForKey("name");
			if(isa != null && isa.getContent().equals("PBXGroup") && nameString != null && name.equals(nameString.getContent())){
				return obj;
			}
		}
		return null;
	}
	
	private static NSDictionary getPhase(NSDictionary objects, String name) throws PBXProjectException{
		HashMap<String, NSObject> map = objects.getHashMap();
		Collection<NSObject> values = map.values();
		for (NSObject nsObject : values) {
			NSDictionary obj = (NSDictionary)nsObject;
			NSString isa = (NSString) obj.objectForKey("isa");
			if(isa != null && isa.getContent().equals(name)){
				return obj;
			}
		}
		return null;
	}	
	
	
}
