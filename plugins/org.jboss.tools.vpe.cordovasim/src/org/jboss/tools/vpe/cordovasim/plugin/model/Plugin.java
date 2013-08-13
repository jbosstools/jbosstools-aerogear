package org.jboss.tools.vpe.cordovasim.plugin.model;

import java.util.List;

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
