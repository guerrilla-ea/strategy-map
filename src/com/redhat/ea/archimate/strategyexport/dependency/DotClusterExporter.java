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
import java.util.SortedMap;
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
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.IWorkPackage;
import com.archimatetool.model.util.ArchimateModelUtils;
import com.redhat.ea.archimate.strategyexport.Messages;
import com.redhat.ea.archimate.strategyexport.global.NodeFactory;
import com.redhat.ea.archimate.strategyexport.global.TNode;
import com.redhat.ea.archimate.strategyexport.global.types.Colors;
import com.redhat.ea.archimate.strategyexport.util.ExportUtils;

public class DotClusterExporter extends TDotExporter {

	private Queue<IArchimateElement> requirements;

	private SortedMap<String, List<TNode>> allClusters;
	protected List<TNode> nodeLabels;
	protected List<SimpleConnector> nodeRelations;

	protected List<TNode> processed;

	protected SubMonitor monitor;

	public DotClusterExporter(IArchimateModel model, String[] status, File file) {
		super(model, file, status);

		requirements = new LinkedList<IArchimateElement>();
		allClusters = new TreeMap<String, List<TNode>>();

		nodeLabels = new ArrayList<TNode>();
		nodeRelations = new ArrayList<SimpleConnector>();

		processed = new ArrayList<TNode>();
	}

	public IStatus runInUIThread(IProgressMonitor ipm) {
		try {
			monitor = SubMonitor.convert(ipm);
			if (this.model.getFolders() != null
					&& this.model.getFolders().size() > 0) {
				for (IFolder f : this.model.getFolders()) {
					processPackage(f, monitor);
				}

				SubMonitor loopMonitor = monitor.newChild(100);

				for (IArchimateElement ele : requirements) {
					
										
					
					// convert the IArchimateElement list to a TNode list
					String phase = ExportUtils.getPropertyValue(ele, "Phase");
					if (phase == null || phase == "") {
						/*
						 * do not cluster null or empty phases (null vs empty will have different behavior on graph visibility beyond cluster, though
						 */
						continue;
					}
					List<TNode> cluster = createCluster(
							NodeFactory.createNode(ele), 0, phase, loopMonitor);
					if (allClusters.containsKey(phase)) {
						allClusters.get(phase).addAll(cluster);
					} else {
						allClusters.put(phase, cluster);
					}
				}
			}
			writeFile();

		} catch (StackOverflowError err) {
			err.printStackTrace();
		} 
		
		return new Status(Status.OK, Messages.Plugin_ID,
				"DotClusterExporter Completed");
	}

	private void processPackage(IFolder currentPackage, IProgressMonitor m) {

		SubMonitor progress = SubMonitor.convert(m, 50);

		reportStatus(String.format("Processing Package: %s",
				currentPackage.getName()));

		reportStatus(String.format("\t\tIArchimateElements found: %d",
				currentPackage.getElements().size()));

		SubMonitor loopProgress = progress.newChild(90).setWorkRemaining(
				currentPackage.getElements().size());

		for (EObject x : currentPackage.getElements()) {
			if(x instanceof IArchimateElement){
				IArchimateElement s = (IArchimateElement) x;
				
				if ((s instanceof IWorkPackage || s instanceof IGoal)
					&& (ExportUtils.getPhase(s) != null)
					&& (this.inStatus.contains(ExportUtils.getStatus(s)))) {
					if (s instanceof IGoal) {
						requirements.add(s);
					}

					List<IRelationship> connectors = ArchimateModelUtils
							.getSourceRelationships(s);

					reportStatus(String.format("\t\t\tConnections found: %s",
							connectors.size()));

					for (IRelationship cn : connectors) {

						TNode tSource = null;
						TNode tDest = null;

						if (cn.getSource() != null) {
							TNode o = NodeFactory.createNode(cn.getSource());
							if (o != null && !nodeLabels.contains(o)) {
								nodeLabels.add(o);
							}
							tSource = o;
						}

						else {
							reportStatus(String
									.format("\t\t\tWARNING: %s Source IArchimateElement not found for connector.",
											cn.getName()));
						}
						if (cn.getTarget() != null) {
							TNode o2 = NodeFactory.createNode(cn.getTarget());
							if (o2 != null && !nodeLabels.contains(o2)) {
								nodeLabels.add(o2);
							}
							tDest = o2;
						} else {
							reportStatus(String
									.format("\t\t\tWARNING: %s Destination IArchimateElement not found",
											cn.getName()));
						}

						if (cn.getTarget() != null && cn.getSource() != null) {
							SimpleConnector sc = new SimpleConnector(tSource,
									tDest);
							if (!nodeRelations.contains(sc)) {
								nodeRelations.add(sc);
							}
						}
					}

				}
				loopProgress.internalWorked(1);
			}
		}

		progress.setWorkRemaining(1000);
		for (IFolder nP : currentPackage.getFolders()) {
			processPackage(nP, progress.newChild(1));
		}
	}

