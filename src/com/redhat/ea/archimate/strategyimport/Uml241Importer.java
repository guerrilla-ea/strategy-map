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
package com.redhat.ea.archimate.strategyimport;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IGoal;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.IRelationship;
import com.archimatetool.model.IWorkPackage;

public class Uml241Importer {

	Element root;
	IArchimateModel model;
	XPathFactory xFactory;
	NamespaceContext context;

	XPath xParser;

	public IArchimateModel getModel() {
		return this.model;
	}

	String modelName;

	public static String PHASE_KEY = "Phase";
	public static String COMPLEXITY_KEY = "Complexity";
	public static String STATUS_KEY = "Status";

	SortedMap<String, IArchimateElement> allElements = new TreeMap<String, IArchimateElement>();
	SortedMap<String, IFolder> goalFolders = new TreeMap<String, IFolder>();
	SortedMap<String, IFolder> workFolders = new TreeMap<String, IFolder>();
	SortedMap<String, IFolder> allFolders = new TreeMap<String, IFolder>();
	SortedMap<String, IRelationship> allRelations = new TreeMap<String, IRelationship>();

	IFolder diagramFolder;
	IFolder relationFolder;

	public Uml241Importer(File f, IArchimateModel m) {
		this(f);
		this.model = m;
		if (this.model.getName() == null || this.model.getName().equals("")) {
			this.model.setName(this.modelName);
		}
	}

	private Uml241Importer(File f) {

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document document = builder.parse(f);
			root = document.getDocumentElement();
		} catch (Exception e) {

		}

		this.modelName = ((Element) (root
				.getElementsByTagName("packagedElement").item(0)))
				.getAttribute("name");

