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
package org.jboss.tools.feedhenry.ui.internal.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.op.AddToIndexOperation;
import org.eclipse.egit.core.op.CommitOperation;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.UserConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.osgi.util.NLS;
import org.eclipse.team.core.RepositoryProvider;
import org.jboss.tools.feedhenry.ui.FHPlugin;
/**
 * Utility for Git operations using EGit
 *
 */
public final class GitUtil {
	private static final String EGIT_TEAM_PROVIDER_ID = "org.eclipse.egit.core.GitProvider";
	
	/**
	 * Returns true if the git provider is enabled given project is
	 * @param project
	 * @return true if git provider is enabled
	 */
	public static boolean isGitEnabled( IProject project) {
		RepositoryProvider provider = RepositoryProvider.getProvider(project);
		return provider != null
				&& EGIT_TEAM_PROVIDER_ID.equals(provider.getID());
	}
	
	/**
	 *  Adds the remote config to given repository.
	 *  
	 * @param remoteName
	 * @param uri
	 * @param repository
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void addRemoteConfig(String remoteName, URIish uri,
			Repository repository) throws URISyntaxException, MalformedURLException, IOException {
		StoredConfig config = repository.getConfig();
		RemoteConfig remoteConfig = new RemoteConfig(config, remoteName);
		remoteConfig.addURI(uri);
		remoteConfig.update(config);
		config.save();
	}
	/**
	 * Returns the git repository for the project
	 * @param project
	 * @return null or repository
	 */
	public static Repository getRepository(IProject project) {
		if(project == null ) return null;
		RepositoryMapping repositoryMapping = RepositoryMapping.getMapping(project);
		if (repositoryMapping == null) {
			return null;
		}
		return repositoryMapping.getRepository();
	}
	
	public static Repository share(IProject project, IProgressMonitor monitor)
			throws CoreException {
		Repository repository = createRepository(project, monitor);
		connect(project, repository.getDirectory(), monitor);
		addToRepository(project, repository, monitor);
		commit(project,"Initial commit from JBoss Tools",repository, monitor);
		return repository;
	}

	public static Repository createRepository(IProject project,
			IProgressMonitor monitor) throws CoreException {
		try {
			InitCommand init = Git.init();
			init.setBare(false).setDirectory(project.getLocation().toFile());
			Git git = init.call();
			return git.getRepository();
		} catch (JGitInternalException | GitAPIException e) {
			throw new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID,
					NLS.bind("Could not initialize a git repository at {0}: {1}",
							getRepositoryPathFor(project), e.getMessage()), e));
		}
	}
	
	public static File getRepositoryPathFor(IProject project) {
		return new File(project.getLocationURI().getPath(), ".git");
	}
	
	private static void connect(IProject project, File repositoryFolder,
			IProgressMonitor monitor) throws CoreException {
		new ConnectProviderOperation(project, repositoryFolder)
				.execute(monitor);
	}

	public static void addToRepository(IProject project, Repository repository,
			IProgressMonitor monitor) throws CoreException {
		AddToIndexOperation add = new AddToIndexOperation(Collections.singletonList(project));
		add.execute(monitor);
	}
	

	private static RevCommit commit(IProject project, String commitMessage,
			Repository repository, IProgressMonitor monitor)
			throws CoreException {
		UserConfig userConfig = getUserConfig(repository);
		CommitOperation op = new CommitOperation(null, null, null,
				getFormattedUser(userConfig.getAuthorName(),
						userConfig.getAuthorEmail()), getFormattedUser(
						userConfig.getCommitterName(),
						userConfig.getCommitterEmail()), commitMessage);
		op.setCommitAll(true);
		op.setRepository(repository);
		op.execute(monitor);
		return op.getCommit();
	}
	
	private static UserConfig getUserConfig(Repository repository)
			throws CoreException {
		Assert.isNotNull(repository, "Could not get user configuration. No repository provided.");

		if (repository.getConfig() == null) {
			throw new CoreException(new Status(IStatus.ERROR, FHPlugin.PLUGIN_ID, 
					NLS.bind("no user configuration (author, committer) are present in repository \"{0}\"",
					repository.toString())));
		}
		return repository.getConfig().get(UserConfig.KEY);
	}
	
	private static String getFormattedUser(String name, String email) {
		return new StringBuilder(name).append(" <").append(email).append('>').toString();
	}
}
