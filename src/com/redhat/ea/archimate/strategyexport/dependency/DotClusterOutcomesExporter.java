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
package com.redhat.ea.archimate.strategyexport.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IGoal;
import com.redhat.ea.archimate.strategyexport.Messages;
import com.redhat.ea.archimate.strategyexport.global.NodeFactory;
import com.redhat.ea.archimate.strategyexport.global.TNode;
import com.redhat.ea.archimate.strategyexport.global.types.Colors;
import com.redhat.ea.archimate.strategyexport.resourceplan.CriticalPath;
import com.redhat.ea.archimate.strategyexport.resourceplan.ElementWrapper;
import com.redhat.ea.archimate.strategyexport.util.ExportUtils;

/*
 * outcomes-only, showing derived dependencies
 * 
 * WIP, known-broken
 * 
 */

/// <summary>
/// Dot cluster exporter.
/// Handles the export into a .dot file.
/// </summary>
public class DotClusterOutcomesExporter extends TDotExporter {

	private Queue<IArchimateElement> requirements;
	private List<CriticalPath> criticalPaths;

	private TreeMap<String, List<TNode>> allClusters;

	protected List<SimpleConnector> nodeRelations;

	protected List<TNode> processedItems;

	protected SubMonitor monitor;

	public DotClusterOutcomesExporter(IArchimateModel model, String[] status,
			File file) {
		super(model, file, status);
		requirements = new LinkedList<IArchimateElement>();
		allClusters = new TreeMap<String, List<TNode>>();

		nodeRelations = new ArrayList<SimpleConnector>();
		processedItems = new ArrayList<TNode>();
		criticalPaths = new ArrayList<CriticalPath>();
	}

	public IStatus runInUIThread(IProgressMonitor ipm) {

		try {
			monitor = SubMonitor.convert(ipm);
			if (this.model.getFolders().size() > 0) {
				for (IFolder f : this.model.getFolders()) {
					processPackage(f, monitor);
				}

				@SuppressWarnings("unused")
				SubMonitor loopMonitor = monitor.newChild(100);

				for (IArchimateElement ele : requirements) {
					// get the critical path for each requirement
					CriticalPath cp = new CriticalPath(ele, this.inStatus);
					cp.process();
					criticalPaths.add(cp);
				}

				// now filter on the outcomes/requirements only and create the
				// connections
				for (CriticalPath cp : criticalPaths) {

					String[] keys = (String[]) cp.getOutcomeElements().keySet()
							.toArray();

					for (int i = keys.length - 1; i > 0; i--) {

						addConnector(cp.getOutcomeElements().get(keys[i])
								.getSource(),
								cp.getOutcomeElements().get(keys[i - 1])
										.getSource());

					}

					// now cluster the outcomes
					for (ElementWrapper ew : cp.getOutcomeElements().values()) {
						IArchimateElement ele = ew.getSource();
						TNode item = NodeFactory.createNode(ele);
						String phase = ExportUtils.getPhase(ele);
						if (allClusters.containsKey(phase)) {
							if (!allClusters.get(phase).contains(item)) {
								allClusters.get(phase).add(item);
							}
						} else {
							allClusters.put(phase, new ArrayList<TNode>());
							allClusters.get(phase).add(item);
						}
					}
				}
			}
			writeFile();
		} catch (StackOverflowError err) {
			err.printStackTrace();
			return new Status(Status.ERROR, Messages.Plugin_ID,
					"DotClusterOutcomesExporter- Stack Overflow Error Occurred.");
		}
		return new Status(Status.OK, Messages.Plugin_ID,
				"DotClusterOutcomesExporter Completed");
	}

	private void processPackage(IFolder currentFolder, IProgressMonitor m) {

		SubMonitor progress = SubMonitor.convert(m, 50);

		reportStatus(String.format("Processing Package: %s",
				currentFolder.getName()));

		SubMonitor loopProgress = progress.newChild(90).setWorkRemaining(
				currentFolder.getElements().size());
		for (EObject x : currentFolder.getElements()) {
			if (x instanceof IArchimateElement) {
				IArchimateElement s = (IArchimateElement) x;
				if (s instanceof IGoal
						&& this.inStatus.contains(ExportUtils.getStatus(s))) {
					requirements.add(s);
				}
			}
			loopProgress.internalWorked(1);
		}

		progress.setWorkRemaining(1000);

		for (IFolder nP : currentFolder.getFolders()) {
			processPackage(nP, progress.newChild(1));

		}
	}

	// / <summary>
	// / Adds the connectors for only the outcomes.
	// / </summary>
	// / <param name="requirement">Requirement.</param>
	private void addConnector(IArchimateElement source, IArchimateElement dest) {
		TNode tSource = null;
		TNode tDest = null;

		if (source != null) {
			tSource = NodeFactory.createNode(source);
		}
		if (dest != null) {
			tDest = NodeFactory.createNode(dest);
		}
		if (dest != null && source != null) {
			SimpleConnector sc = new SimpleConnector(tSource, tDest);
			if (!nodeRelations.contains(sc)) {
				nodeRelations.add(sc);
			}
		}
	}

	@Override
	protected void writeFormat() throws IOException {

		for (SimpleConnector sc : nodeRelations) {
			if (sc != null) {
				// ProgressOutput.write(String.Format("Writing Item SC with Source of {0}",
				// "blah"));
				_file.write(sc.toString());
			} else {
			}
		}
		int i = 0;
		for (String key : allClusters.keySet()) {
			if (allClusters.get(key).size() > 0) {
				_file.write("subgraph \"cluster" + key + "\"{\n");
				_file.write("concentrate=true;\n");
				if (i == Colors.NUMCOLORS) {
					i = 0;
				}
				_file.write("color=\"" + Colors.PhaseColors[i++] + "\";\n");
				_file.write("style=filled;\n");
				_file.write("clusterrank=global;\n");
				_file.write("outputorder=edgesfirst;\n");

				for (TNode node : allClusters.get(key)) {
					_file.write(node.toString());
				}

				_file.write("label=\"Phase " + key + "\";\n");
				_file.write("fontsize=20;fontname=Helvetica;\n");
				_file.write("}\n");
			}
		}
	}
}
