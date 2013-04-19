/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

/**
 * @author Yahor Radtsevich (yradtsevich)
 */
public class TransparentReader {
	private Reader reader;
	private PrintStream output;
	private int prevCharInt = -1;

	public TransparentReader(Reader reader, PrintStream output) {
		this.reader = reader;
		this.output = output;
	}
	
	public String readLine(boolean forwardToConcole) throws IOException {
		StringBuilder nextLine = new StringBuilder();
		int nextCharInt = -1; 
		boolean eolReached = false;
		while (!eolReached && (nextCharInt = reader.read()) >= 0) {
			char nextChar = (char) nextCharInt;
			if (forwardToConcole) {
				output.print(nextChar);
			}				
			
			if (nextChar == '\r' || nextChar == '\n') { // EOL
				if ((nextChar == '\r' && prevCharInt != '\n') || (nextChar == '\n' && prevCharInt != '\r')) {//not second part of CR/LF
					eolReached = true;
				}
			} else {
				nextLine.append(nextChar);
			}
			
			prevCharInt = nextCharInt;
		}
		
		if (nextLine.length() == 0 && nextCharInt < 0) {// nothing read AND end reached
			return null;
		} else {
			return nextLine.toString();
		}
	}
	
}
