/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * 	Contributors:
 * 		 Red Hat Inc. - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.jboss.tools.feedhenry.ui.model;

public class FeedHenryException extends Exception {

	private int code;

	private static final long serialVersionUID = -8158528416707671352L;

	public FeedHenryException(String message) {
		super(message);
	}
	
	public FeedHenryException(int errorCode, String message){
		super(message);
		this.code = errorCode;
	}

	public int getCode() {
		return code;
	}
}
