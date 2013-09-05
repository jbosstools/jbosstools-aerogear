package org.jboss.tools.aerogear.hybrid.core.plugin.registry;

import java.io.File;
import java.io.IOException;

import org.eclipse.ecf.filetransfer.IFileTransferListener;
import org.eclipse.ecf.filetransfer.events.IFileTransferEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDataEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveDoneEvent;
import org.eclipse.ecf.filetransfer.events.IIncomingFileTransferReceiveStartEvent;
import org.jboss.tools.aerogear.hybrid.core.util.FileUtils;
import org.jboss.tools.aerogear.hybrid.core.util.TarException;

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
public class PluginReceiver implements IFileTransferListener{
	private final File cacheDirectory;
	
	public PluginReceiver(File directory) {
		this.cacheDirectory = directory;
	}

	@Override
	public void handleTransferEvent(IFileTransferEvent event) {
		File tarFile = new File(cacheDirectory,"package.tgz");

		 if (event instanceof IIncomingFileTransferReceiveStartEvent) {
			 IIncomingFileTransferReceiveStartEvent startEvent = (IIncomingFileTransferReceiveStartEvent) event;
			 try {
				 if(!cacheDirectory.exists())
					 cacheDirectory.mkdirs();
				 
				startEvent.receive(tarFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }else if(event instanceof IIncomingFileTransferReceiveDataEvent){
			 IIncomingFileTransferReceiveDataEvent dataEvent = (IIncomingFileTransferReceiveDataEvent) event;
			 System.out.println("percent complete:" + dataEvent.getSource().getPercentComplete());
		 }else if(event instanceof IIncomingFileTransferReceiveDoneEvent ){
			 IIncomingFileTransferReceiveDoneEvent doneEvent = (IIncomingFileTransferReceiveDoneEvent) event;
			 Exception ex = doneEvent.getException();
			 if(ex == null){
				 try {
					FileUtils.untarFile(tarFile, cacheDirectory);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TarException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 
			 }
		 }
	
		
	}

}
