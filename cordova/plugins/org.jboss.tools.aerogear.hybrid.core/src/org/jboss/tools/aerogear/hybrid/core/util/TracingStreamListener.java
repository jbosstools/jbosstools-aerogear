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
package org.jboss.tools.aerogear.hybrid.core.util;

import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
/**
 * Helper listener which generates traces of the streams 
 * 
 * @author Gorkem Ercan
 *
 */
public class TracingStreamListener implements IStreamListener {
	
	private IStreamListener delegate;

	public TracingStreamListener(){
		this(null);
	}
	
	public TracingStreamListener(IStreamListener delegate){
		this.delegate = delegate;
	}

	@Override
	public void streamAppended(String text, IStreamMonitor monitor) {
		HybridCore.trace(text);
		if(delegate != null){
			delegate.streamAppended(text, monitor);
		}
	}

}
