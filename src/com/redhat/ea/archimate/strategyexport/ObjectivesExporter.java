/**
 * 
 *	Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 *	This file is part of strategy exporter.
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU Lesser General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Lesser General Public License for more details.
 *
 *	You should have received a copy of the GNU Lesser General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.ea.archimate.strategyexport;

import java.io.File;

import java.io.IOException;


import org.eclipse.core.runtime.jobs.IJobChangeEvent;

import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;


import com.redhat.ea.archimate.strategyexport.dialog.ExportDialog;

import com.archimatetool.editor.model.IModelExporter;

import com.archimatetool.model.IArchimateModel;


public class ObjectivesExporter implements IModelExporter {

	String MY_EXTENSION = ".html"; //$NON-NLS-1$
	String MY_EXTENSION_WILDCARD = "*.html"; //$NON-NLS-1$


	public ObjectivesExporter() {
	}

	@Override
	public void export(IArchimateModel model) throws IOException {
		ExportDialog ed = new ExportDialog(Display.getCurrent().getActiveShell(), "Export the selected Roadmap to html format to display objectives and outcomes.");
		ed.open();
		if(ed.isCancelled()){
			return;
		}
		
		String[] statuses = ed.getStatuses();
		
		
		File file = askSaveFile();
		if (file == null) {
			return;
		}

		com.redhat.ea.archimate.strategyexport.objectives.ObjectivesExporter exportRunner = 
				new com.redhat.ea.archimate.strategyexport.objectives.ObjectivesExporter(model, statuses, file);

		
		try {
			exportRunner.setUser(true);
			exportRunner.addJobChangeListener(new JobChangeAdapter(){
				@Override
				public void done(IJobChangeEvent event) {
					// TODO Auto-generated method stub
					super.done(event);
					Display.getCurrent().asyncExec(new Runnable(){
						public void run(){
							MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "DOT Cluster Exporter", "Completed Export!");
						}
					});
				}
			});
			
			exportRunner.schedule();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	/**
	 * Ask user for file name to save to
	 */
	private File askSaveFile() {
		FileDialog dialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.SAVE);
		dialog.setText(Messages.MyExporter_0);
		dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD, "*.*" }); //$NON-NLS-1$
		String path = dialog.open();
		if (path == null) {
			return null;
		}

		// Only Windows adds the extension by default
		if (dialog.getFilterIndex() == 0 && !path.endsWith(MY_EXTENSION)) {
			path += MY_EXTENSION;
		}

		File file = new File(path);

		// Make sure the file does not already exist
		if (file.exists()) {
			boolean result = MessageDialog.openQuestion(Display.getCurrent()
					.getActiveShell(), Messages.MyExporter_0, NLS.bind(
					Messages.MyExporter_1, file));
			if (!result) {
				return null;
			}
		}

		return file;
	}
}
