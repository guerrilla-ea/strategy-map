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
package com.redhat.ea.archimate.strategyexport.global;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IGoal;
import com.archimatetool.model.IWorkPackage;

/// <summary>
/// Node factory. Creates nodes in the .dot language format.
/// </summary>
public class NodeFactory {
	// / <summary>
	// / Creates the node.
	// / </summary>
	// / <returns>
	// / The node.
	// / </returns>
	// / <param name='item'>
	// / Item.
	// / </param>
	// / <param name='color'>
	// / Color.
	// / </param>
	public static TNode createNode(IArchimateElement item) {
		TNode oo = null;
		String nName = item.getName();
		//String nName = NameFunctions.cleanup(item.getName());
		if (item instanceof IGoal) {
			oo = new Outcome(item.getId(), nName, item);
		} else if (item instanceof IWorkPackage) {
			oo = new Activity(item.getId(), nName, item);
		} else {
			oo = null;
		}
		return oo;
	}
}
