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
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.redhat.ea.archimate.FillDefaultPropertiesAction.DefaultProperties;
import com.redhat.ea.archimate.strategyexport.Exporter;
import com.redhat.ea.archimate.strategyexport.global.TNode;
import com.redhat.ea.archimate.strategyexport.global.types.Colors;

public abstract class TDotExporter extends Exporter{
	
	protected List<TNode> unlinkedNodes= new ArrayList<TNode>();

	public TDotExporter(IArchimateModel model, File file, String[] status) {
		super(model, file, status);

	}

	@Override
	protected void writeHeader() throws IOException{
		_file.write("digraph {\n");
		_file.write("rankdir=LR;\n");
		_file.write("#rankdir=SFDP;\n");
		_file.write("#rankdir=NEATO;\n");
		_file.write("#rankdir=FDP;\n");
		_file.write("#rankdir=TWOPI;\n");
		_file.write("#rankdir=CIRCO;\n");
		_file.write("concentrate=true;\n");
		_file.write("ratio=.7;\n");
	}
	
	@Override
	protected void writeFooter() throws IOException{
		//create the legend
		_file.write("subgraph clusterLegend{\n");
		_file.write("label=\"Shape Legend\";\n");
		_file.write("color=gray;\n");
		_file.write("style=filled;\n");

		_file.write("Activity [label=\"Activity\",style=filled, shape=Mrecord, color=white];\n");
		_file.write(String.format ("ActivityFuture [label=\"Future\",style=filled, shape=Mrecord, fillcolor=\"%s\"];\n", Colors.Node.Future));
		_file.write(String.format ("ActivityInProgress [label=\"In Progress\",style=filled, shape=Mrecord, fillcolor=\"%s\"];\n", Colors.Node.InProgress));
		_file.write(String.format ("ActivityCompleted [label=\"Completed\",style=filled, shape=Mrecord, fillcolor=\"%s\"];\n", Colors.Node.Finished));
		_file.write(String.format ("ActivityProposed [label=\"Proposed\",style=filled, shape=Mrecord, fillcolor=\"%s\"];\n", Colors.Node.Proposed));
		_file.write(String.format ("ActivityOutofOrder [label=\"Unmet Dependencies!\",style=filled, shape=Mrecord, fillcolor=\"%s\"];\n", Colors.Warning));
		_file.write("Activity -> ActivityFuture;\n");
		_file.write("Activity -> ActivityInProgress;\n");
		_file.write("Activity -> ActivityCompleted;\n");
		_file.write("Activity -> ActivityProposed;\n");
		_file.write("Activity -> ActivityOutofOrder;\n");

		_file.write("Outcome [label=\"Outcome\",shape=box, style=filled, peripheries=3, color=white];\n");
		_file.write(String.format ("OutcomeFuture [label=\"Future\",shape=box, style=filled, peripheries=3, fillcolor=\"%s\"];\n", Colors.Outcome.Future));
		_file.write(String.format ("OutcomeInProgress [label=\"InProgress\",shape=box, style=filled, peripheries=3, fillcolor=\"%s\"];\n", Colors.Outcome.InProgress));
		_file.write(String.format ("OutcomeCompleted [label=\"Completed\",shape=box, style=filled, peripheries=3, fillcolor=\"%s\"];\n", Colors.Outcome.Finished));
		_file.write(String.format ("OutcomeProposed [label=\"Proposed\",shape=box, style=filled, peripheries=3, fillcolor=\"%s\"];\n", Colors.Outcome.Proposed));
		_file.write("Outcome -> OutcomeFuture;\n");
		_file.write("Outcome -> OutcomeInProgress;\n");
		_file.write("Outcome -> OutcomeCompleted;\n");
		_file.write("Outcome -> OutcomeProposed;\n");

		_file.write(String.format ("Decision [label=\"Decision\",fillcolor=\"%s\", style=filled, shape=diamond];\n", Colors.Decision));
		_file.write("}\n");
		
		//add the unlinked nodes
		if(unlinkedNodes.size()>0){
			_file.write("subgraph clusterUnlinked{\n");
			_file.write("label=\"Unlinked Nodes. (Please link to complete your map.)\";\n");
			_file.write("color=red;n");
			_file.write("style=filled;\n");
			for(TNode t : unlinkedNodes){
				_file.write(t.toString());
			}
			_file.write("}\n");
		}
		_file.write("}\n");
	}
	
	protected int getNextColorX (int c, int i)
	{
		c+=i;
		if(c>Colors.NUMCOLORS){
			c = 0;
		}
		return c;
	}
	

	
}
