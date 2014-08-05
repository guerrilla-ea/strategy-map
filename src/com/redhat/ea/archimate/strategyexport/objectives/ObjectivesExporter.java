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
package com.redhat.ea.archimate.strategyexport.objectives;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IGoal;

import com.redhat.ea.archimate.strategyexport.Exporter;
import com.redhat.ea.archimate.strategyexport.Messages;
import com.redhat.ea.archimate.strategyexport.util.ExportUtils;

public class ObjectivesExporter extends Exporter {
	private Queue<Objective> objectives;

	protected SubMonitor monitor;

	public ObjectivesExporter(IArchimateModel model, String[] status, File file) {
		super(model, file, status);
		objectives = new LinkedList<Objective>();
	}

	public IStatus runInUIThread(IProgressMonitor ipm) {
		try {
			monitor = SubMonitor.convert(ipm);
			if (this.model.getFolders() != null
					&& this.model.getFolders().size() > 0) {
				for (IFolder f : this.model.getFolders()) {
					processPackage(f, true, monitor);
				}
			}
			writeFile();
		} catch (StackOverflowError sofe) {
			sofe.printStackTrace();
		}
		return new Status(Status.OK, Messages.Plugin_ID,
				"DotClusterExporter Completed");

	}

	public void processPackage(IFolder currentFolder, boolean isTop,
			IProgressMonitor m) {
		SubMonitor progress = SubMonitor.convert(m, currentFolder.getElements()
				.size());
		reportStatus("Processing Package " + currentFolder.getName());
		if (!isTop) {
			Objective oInfo = new Objective(currentFolder.getName(),
					currentFolder.getDocumentation());
			List<Outcome> milestones = new ArrayList<Outcome>();

			SubMonitor loopProgress = progress.newChild(90).setWorkRemaining(
					currentFolder.getElements().size());
			for (EObject s : currentFolder.getElements()) {
				if (s instanceof IArchimateElement) {
					IArchimateElement x = (IArchimateElement) s;

					if (x instanceof IGoal
							&& this.inStatus.contains(ExportUtils.getStatus(x))) {
						milestones.add(new Outcome(x.getName(), x
								.getDocumentation(), ExportUtils.getPhase(x),
								ExportUtils.getStatus(x)));
					}
				}
				loopProgress.internalWorked(1);
			}

			oInfo.setOutcomes(milestones);
			objectives.add(oInfo);

		}

		progress.setWorkRemaining(1000);
		for (IFolder nP : currentFolder.getFolders()) {
			processPackage(nP, false, progress.newChild(1));
		}
	}

	@Override
	protected void writeHeader() throws IOException {
		_file.write("<html>\n");
		_file.write("<head>\n");
		_file.write("<title>Objectives</title>\n");
		_file.write("<style type=\"text/css\">\n");
		_file.write("th{ background-color:888A85; color:black;}\n");
		_file.write(".header{ background-color:4E9FDD; color:white;}\n");
		_file.write(".item{ background-color:white; color:black;}\n");
		_file.write("</style>\n");
		_file.write("</head>\n");
		_file.write("<body>\n");
		_file.write("<table width=\"75%\" border=\"1\">\n");
		_file.write("<thead class=\"header\"><th width=\"30%\">Objective</th><th>Description</th><th>Milestone Outcomes</th></thead>\n");
		_file.write("<tbody>\n");
	}

	@Override
	protected void writeFormat() throws IOException {
		for (Objective o : objectives) {
			_file.write(o.toString());
		}
	}

	@Override
	protected void writeFooter() throws IOException {
		_file.write("</tbody>\n");
		_file.write("</table>\n");
		_file.write("</body></html>\n");
	}

}
