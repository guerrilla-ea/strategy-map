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

public class Outcome extends TNode {

	/// <summary>
	/// Outcome node type.
	/// </summary>

		public int peripheries;

		public Outcome(String uid, String name, IArchimateElement source)
		{
			super(uid,name,source);
			this.setShape(ShapeEnum.box);
			this.setStyle(StyleEnum.filled);
			this.peripheries = 3;
			this.setRealColor("green");
		}


		@Override
		public String toString ()
		{
			return String.format ("\"%s\" [label=\"%s\", shape=%s, style=%s, peripheries=%s, fillcolor=\"%s\", color=\"%s\", fontcolor=\"%s\"];\n", this.getuID(), this.getName(), this.getShape().toString(), this.getStyle().toString(), this.peripheries, this.getFillColor(), this.getRealColor(), this.getFontColor());
		}

		@Override
		protected void setNodeColor ()
		{
			switch (this.getState()) {
			case Approved:
				this.setFillColor(Colors.Outcome.InProgress);
				break;
			case Implemented:
				this.setFillColor(Colors.Outcome.Finished);
				break;
			case Mandatory:
				this.setFillColor(Colors.Warning);
				break;
			case Proposed:
				this.setFillColor(Colors.Outcome.Proposed);
				break;
			case Validated:
				this.setFillColor(Colors.Outcome.Future);
				break;
			default:
				this.setFillColor(Colors.None);
				break;
			}
		}

	}


