package org.jboss.tools.feedhenry.ui.test;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.feedhenry.ui.model.FeedHenry;
import org.jboss.tools.feedhenry.ui.model.FeedHenryException;
import org.junit.Test;


public class FeedHenryAPITest {
	
	@Test(expected=FeedHenryException.class)
	public void testFailAPIKey() throws MalformedURLException, FeedHenryException{
		FeedHenry fh = new FeedHenry();
		fh.setFeedHenryURL(new URL("https://aerogear-t.sandbox.feedhenry.com/")).listProjects(new NullProgressMonitor());
	}	
	

}
