package org.jboss.tools.vpe.cordovasim;

import java.io.File;

import org.eclipse.swt.browser.Browser;
import org.jboss.tools.vpe.browsersim.ui.menu.FileMenuCreator;

public class CordovaSimFileMenuCreator extends FileMenuCreator {
	@Override
	protected void viewServerSource(Browser browser) {
		String url = browser.getUrl();
		String port = Integer.toString(CordovaSimArgs.getPort());
		int index = url.indexOf(port) + port.length();
		url = url.substring(index);
		File sourceFile = new File(CordovaSimArgs.getRootFolder() + url.substring(0, minAnchorIndex(url)));
		if (sourceFile.exists()) {
			System.out.println(OPEN_FILE_COMMAND + sourceFile.getAbsolutePath()); // send command to Eclipse
		} else {
			super.viewServerSource(browser);
		}
	}
	
	private int minAnchorIndex(String url) {
		int questionMarkIndex = url.indexOf('?');
		questionMarkIndex = questionMarkIndex != -1 ? questionMarkIndex : url.length();
		
		int hashIndex =  url.indexOf('#');
		hashIndex = hashIndex != -1 ? hashIndex : url.length();
		return Math.min(questionMarkIndex, hashIndex);
	}
}
