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
package org.jboss.tools.vpe.cordovasim.plugin.model;

import java.util.List;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public class Plugin {
	private String file;
	private String id;
	private List<String> clobbers;
	private List<String> merges;

	public Plugin(String file, String id, List<String> clobbers, List<String> merges) {
		this.file = file;
		this.id = id;
		this.clobbers = clobbers;
		this.merges = merges;
	}

	public String getFile() {
		return file;
	}

	public String getId() {
		return id;
	}

	public List<String> getClobbers() {
		return clobbers;
	}

	public List<String> getMerges() {
		return merges;
	}

}
