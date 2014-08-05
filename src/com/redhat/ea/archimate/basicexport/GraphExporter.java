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
package com.redhat.ea.archimate.basicexport;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IRelationship;

import com.redhat.ea.archimate.strategyexport.Exporter;
import com.redhat.ea.archimate.strategyexport.Messages;

public class GraphExporter extends Exporter {


	List<String> relationships = new ArrayList<String>();
	List<String> elements = new ArrayList<String>();

	public GraphExporter(IArchimateModel model, File file, String[] status) {
		super(model, file, status);
	}

	private void processPackage(IFolder f) {

		for (EObject eo : f.getElements()) {
			if (eo instanceof IArchimateElement) {
				if (eo instanceof IRelationship) {
					relationships.add(getRelationshipInfo((IRelationship) eo));
				}else{
					elements.add(getElementInfo((IArchimateElement)eo));
				}
			}
		}

		// now do the recursion
		for (IFolder f1 : f.getFolders()) {
			processPackage(f1);
		}

	}

	@Override
	protected void writeHeader() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeFormat() throws IOException {
		// TODO Auto-generated method stub
		for(String s: elements){
			_file.write(s);
		}
		for(String s: relationships){
			_file.write(s);
		}
	}

	@Override
	protected void writeFooter() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public IStatus runInUIThread(IProgressMonitor ipm) {
		// TODO Auto-generated method stub
		if (this.model.getFolders() != null
				&& this.model.getFolders().size() > 0) {
			for (IFolder f : this.model.getFolders()) {
				processPackage(f);
			}
		}
		writeFile();
		return new Status(Status.OK, Messages.Plugin_ID,
				"GraphExporter Completed");
	}

	public String getRelationshipInfo(IRelationship ir) {
		return String
				.format("start n1=node:node_auto_index(id='%s'), n2=node:node_auto_index(id='%s') create (n1)-[:%s {id:'%s'}]->(n2);\n",
						ir.getSource().getId(), ir.getTarget().getId(), ir.getClass().getSimpleName(), ir.getId());
	}

	public String getElementInfo(IArchimateElement iae){
		return String
				.format("create (x%s:%s {id:'%s', name:'%s', description:'%s', type:'%s'});\n", iae.getId(), iae.getClass().getSimpleName(),
						iae.getId(), iae.getName(), iae.getDocumentation(), iae.getClass().getSimpleName());
				
				//create (a1:Product {name:"p"})
	}

}
