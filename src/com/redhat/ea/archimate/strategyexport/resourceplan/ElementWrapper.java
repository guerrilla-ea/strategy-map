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

import com.archimatetool.model.IArchimateElement;

public class ElementWrapper
{
	private String name;

	private IArchimateElement source;

	public ElementWrapper (IArchimateElement s)
	{
		this.name = s.getName();
		this.source = s;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IArchimateElement getSource() {
		return source;
	}

	public void setSource(IArchimateElement source) {
		this.source = source;
	}

	@Override
	public boolean equals(Object obj){
		if (obj == null) {
			return false;
		}
		if(obj instanceof ElementWrapper){
			if(((ElementWrapper)obj).getSource().getId().equals(this.getSource().getId())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return this.getName().hashCode();
	}
	
}
