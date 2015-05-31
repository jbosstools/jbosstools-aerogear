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
package org.jboss.tools.feedhenry.ui.cordova.internal.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferences;
import org.jboss.tools.feedhenry.ui.internal.FHErrorHandler;
import org.jboss.tools.feedhenry.ui.internal.util.GitUtil;
import org.jboss.tools.feedhenry.ui.model.FeedHenry;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;
import org.jboss.tools.feedhenry.ui.model.FeedHenryException;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;

public class NewApplicationWizard extends Wizard implements INewWizard {

	private static final String DIALOG_STTINGS_KEY = "FeedHenryNewApplicationWizard";
	private FHProjectSelectionPage page;

	public NewApplicationWizard() {
		super();
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID,"/icons/wizban/fh_wizban.png"));
		IDialogSettings wbSettings = FHPlugin.getDefault().getDialogSettings();
		IDialogSettings section= wbSettings.getSection(DIALOG_STTINGS_KEY);
		setDialogSettings(section);

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addPages() {
		super.addPages();
		page = new FHProjectSelectionPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		final HybridProject project = page.getHybridProject();
		final FeedHenryProject FHProject = page.getFeedHenryProject();
		final String remoteName = page.getRemoteName();
		Assert.isNotNull(project);
		Assert.isNotNull(FHProject);
		Assert.isNotNull(remoteName);
		try{
			getContainer().run(false, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					FeedHenry fh = new FeedHenry();
					FHPreferences prefs = FHPreferences.getPreferences();
					String feedHenryURL = prefs.getFeedHenryURL();
					if(feedHenryURL == null || feedHenryURL.isEmpty()){
						CoreException ce = new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, 
								FHErrorHandler.ERROR_INVALID_PREFERENCES, 
								"FeedHenry URL preference is empty. Specify FeedHenry URL on preferences.",null));
						throw new InvocationTargetException(ce);
					}
					try {
						SubMonitor sm = SubMonitor.convert(monitor, 
								NLS.bind("Create FeedHenry Application {0} on {1}", new String[]{project.getAppName(), feedHenryURL}), 100);
						Repository repository = GitUtil.getRepository(project.getProject());
						FeedHenryApplication fha = fh.setFeedHenryURL(new URL(feedHenryURL))
								.setAPIKey(prefs.getUserAPIKey())
								.importBareRepo(FHProject.getGuid(), project.getAppName(), FeedHenryApplication.APP_TYPE_CORDOVA_ADVANCED,
										sm.newChild(50));
						if(repository == null ){ 
							sm.setTaskName("Create Git repository");
							repository = GitUtil.share(project.getProject(), sm.newChild(50));
						}
						GitUtil.addRemoteConfig(remoteName, new URIish(fha.getRepoUrl()), repository);
						sm.done();
					} catch (FeedHenryException | CoreException | URISyntaxException | IOException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		}
		catch(Exception e){
			FHErrorHandler.handle(e);
			return false;
		}
		return true;
	}

}