	private List<TNode> createCluster(TNode node, int level, String phase,
			IProgressMonitor p) {

		// SubMonitor progress = SubMonitor.convert(p, 100);

		if (processed.contains(node)) {
			return new ArrayList<TNode>();
		}

		List<IArchimateElement> recursionList;
		List<TNode> cluster;

		String clusterId = phase;

		reportStatus(String.format("Creating Cluster on %s for phase %s",
				node.getSource().getName(), clusterId));

		reportStatus(String.format( 
				"Creating Cluster on %s at level %s for phase %s",
				node.getSource().getName(), level, clusterId));

		String nodePhase = ExportUtils.getPropertyValue(node.getSource(), "Phase");
		// set the correct phase if we have Goal
		if (node.getSource() instanceof IGoal) {
			if (clusterId != null && (clusterId != nodePhase)) {
				// not in the same phase where we are, so we are done.
				return new ArrayList<TNode>();
			} else {
				clusterId = nodePhase;
			}
		}

		reportStatus("Cluster process of activity...");

		cluster = new ArrayList<TNode>();
		cluster.add(node);

		reportStatus("Cluster getting predecessors...");

		recursionList = getPredecessors(node.getSource(), clusterId);

		// figure out what to return
		// SubMonitor loopProgress = progress.newChild(recursionList.size());

		if (recursionList != null && recursionList.size() > 0) {
			for (IArchimateElement ele : recursionList) {
				if (!processed.contains(ele)) {
					TNode eNode = NodeFactory.createNode(ele);
					cluster.addAll(createCluster(eNode, level++, clusterId, p));
				}
			}
		}

		processed.add(node);

		return cluster;
	}

	// / <summary>
	// / Gets the predecessors for the IArchimateElement passed. This returns
	// all activities and any outcomes
	// / (requirements) from the phase passed.
	// / </summary>
	// / <returns>
	// / The predecessors.
	// / </returns>
	// / <param name='IArchimateElement'>
	// / IArchimateElement.
	// / </param>
	// / <param name='currentPhase'>
	// / Current phase.
	// / </param>
	private List<IArchimateElement> getPredecessors(IArchimateElement iElement,
			String currentPhase) {
		List<IArchimateElement> list = new ArrayList<IArchimateElement>();

		for (IRelationship cn : ArchimateModelUtils
				.getTargetRelationships(iElement)) {

			IArchimateElement source = cn.getSource();
			if (source instanceof IWorkPackage) {
				//add any work items
				list.add(source);
			} else {
				//only add the IGoals if we're in the same phase
				String nodePhase = ExportUtils.getPropertyValue(iElement, "Phase");
				if (source instanceof IGoal && currentPhase == nodePhase) {
					list.add(source);
				}

			}
		}
		return list;
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
				if(i==Colors.NUMCOLORS){
					i=0;
				}
				_file.write("color=\"" + Colors.PhaseColors[i++] + "\";\n");
				_file.write("style=filled;\n");
				_file.write("clusterrank=global;\n");
				_file.write("outputorder=edgesfirst;\n");

				for (TNode node : allClusters.get(key)) {
					_file.write(node.toString());
					nodeLabels.remove(node);
				}

				_file.write("label=\"Phase " + key + "\";\n");
				_file.write("fontsize=20;fontname=Helvetica;\n");
				_file.write("}\n");
			}
		}
		
		//write out any of the nodes that weren't part of a cluster
		for (TNode node : nodeLabels) {
			reportStatus(String
					.format("Writing Node %s", node.getSource().getName()));
			_file.write(node.toString());
		}
	}
}