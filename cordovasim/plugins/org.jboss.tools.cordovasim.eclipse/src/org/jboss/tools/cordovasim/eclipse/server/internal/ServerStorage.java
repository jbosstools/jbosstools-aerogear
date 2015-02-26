/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cordovasim.eclipse.server.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class ServerStorage {
	private ServerStorage() {
	}

	private static final Map<Integer, Server> INSTANCE = Collections.synchronizedMap(new HashMap<Integer, Server>());

	public static Map<Integer, Server> getStorage() {
		return INSTANCE;
	}

}