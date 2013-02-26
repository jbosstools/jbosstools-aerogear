/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.actions;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserView;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.editors.CordovaSimEditorLauncher;
import org.jboss.tools.vpe.cordovasim.eclipse.util.CordovaSimLauncher;

/**
 * @author "Yahor Radtsevich (yradtsevich)"
 */
@SuppressWarnings("restriction")
public class RunCordovaSimAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		String url = guessUrl();
		CordovaSimLauncher.launchCordovaSim(url);
	}

	private String guessUrl() {
		String url = null;
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {			
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IWorkbenchPart part = page.getActivePart();
				url = getInternalWebBrowserUrl(part);
				if (url == null) {
					IFile activeEditorFile = getEditorFile(part);
					if (isSupportedFile(activeEditorFile)) {
						url = toUrl(activeEditorFile);
					}
				}
			}
			
			if (url == null) {
				ISelection selection = window.getSelectionService().getSelection();
				IFile selectedFile = getSelectedFile(selection);
				if (isSupportedFile(selectedFile)) {
					url = toUrl(selectedFile);
				}
			}
		}

		return url;
	}

	private boolean isSupportedFile(IFile file) {
		if (file != null) {
			IEditorDescriptor[] editors = PlatformUI.getWorkbench()
					.getEditorRegistry().getEditors(file.getName());

			for (IEditorDescriptor editor : editors) {
				if (CordovaSimEditorLauncher.EDITOR_ID.equals(editor.getId())) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * If {@code part} is Internal Web Browser, returns opened URL. Otherwise
	 * returns {@code null}. 
	 */
	private String getInternalWebBrowserUrl(IWorkbenchPart part) {
		String url = null;
		
		BrowserViewer browserViewer = null;
		if (part instanceof IViewPart) {
			IViewSite viewSite = ((IViewPart) part).getViewSite();
			if (WebBrowserView.WEB_BROWSER_VIEW_ID.equals(viewSite.getId()) && part instanceof WebBrowserView) {
				browserViewer = getFieldValue(part, "viewer"); //$NON-NLS-1$
			}
		} else if (part instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) part;
			IEditorSite editorSite = editorPart.getEditorSite();
			if (WebBrowserEditor.WEB_BROWSER_EDITOR_ID.equals(editorSite.getId()) && part instanceof WebBrowserEditor) {
				browserViewer = getFieldValue(part, "webBrowser"); //$NON-NLS-1$
			}
		}
		
		if (browserViewer != null) {
			url = browserViewer.getURL();
		}
		
		return url;
	}
	
	/**
	 * If {@code part} is Internal Web Browser, returns opened URL. Otherwise
	 * returns {@code null}. 
	 */
	private IFile getEditorFile(IWorkbenchPart part) {
		IFile file = null;
		
		if (part instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) part;

			if (editorPart.getEditorInput() instanceof IFileEditorInput) {
				IFileEditorInput fileEditorInput = (IFileEditorInput) editorPart.getEditorInput();
				file = fileEditorInput.getFile();
			}
		}
		
		return file;
	}

	@SuppressWarnings("unchecked")
	private <T> T getFieldValue(Object object, String name) {
		T fieldValue = null;
		try {
			Field field = object.getClass().getDeclaredField(name);
			field.setAccessible(true);
			fieldValue = (T) field.get(object);
		} catch (SecurityException e) {
			Activator.logError(e.getMessage(), e);
		} catch (NoSuchFieldException e) {
			Activator.logError(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Activator.logError(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			Activator.logError(e.getMessage(), e);
		}
		
		return fieldValue;
	}

	/**
	 * Returns selected file, if a file is contained in the {@code selection}.
	 * Otherwise returns {@code null}.
	 */
	private IFile getSelectedFile(ISelection selection) {
		IFile file = null;
		if (selection instanceof IStructuredSelection) {
		    IStructuredSelection ssel = (IStructuredSelection) selection;
		    Object firstSelectedElement = ssel.getFirstElement();
		    if (firstSelectedElement != null) {
			    file = (IFile) Platform.getAdapterManager().getAdapter(firstSelectedElement,
			            IFile.class);
		    }
		    
		    if (file == null) {
		        if (firstSelectedElement instanceof IAdaptable) {
		            file = (IFile) ((IAdaptable) firstSelectedElement).getAdapter(IFile.class);
		        }
		    }
		}
		return file;
	}

	private String toUrl(IFile file) {
		String url = null;
		
		if (file != null) {
		    IPath location = file.getLocation();
		    if (location != null) {
		    	url = location.toFile().toURI().toASCIIString();
		    }
		}
		return url;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void init(IWorkbenchWindow window) {
	}
}
