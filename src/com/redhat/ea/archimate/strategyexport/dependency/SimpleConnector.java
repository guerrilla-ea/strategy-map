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

import com.redhat.ea.archimate.strategyexport.global.TNode;

public class SimpleConnector {
	public TNode source;
	public TNode getsource() {
		return source;
	}

	public void setsource(TNode source) {
		this.source = source;
	}

	public TNode getdestination() {
		return destination;
	}

	public void setdestination(TNode destination) {
		this.destination = destination;
	}


	public TNode destination; 

	public SimpleConnector (TNode s, TNode d)
	{
		this.source = s;
		this.destination = d;
	}


	@Override
	public boolean equals (Object obj)
	{
		SimpleConnector simpleConnectorObj = (SimpleConnector)obj;
		if (simpleConnectorObj == null || simpleConnectorObj.source == null || simpleConnectorObj.destination == null ||
		    this.source == null || this.destination == null) {
			return false;
		} else {
			if (this.source.hashCode () == simpleConnectorObj.source.hashCode () &&
				this.destination.hashCode () == simpleConnectorObj.destination.hashCode ())
			{
				return true;
			}else{
				return false;
			}
		}

	}

	@Override
	public int hashCode ()
	{
		return ((this.source==null?"":this.source.toString()) + (this.destination==null?"":this.destination.toString())).hashCode();
	}

	@Override
	public String toString ()
	{
		if (this.source != null && this.destination != null) {
			return String.format ("\"%s\" -> \"%s\" [tailport=e,headport=w];\n", this.source.getuID(), this.destination.getuID());
		}else{
			if(this.source == null && this.destination == null){
				return "";
				//return String.Format ("SNull -> DNull [tailport=e,headport=w];");
			}else{
				if(this.source == null){
					return "";
					//return String.Format("SNull -> %s [tailport=e,headport=w];", this.destination.Label);
				}else{
					return "";
					//return String.Format ("DNull -> %s [tailport=e,headport=w];", this.source.Label);
				}
			}
		}
	}

}
