package org.jboss.tools.vpe.cordovasim.eclipse;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Main {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws URISyntaxException {
		// TODO Auto-generated method stub
		try {
			System.out.println(new File(new URI("http://google.com")));
		} catch (URISyntaxException e) {
			
		}
	}

}
