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
package com.redhat.ea.archimate.strategyexport.resourceplan;

import java.util.*;

import com.redhat.ea.archimate.strategyexport.dependency.SimpleConnector;
import com.redhat.ea.archimate.strategyexport.global.NodeFactory;
import com.redhat.ea.archimate.strategyexport.global.TNode;
import com.redhat.ea.archimate.strategyexport.util.ExportUtils;
import com.redhat.ea.archimate.strategyexport.util.Tree;
import com.redhat.ea.archimate.strategyexport.util.TreeNode;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IGoal;
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.util.ArchimateModelUtils;

public class CriticalPath {
	// / <summary>
	// / Gets or sets the full critical path for the start IArchimateElement in
	// a tree structure form.
	// / </summary>
	// / <value>
	// / The full path.
	// / </value>
	private Tree<TreeNode<IArchimateElement>> fullPath;

	public Tree<TreeNode<IArchimateElement>> getFullPath() {
		return fullPath;
	}

	public void setFullPath(Tree<TreeNode<IArchimateElement>> fullPath) {
		this.fullPath = fullPath;
	}

	public Map<String, ElementWrapper> getAllElements() {
		return allElements;
	}

	public void setAllElements(Map<String, ElementWrapper> allElements) {
		this.allElements = allElements;
	}

	public Map<String, ElementWrapper> getOutcomeElements() {
		return outcomeElements;
	}

	public void setOutcomeElements(Map<String, ElementWrapper> outcomeElements) {
		this.outcomeElements = outcomeElements;
	}

	public List<SimpleConnector> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<SimpleConnector> connectors) {
		this.connectors = connectors;
	}

	// / <summary>
	// / Gets all IArchimateElements in a flattened list format.
	// / </summary>
	// / <value>
	// / All IArchimateElements.
	// / </value>
	private Map<String, ElementWrapper> allElements;

	private Map<String, ElementWrapper> outcomeElements;

	private IArchimateElement startElement;

	private List<String> inStatus = null;

	private List<SimpleConnector> connectors;

	private boolean includeOutcomes = false;
	protected Map<String, IArchimateElement> processedItems = new TreeMap<String, IArchimateElement>();
	private boolean isComplete = false;
	private TreeMap<String, List<TreeNode<IArchimateElement>>> _allClusters;

	public TreeMap<String, List<TreeNode<IArchimateElement>>> AllClusters() {
		if (isComplete) {
			return _allClusters;
		} else {
			String phase = ExportUtils.getPropertyValue(this.fullPath.getRoot()
					.getData(), "Phase");
			createClusters(this.fullPath.getRoot(), 0, phase);
			isComplete = true;
			return _allClusters;
		}
	}

	public CriticalPath(IArchimateElement se, List<String> inStat) {

		this.allElements = new TreeMap<String, ElementWrapper>();
		this.outcomeElements = new TreeMap<String, ElementWrapper>();
		this.connectors = new ArrayList<SimpleConnector>();
		_allClusters = new TreeMap<String, List<TreeNode<IArchimateElement>>>();

		this.fullPath = new Tree<TreeNode<IArchimateElement>>();
		this.startElement = se;
		this.inStatus = inStat;
	}

	public CriticalPath(IArchimateElement se, List<String> inStat,
			boolean includeOutcomes) {
		this(se, inStat);
		this.includeOutcomes = includeOutcomes;
	}

	public void process() {
		TreeNode<IArchimateElement> root = new TreeNode<IArchimateElement>(
				this.startElement, 1);

		ElementWrapper ew = new ElementWrapper(this.startElement);
		String key = String.format("%d}:%s", 0, this.startElement.getName());
		if (this.startElement instanceof IGoal
				&& !outcomeElements.containsValue(ew)) {
			outcomeElements.put(key, ew);
		}

		getPredecessors(root, 1);
		fullPath.setRoot(root);
		// Make sure we can add the start IArchimateElement.
		// if (root.Data.Type != IArchimateElementTypes.OUTCOME) {
		allElements.put(
				String.format("{0}:Completed- {1}", 0,
						this.startElement.getName()),
				new ElementWrapper(root.getData()));
		// }

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
	private void getPredecessors(TreeNode<IArchimateElement> element, int level) {

		for (IRelationship cn : ArchimateModelUtils
				.getTargetRelationships(element.getData())) {

			IArchimateElement source = cn.getSource();
			if (this.inStatus.contains(ExportUtils.getPropertyValue(source,
					"Status"))) {

				TreeNode<IArchimateElement> child = new TreeNode<IArchimateElement>(
						source, level);

				if (processedItems.containsKey(child.getData().getId())) {
					if (source instanceof IGoal) {
						if (this.includeOutcomes) {
							element.addChild(new TreeNode<IArchimateElement>(
									processedItems.get(child.getData().getId()),
									level));
						}
					} else {
						element.addChild(new TreeNode<IArchimateElement>(
								processedItems.get(child.getData().getId()),
								level));
					}

				} else {
					if (source instanceof IGoal) {
						if (this.includeOutcomes) {
							element.addChild(child);
						}
					} else {
						element.addChild(child);
					}
					getPredecessors(child, level + 1);
					processedItems
							.put(child.getData().getId(), child.getData());
				}

				String key = String.format("%d:%d", level, source.getName());
				ElementWrapper ew = new ElementWrapper(source);
				// make sure we only add once
				if (!allElements.containsValue(ew)) {
					if (source instanceof IGoal) {
						if (this.includeOutcomes) {
							allElements.put(key, ew);
						}
					} else {
						allElements.put(key, ew);
					}
				}

				if (source instanceof IGoal
						&& !outcomeElements.containsValue(ew)) {
					outcomeElements.put(key, ew);
				}

				IArchimateElement dest = cn.getTarget();
				// add the connector
				TNode sourceNode = NodeFactory.createNode(source);
				TNode destNode = NodeFactory.createNode(dest);
				SimpleConnector sc = new SimpleConnector(sourceNode, destNode);
				connectors.add(sc);
			}
		}
	}

	private void createClusters(TreeNode<IArchimateElement> node, int level,
			String phase) {

		// make sure the phase exists
		if (!_allClusters.containsKey(phase)) {
			_allClusters.put(phase,
					new ArrayList<TreeNode<IArchimateElement>>());
		}

		if (node.getData() instanceof IGoal) {
			if (ExportUtils.getPropertyValue(node.getData(), "Phase").equals(
					phase)) {
				_allClusters.get(phase).add(node);
			} else {
				String localPhase = ExportUtils.getPropertyValue(
						node.getData(), "Phase");
				if (!_allClusters.containsKey(localPhase)) {
					_allClusters.put(localPhase,
							new ArrayList<TreeNode<IArchimateElement>>());
				}
				_allClusters.get(localPhase).add(node);
			}
		} else {
			// add to the current phase that we care about
			_allClusters.get(phase).add(node);
		}

		// now process the children
		for (TreeNode<IArchimateElement> child : node.getChildren()) {
			// if it is an outcome, process the subnodes
			String subPhase = ExportUtils.getPropertyValue(node.getData(),
					"Phase");
			if (node.getData() instanceof IGoal) {
				if (subPhase.equals(phase)) {
					_allClusters.get(phase).add(node);
				} else {

					if (!_allClusters.containsKey(subPhase)) {
						_allClusters.put(subPhase,
								new ArrayList<TreeNode<IArchimateElement>>());
					}
					_allClusters.get(subPhase).add(node);
				}
			} else {
				// add to the current phase that we care about
				_allClusters.get(phase).add(node);
			}
			// now process the children
			createClusters(child, level++, subPhase);
		}
	}
}
