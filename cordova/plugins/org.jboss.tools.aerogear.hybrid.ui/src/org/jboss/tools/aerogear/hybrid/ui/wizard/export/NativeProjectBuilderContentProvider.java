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
package org.jboss.tools.aerogear.hybrid.ui.wizard.export;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jboss.tools.aerogear.hybrid.core.HybridCore;
import org.jboss.tools.aerogear.hybrid.core.extensions.NativeProjectBuilder;

public class NativeProjectBuilderContentProvider implements
		IStructuredContentProvider {
	private List<NativeProjectBuilder> builders;
	
	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		try{
			builders = (List<NativeProjectBuilder>) newInput;
		}catch(ClassCastException e){
			Assert.isTrue(false, "new input is correct type this conent provider works only with List<NativeProjectBuilder>");
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(builders == null ){
			builders = HybridCore.getNativeProjectBuilders();
		}
		
		
		return builders.toArray();
	}

}
