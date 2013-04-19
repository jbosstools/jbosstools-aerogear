/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.events;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;

public class RippleInjector extends LocationAdapter {
	@Override
	public void changed(LocationEvent event) {
		Browser browser = (Browser) event.widget;
		browser.execute("if (window.opener.ripple) { window.opener.ripple('bootstrap').inject(window, document);}");
		browser.forceFocus();
	}
}
