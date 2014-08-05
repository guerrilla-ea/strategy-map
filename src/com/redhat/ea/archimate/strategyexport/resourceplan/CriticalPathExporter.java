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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;

import com.redhat.ea.archimate.strategyexport.Exporter;
import com.redhat.ea.archimate.strategyexport.Messages;
import com.redhat.ea.archimate.strategyexport.util.NameFunctions;
import com.redhat.ea.archimate.strategyexport.util.TreeNode;

/// <summary>
/// Determines the Critical Path required to reach a given Outcome or Activity. This travereses the entire dependency chain backwards from the
/// end state and builds a tree structure that can be analyzed.
/// <br/>Also contains methods for writing an arbitrary XML format with the tree structure.
/// </summary>
public class CriticalPathExporter extends Exporter {

	// / <summary>
	// / Gets or sets the start element for which we will determine the critical
	// path.
	// / </summary>
	// / <value>
	// / The start element.
	// / </value>
	private IArchimateElement startElement;

	public IArchimateElement getStartElement() {
		return startElement;
	}

	public void setStartElement(IArchimateElement startElement) {
		this.startElement = startElement;
	}

	public CriticalPath getCp() {
		return cp;
	}

	public void setCp(CriticalPath cp) {
		this.cp = cp;
	}

	private CriticalPath cp;

	public CriticalPathExporter(IArchimateModel model, String[] status, File f) {
		super(model, f, status);

	}

	public void process() {
		cp = new CriticalPath(startElement, this.inStatus);
		cp.process();
	}

	public IStatus runInUIThread(IProgressMonitor ipm) {
		process();
		writeFile();
		return new Status(Status.OK, Messages.Plugin_ID,
				"Critical Path Exporter Completed");
	}

	@Override
	protected void writeFooter() throws IOException {
		_file.write("</CriticalPath>\n");
	}

	@Override
	protected void writeFormat() throws IOException {
		// _file.WriteLine(String.Format("<Element level=\"{0}\" id=\"{1}\" name=\"{2}\">",
		// 0, FullPath.Root.Data.ElementGUID,
		// NameFunctions.Cleanup(FullPath.Root.Data.Name)));
		writeChildren(cp.getFullPath().getRoot(), 0);
	}

	private void writeChildren(TreeNode<IArchimateElement> t, int level)
			throws IOException {
		for (int i = 0; i <= level; i++) {
			_file.write("\t");
		}
		_file.write(String.format(
				"<Element level=\"%d\" id=\"%s\" name=\"%s\">\n", level, t
						.getData().getId(), NameFunctions.cleanup(t.getData()
						.getName())));
		for (TreeNode<IArchimateElement> tc : t.getChildren()) {
			writeChildren(tc, level + 1);
		}
		for (int i = 0; i <= level; i++) {
			_file.write("\t");
		}
		_file.write("</Element>\n");
	}

	@Override
	protected void writeHeader() throws IOException {
		_file.write("<?xml version=\"1.0\"?>\n");
		_file.write("<CriticalPath>\n");
	}

}