		xFactory = XPathFactory.newInstance();
		xParser = xFactory.newXPath();
		xParser.setNamespaceContext(new NamespaceContextResolver());

	}

	public void doImport() throws Exception {
		parseFolders();

		// get a list of all the folders so we can place the elements
		allFolders.putAll(workFolders);
		allFolders.putAll(goalFolders);

		parseElements();
		parseRelationships();

		createDiagrams();
	}

	private void parseFolders() {
		// create a top level default folder for the views
		Element topFolder = ((Element) (root
				.getElementsByTagName("packagedElement").item(0)));

		IFolder baseStrategy = IArchimateFactory.eINSTANCE.createFolder();
		// baseStrategy.setType(FolderType.USER);
		baseStrategy.setName(modelName);
		this.model.getFolder(FolderType.DIAGRAMS).getFolders()
				.add(baseStrategy);
		diagramFolder = baseStrategy;

		IFolder baseRelation = IArchimateFactory.eINSTANCE.createFolder();
		baseRelation.setName(modelName);
		this.model.getFolder(FolderType.RELATIONS).getFolders()
				.add(baseRelation);
		relationFolder = baseRelation;

		// create folders under the Motivation and Implementation & Migration
		// folders
		// to match the imported structure
		// find the folder that starts with "Objectives" for the motivation
		// section

		try {
			Element objectivesTopFolder = (Element) xParser
					.evaluate(
							"//packagedElement[@type='uml:Package' and contains(@name, 'Objectives')]",
							root, XPathConstants.NODE);
			IFolder parentMotivation = this.model
					.getFolder(FolderType.MOTIVATION);
			if (objectivesTopFolder != null) {
				addFolder(objectivesTopFolder, parentMotivation, goalFolders,
						true);
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Unable to add Objectives folder.");
		}

		IFolder parentImplementation = this.model
				.getFolder(FolderType.IMPLEMENTATION_MIGRATION);
		addFolder(topFolder, parentImplementation, workFolders, false);
	}

	private void addFolder(Element folderInfo, IFolder containingFolder,
			Map<String, IFolder> keepTrack, boolean isObjectives) {

		if (!keepTrack.containsKey(folderInfo.getAttribute("xmi:id"))) {
			IFolder f = IArchimateFactory.eINSTANCE.createFolder();
			f.setId(folderInfo.getAttribute("xmi:id"));
			f.setName(folderInfo.getAttribute("name"));
			// f.setType(FolderType.USER);
			if (!goalFolders.containsKey(f.getId())) {
				containingFolder.getFolders().add(f);
				NodeList nl = folderInfo.getElementsByTagName("packagedElement");
				for (int i = 0; i < nl.getLength(); i++) {
					try {
						Element e = (Element) nl.item(i);
	
						if ("uml:Package".equals(e
										.getAttribute("xmi:type"))) {

							// if this is an objectives folder,
							// we need to create Driver elements for each of the
							// sub folders
							if (isObjectives) {
								// find the element that is the package so we
								// can get the documentation for it.
								String id = e.getAttribute("xmi:id");
								Element coreElement = (Element) xParser
										.evaluate(
												String.format(
														"//elements/element[@type='uml:Package' and @idref='%s']",
														id), root,
												XPathConstants.NODE);

								IArchimateElement iae = IArchimateFactory.eINSTANCE
										.createDriver();
								iae.setId(e.getAttribute("xmi:id"));
								iae.setName(e.getAttribute("name"));
								iae.setDocumentation(xParser.evaluate(
										"properties/@documentation",
										coreElement));
								f.getElements().add(iae);
							}
							addFolder(e, f, keepTrack, isObjectives);
						}
					} catch (Exception e) {
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
				}
			}
			keepTrack.put(f.getId(), f);
		}
	}

	/**
	 * Parse the file and get a list of all the elements. Put them in the
	 * correct folder.
	 */
	private void parseElements() throws Exception {
		NodeList eles = root.getElementsByTagName("element");

		for (int i = 0; i < eles.getLength(); i++) {
			Element e = (Element) eles.item(i);
			String typ = e.getAttribute("xmi:type");

			if ("uml:Activity".equals(typ) || "uml:Requirement".equals(typ)) {
				IArchimateElement element = null;
				if ("uml:Activity".equals(typ)) {
					element = IArchimateFactory.eINSTANCE.createWorkPackage();
				} else {
					element = IArchimateFactory.eINSTANCE.createGoal();
				}
				setProperties(e, element);
				try {
					String packageId = xParser.evaluate("model/@package", e);
					allFolders.get(packageId).getElements().add(element);
				} catch (Exception except) {
					except.printStackTrace();
				}
				allElements.put(element.getId(), element);
			}

		}
	}

	private void createDiagrams() {

		NodeList diagrams = root.getElementsByTagName("diagram");

		for (int i = 0; i < diagrams.getLength(); i++) {
			try {
				Element e = (Element) diagrams.item(i);

				String diagramId = e.getAttribute("xmi:id");
				// create a new diagram and put it in the right folder
				IDiagramModel diagram = IArchimateFactory.eINSTANCE
						.createArchimateDiagramModel();
				diagram.setId(diagramId);
				diagram.setName(xParser.evaluate("properties/@name", e));
				diagram.setConnectionRouterType(IDiagramModel.CONNECTION_ROUTER_SHORTEST_PATH);
				diagramFolder.getElements().add(diagram);

				// now add the elements to the diagram
				NodeList elements = e.getElementsByTagName("element");
				for (int j = 0; j < elements.getLength(); j++) {
					Element element = (Element) elements.item(j);
					String elementId = element.getAttribute("subject");
					if (allElements.containsKey(elementId)) {
						IDiagramModelArchimateObject diaEle = IArchimateFactory.eINSTANCE
								.createDiagramModelArchimateObject();
						diaEle.setArchimateElement(allElements.get(elementId));

						Geometry g = new Geometry(
								element.getAttribute("geometry"));
						diaEle.setBounds(g.x, g.y, g.width, g.height);
						diagram.getChildren().add(diaEle);
					} else if (allRelations.containsKey(elementId)) {
						// it is a relation, so we need to add it to the
						// diagram. These should come after
						// all of the elements based on the way the xml format
						// works
						createAndAddConnectionsToView(diagram,
								allRelations.get(elementId));
					}
				}

			} catch (Exception except) {
				except.printStackTrace();
			}
		}

	}

	private void createAndAddConnectionsToView(IDiagramModel diagramModel,
			IRelationship relationship) {
		List<IDiagramModelComponent> sources = DiagramModelUtils
				.findDiagramModelComponentsForElement(diagramModel,
						relationship.getSource());
		List<IDiagramModelComponent> targets = DiagramModelUtils
				.findDiagramModelComponentsForElement(diagramModel,
						relationship.getTarget());

		for (IDiagramModelComponent dmcSource : sources) {
			for (IDiagramModelComponent dmcTarget : targets) {
				IDiagramModelArchimateConnection dmc = IArchimateFactory.eINSTANCE
						.createDiagramModelArchimateConnection();
				dmc.setRelationship(relationship);
				dmc.connect((IDiagramModelObject) dmcSource,
						(IDiagramModelObject) dmcTarget);
			}
		}
	}

	private void parseRelationships() {
		NodeList relations = root.getElementsByTagName("connector");

		for (int i = 0; i < relations.getLength(); i++) {
			Element e = (Element) relations.item(i);

			try {

				String sourceId = xParser.evaluate("source/@idref", e);
				String targetId = xParser.evaluate("target/@idref", e);

				// get the source and target to determine the type
				IRelationship relation = null;

				IArchimateElement sourceElement = allElements.get(sourceId);
				IArchimateElement targetElement = allElements.get(targetId);

				if (sourceElement instanceof IWorkPackage
						&& targetElement instanceof IWorkPackage) {
					relation = IArchimateFactory.eINSTANCE
							.createFlowRelationship();
				} else if (sourceElement instanceof IWorkPackage
						&& targetElement instanceof IGoal) {
					relation = IArchimateFactory.eINSTANCE
							.createRealisationRelationship();
				} else if (sourceElement instanceof IGoal
						&& targetElement instanceof IWorkPackage) {
					relation = IArchimateFactory.eINSTANCE
							.createAssociationRelationship();
				} else if (sourceElement instanceof IGoal
						|| targetElement instanceof IGoal) {
					relation = IArchimateFactory.eINSTANCE
							.createInfluenceRelationship();
				} else {
					relation = IArchimateFactory.eINSTANCE
							.createAssociationRelationship();
				}
				relation.setSource(sourceElement);
				relation.setTarget(targetElement);
				relation.setId(e.getAttribute("xmi:idref"));
				// relation.setName(String.format("%s - %s",
				// sourceElement.getName(), targetElement.getName()));

				relationFolder.getElements().add(relation);
				allRelations.put(relation.getId(), relation);
			} catch (Exception except) {
				System.err.println("Unable to process relationship.");
			}
		}
	}

	private void setProperties(Element e, IArchimateElement target)
			throws XPathExpressionException {

		target.setId(e.getAttribute("xmi:idref"));
		target.setName(e.getAttribute("name"));

		target.setDocumentation(xParser
				.evaluate("properties/@documentation", e));

		// create the properties
		IProperty phase = IArchimateFactory.eINSTANCE.createProperty();
		phase.setKey(PHASE_KEY);
		phase.setValue(xParser.evaluate("project/@phase", e));

		IProperty status = IArchimateFactory.eINSTANCE.createProperty();
		status.setKey(STATUS_KEY);
		status.setValue(xParser.evaluate("project/@status", e));

		IProperty complexity = IArchimateFactory.eINSTANCE.createProperty();
		complexity.setKey(COMPLEXITY_KEY);
		complexity.setValue(calcComplexity(xParser.evaluate(
				"project/@complexity", e)));

		target.getProperties().add(phase);
		target.getProperties().add(status);
		target.getProperties().add(complexity);

	}

	private String calcComplexity(String i) {
		try {
			int v = Integer.parseInt(i);
			switch (v) {
			case 1:
				return "Easy";
			case 2:
				return "Medium";
			case 3:
				return "Difficult";
			default:
				return "Easy";
			}
		} catch (Exception e) {
			return "Easy";
		}
	}

	private class Geometry {

		public int x;
		public int y;
		public int width;
		public int height;

		public Geometry(String toParse) throws Exception {
			String[] val = toParse.split(";");
			x = Integer.parseInt(val[0].substring(5));
			y = Integer.parseInt(val[1].substring(4));
			int x2 = Integer.parseInt(val[2].substring(6));
			int y2 = Integer.parseInt(val[3].substring(7));
			width = x2 - x;
			height = y2 - y;
		}

	}

}
