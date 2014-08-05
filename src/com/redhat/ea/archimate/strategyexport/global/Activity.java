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

import com.redhat.ea.archimate.strategyexport.global.types.Colors;
import com.redhat.ea.archimate.strategyexport.global.types.ShapeEnum;
import com.redhat.ea.archimate.strategyexport.global.types.StyleEnum;

public class Activity extends TNode {

	// / <summary>
	// / Activity type of node.
	// / </summary>

	public Activity(String uid, String name, IArchimateElement source) {
		super(uid, name, source);
		this.setShape(ShapeEnum.Mrecord);
		this.setStyle(StyleEnum.filled);
		this.setRealColor("black");
	}

	@Override
	public String toString() {
		return String
				.format("\"%s\" [label=\"%s\", shape=%s, style=%s, color=\"%s\", fillcolor=\"%s\", fontcolor=\"%s\", width=\"%f\", height=\"%f\"];\n",
						this.getuID(), this.getName(), this.getShape().toString(),
						this.getStyle().toString(), this.getRealColor(), this.getFillColor(),
						this.getFontColor(), this.getWidth(), this.getHeight());
	}

	@Override
	protected void setNodeColor() {
		switch (this.getState()) {
		case Approved:
			this.setFillColor(Colors.Node.InProgress);
			break;
		case Implemented:
			this.setFillColor(Colors.Node.Finished);
			break;
		case Mandatory:
			this.setFillColor(Colors.Warning);
			break;
		case Proposed:
			this.setFillColor(Colors.Node.Proposed);
			break;
		case Validated:
			this.setFillColor(Colors.Node.Future);
			break;
		default:
			this.setFillColor(Colors.None);
			break;
		}
	}

	// / <summary>
	// / Sets the size according to the Element's Complexity property
	// / </summary>
	@Override
	protected void setSize() {
		switch (this.getLevel()) {
		case Difficult:
			this.setWidth(6.25);
			this.setHeight(3.5);
			break;
		case Medium:
			this.setWidth(4.5);
			this.setHeight(2.5);
			break;
		default:
			this.setWidth(2.0);
			this.setHeight(0.5);
			break;
		}

	}

}
