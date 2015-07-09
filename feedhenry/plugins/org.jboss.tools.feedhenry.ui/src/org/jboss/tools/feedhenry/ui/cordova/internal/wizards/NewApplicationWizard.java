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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.thym.core.HybridProject;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.feedhenry.ui.FHPlugin;
import org.jboss.tools.feedhenry.ui.cordova.internal.preferences.FHPreferences;
import org.jboss.tools.feedhenry.ui.internal.FHErrorHandler;
import org.jboss.tools.feedhenry.ui.internal.IntegrateFeedhenrySDKOperation;
import org.jboss.tools.feedhenry.ui.internal.util.GitUtil;
import org.jboss.tools.feedhenry.ui.model.FeedHenry;
import org.jboss.tools.feedhenry.ui.model.FeedHenryApplication;
import org.jboss.tools.feedhenry.ui.model.FeedHenryException;
import org.jboss.tools.feedhenry.ui.model.FeedHenryProject;

public class NewApplicationWizard extends Wizard implements INewWizard {

	private final class CreateRemoteApplicationTask implements IRunnableWithProgress {
		private final FeedHenryProject fHProject;
		private final HybridProject project;
		private final String remoteName;
		private final String applicationName;

		private CreateRemoteApplicationTask(FeedHenryProject fHProject, HybridProject project, String remoteName,
				String applicationName) {
			this.fHProject = fHProject;
			this.project = project;
			this.remoteName = remoteName;
			this.applicationName = applicationName;
		}

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
						NLS.bind("Create FeedHenry Application {0} on {1}", new String[]{applicationName, feedHenryURL}), 100);
				FeedHenryApplication fha = fh.setFeedHenryURL(new URL(feedHenryURL))
						.setAPIKey(prefs.getUserAPIKey())
						.importBareRepo(fHProject.getGuid(), applicationName, FeedHenryApplication.APP_TYPE_CORDOVA_ADVANCED,
								sm.newChild(50));
				Repository repository = GitUtil.getRepository(project.getProject());
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
	}

	private static final String DIALOG_SETTINGS_KEY = "FeedHenryNewApplicationWizard";
	public static final String WIZARD_ID = "org.jboss.tools.feedhenry.ui.newApplication";
	private FHProjectSelectionPage page;
	private IStructuredSelection initialSelection;

	public NewApplicationWizard() {
		super();
		setWindowTitle("Create FeedHenry Application");
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(FHPlugin.getImageDescriptor(FHPlugin.PLUGIN_ID,"/icons/wizban/fh_wizban.png"));
		IDialogSettings wbSettings = FHPlugin.getDefault().getDialogSettings();
		IDialogSettings section= wbSettings.getSection(DIALOG_SETTINGS_KEY);
		setDialogSettings(section);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.initialSelection = selection;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		page = new FHProjectSelectionPage(this.initialSelection);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		final HybridProject project = page.getHybridProject();
		final FeedHenryProject FHProject = page.getFeedHenryProject();
		final String applicationName = page.getApplicationName();
		final String remoteName = page.getRemoteName();
		Assert.isNotNull(project);
		Assert.isNotNull(FHProject);
		Assert.isNotNull(remoteName);
		Assert.isNotNull(applicationName);
		try{
			getContainer().run(false, true, new IntegrateFeedhenrySDKOperation(project.getProject()));
			getContainer().run(false, true, new CreateRemoteApplicationTask(FHProject, project, remoteName, applicationName));
		}
		catch(Exception e){
			FHErrorHandler.handle(e);
			return false;
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				String[] messages = {applicationName,FHProject.getTitle(),remoteName};
				Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageDialog.openInformation(parent, "Almost done!",
						NLS.bind("Application \"{0}\" for project \"{1}\" is created on the platform. Git repository is also configured to work with the platform. "
								+ "Please review and push the changes to the configured remote \"{2}\" to complete the integration.",messages));
			}
		});
		return true;
	}

}
