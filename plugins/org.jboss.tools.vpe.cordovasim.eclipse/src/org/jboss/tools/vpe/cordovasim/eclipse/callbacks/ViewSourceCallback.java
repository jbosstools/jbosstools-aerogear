/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.cordovasim.eclipse.callbacks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
//import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.cordovasim.eclipse.Activator;
import org.jboss.tools.vpe.cordovasim.eclipse.util.CordovaSimLauncher;
import org.jboss.tools.vpe.cordovasim.eclipse.util.TransparentReader;

/**
 * Handler for the BrowserSim commands printed to the console in the following form:
 * <pre>
 * org.jboss.tools.vpe.browsersim.ui.BrowserSim.command.viewSource:http://example.com/index.html
 * Base64EncodedPageSource==</code>
 * @author Yahor Radtsevich (yradtsevich)
 */
public class ViewSourceCallback implements CordovaSimCallback {
	private static final String VIEW_SOURCE_COMMAND = CordovaSimLauncher.CORDOVASIM_CLASS_NAME + ".command.viewSource:"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.cordovasim.eclipse.callbacks.BrowserSimCallback#getCallbackId()
	 */
	@Override
	public String getCallbackId() {
		return VIEW_SOURCE_COMMAND;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.cordovasim.eclipse.callbacks.BrowserSimCallback#callback(java.io.InputStreamReader)
	 */
	@Override
	public void call(final String lastString, TransparentReader reader) throws IOException {
		final String address = lastString.substring(VIEW_SOURCE_COMMAND.length());
		String encodedSource = reader.readLine(false);
		final String source = new String(DatatypeConverter.parseBase64Binary(encodedSource));
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				openInMemoryHtmlEditor(source, address, address);
			}
		});
	}
	
	private void openInMemoryHtmlEditor(String content, String name, String toolTip) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window != null ? window.getActivePage() : null;
		if (page != null) {
			try {
				IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench()
						.getEditorRegistry().getDefaultEditor("view-source.html"); // get default editor for .html //$NON-NLS-1$
				String editorId;
				if (editorDescriptor != null && editorDescriptor.isInternal()) {
					editorId = editorDescriptor.getId();
				} else {
					editorId = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
				}

				IStorage storage = new StringStorage("", // see the long comment below to know why an empty storage is created //$NON-NLS-1$
						"view-source.html"); // .html extension is needed to enable code highlighting in the WTP HTML editor //$NON-NLS-1$
				IStorageEditorInput input = new StringInput(storage, name, toolTip);
				IEditorPart editor = page.openEditor(input, editorId);
				
				/* We change content of the editor AFTER the editor is created
				 * as a workaround for the following WTP bug.
				 * The essence of the bug is that if given HTML page contains a link
				 * to an external DTD, then WTP HTML editor tries to access this DTD before the editor
				 * is created and freezes UI.
				 * See http://www.eclipse.org/forums/index.php/m/639937/
				 */
				IDocument doc = null;
				
				// this checking is needed to do not load jst.jsp plug-ins if it is unnecessary
				if ("org.jboss.tools.jst.jsp.jspeditor.HTMLTextEditor".equals(editorId)) { //$NON-NLS-1$
					try {
//						if (editor instanceof JSPMultiPageEditor) {
//							JSPMultiPageEditor multiPageEditor = (JSPMultiPageEditor) editor;
//							doc = multiPageEditor.getSourceEditor().getTextViewer().getDocument();
//							doc.set(content);
//							editor.doSave(null); // reset resource-changed marker
//						}
					} catch (NoClassDefFoundError e1) {
						// this is OK - there are some optional dependencies
						ITextEditor textEditor = null;
						if (editor instanceof ITextEditor) {
							textEditor = (ITextEditor) editor;
						} else {
							textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
						}
						
						if (textEditor != null) {
							doc = textEditor.getDocumentProvider().getDocument(input);
						}
					}
				}
			} catch (PartInitException e) {
				Activator.logError(e.getMessage(), e);
			}
		} else {
			Activator.logError("Cannot obtain workbench page", null);
		}
	}
}
	
class StringInput implements IStorageEditorInput {
	private IStorage storage;
	private String name;
	private String toolTip;

	StringInput(IStorage storage, String name, String toolTip) {
		this.storage = storage;
		this.name = name;
		this.toolTip = toolTip;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return name;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public IStorage getStorage() {
		return storage;
	}

	public String getToolTipText() {
		return toolTip;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
}

class StringStorage implements IStorage {
	private String string;
	private String name;

	StringStorage(String string, String name) {
		this.string = string;
		this.name = name;
	}

	public InputStream getContents() throws CoreException {
		return new ByteArrayInputStream(string.getBytes());
	}

	public IPath getFullPath() {
		return null;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	public String getName() {
		return name;
	}

	public boolean isReadOnly() {
		return true;
	}
}
