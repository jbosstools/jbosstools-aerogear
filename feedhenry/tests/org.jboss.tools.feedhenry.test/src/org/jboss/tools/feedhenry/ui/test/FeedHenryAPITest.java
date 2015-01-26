package org.jboss.tools.feedhenry.ui.test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jboss.tools.feedhenry.ui.model.FeedHenry;
import org.jboss.tools.feedhenry.ui.model.FeedHenryException;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;
import org.junit.Test;


public class FeedHenryAPITest {
	
	@Test
	public void testListProjects() throws MalformedURLException, FeedHenryException{
		FeedHenry fh = new FeedHenry();
		
		List<FeedHenryProject> projects = fh.setFeedHenryURL(new URL("https://aerogear-t.sandbox.feedhenry.com/"))
				.setAPIKey("5a9ac45b4eb2d6ad006867ad78212d35479a8e46").listProjects();
		assertNotNull(projects);
	}
	
	@Test
	public void testListApplications() throws MalformedURLException, FeedHenryException{
		FeedHenry fh = new FeedHenry();
		List<FeedHenryProject> projects = fh.setFeedHenryURL(new URL("https://aerogear-t.sandbox.feedhenry.com/"))
				.setAPIKey("5a9ac45b4eb2d6ad006867ad78212d35479a8e46").listProjects();
		assertNotNull(projects);

	}

}
