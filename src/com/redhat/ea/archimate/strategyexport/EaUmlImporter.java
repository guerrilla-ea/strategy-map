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
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.archimatetool.editor.model.DiagramModelUtils;
import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.model.IModelImporter;
import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IDiagramModel;
import com.archimatetool.model.IDiagramModelArchimateConnection;
import com.archimatetool.model.IDiagramModelArchimateObject;
import com.archimatetool.model.IDiagramModelComponent;
import com.archimatetool.model.IDiagramModelObject;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IRelationship;
import com.redhat.ea.archimate.strategyimport.Uml241Importer;

/**
 */
@SuppressWarnings("nls")
public class EaUmlImporter implements IModelImporter {

	String MY_EXTENSION_WILDCARD = "*.xml"; //$NON-NLS-1$

	@Override
	public void doImport() throws IOException {

		boolean runThis = false;
		IArchimateModel m = null;
		boolean isNewModel = false;

		int numModelsOpen = IEditorModelManager.INSTANCE.getModels().size();

		if (numModelsOpen > 0) {
			if ((MessageDialog
					.openConfirm(
							Display.getCurrent().getActiveShell(),
							"Strategy Importer",
							"Warning: This utility will import into the first open model.\nPress OK to continue."))) {
				runThis = true;
				m = IEditorModelManager.INSTANCE.getModels().get(0);

			}
		} else {
			// create a new model to use;
			m = IArchimateFactory.eINSTANCE.createArchimateModel();
			m.setDefaults();
			runThis = true;
			isNewModel = true;
		}

		if (runThis) {
			File file = askOpenFile();
			if (file == null) {
				return;
			}

			Uml241Importer importer = new Uml241Importer(file, m);

			try {
				importer.doImport();
				// And open the Model in the Editor
				if (isNewModel) {
					IEditorModelManager.INSTANCE.openModel(importer.getModel());
				}
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}

			MessageDialog.openInformation(
					Display.getCurrent().getActiveShell(), "Strategy Importer",
					"Completed import!");
		}
	}

	protected void createAndAddConnectionsToView(IDiagramModel diagramModel,
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

	protected IDiagramModelArchimateObject createAndAddElementToView(
			IDiagramModel diagramModel, IArchimateElement element, int x,
			int y, int width, int height) {
		IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE
				.createDiagramModelArchimateObject();
		dmo.setArchimateElement(element);
		dmo.setBounds(x, y, width, height);
		diagramModel.getChildren().add(dmo);
		return dmo;
	}

	protected IDiagramModel createAndAddView(IArchimateModel model,
			String name, String id) {
		IDiagramModel diagramModel = IArchimateFactory.eINSTANCE
				.createArchimateDiagramModel();
		diagramModel.setName(name);
		diagramModel.setId(id);
		IFolder folder = model.getDefaultFolderForElement(diagramModel);
		folder.getElements().add(diagramModel);
		return diagramModel;
	}

	protected File askOpenFile() {
		FileDialog dialog = new FileDialog(Display.getCurrent()
				.getActiveShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD,
				"*.xml" }); //$NON-NLS-1$
		String path = dialog.open();
		return path != null ? new File(path) : null;
	}
}
