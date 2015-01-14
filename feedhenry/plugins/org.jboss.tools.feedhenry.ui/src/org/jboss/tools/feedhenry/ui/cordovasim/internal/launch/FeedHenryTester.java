/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.feedhenry.ui.cordovasim.internal.launch;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.feedhenry.ui.cordovasim.util.FeedHenryUtil;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class FeedHenryTester extends PropertyTester {
	private static final String IS_FEED_HENRY = "isFeedHenry"; //$NON-NLS-1$

	public FeedHenryTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] arg2, Object expectedValue) {
		if (IS_FEED_HENRY.equals(property) && receiver instanceof IResource) {
			IProject project = ((IResource) receiver).getProject();
			return FeedHenryUtil.isFeedHenry(project);
		}
		return false;
	}
}