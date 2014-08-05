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
package com.redhat.ea.archimate.strategyexport.util;

import java.util.List;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.util.ArchimateModelUtils;

import static com.redhat.ea.archimate.FillDefaultPropertiesAction.DefaultProperties.STATUS;
import static com.redhat.ea.archimate.FillDefaultPropertiesAction.DefaultProperties.LEVEL;
import static com.redhat.ea.archimate.FillDefaultPropertiesAction.DefaultProperties.PHASE;

public class ExportUtils extends ArchimateModelUtils {
	
	public static final String getPropertyValue(IArchimateElement element, String propertyName){
		String retval = null;
		List<IProperty> properties = element.getProperties();
		if (properties.isEmpty()) {return retval;}
		for(IProperty thing : properties){
			if(thing.getKey().equals(propertyName)){
				retval = thing.getValue();
				break;
			}
		}
		return retval;
		
	}
	
	public static final String getPropertyValue(IArchimateElement element, String propertyName, String defaultValue){
		String value = getPropertyValue(element, propertyName);
		if(value == null || value.equals("")){
			return defaultValue;
		}
		return value;
	}
	
	public static final String getStatus(IArchimateElement element){		
		return getPropertyValue(element, STATUS.getPropertyName(), STATUS.getDefaultValue());
	}
	
	public static final String getPhase(IArchimateElement element){
		return getPropertyValue(element, PHASE.getPropertyName(), PHASE.getDefaultValue());
	}
	
	public static final String getLevel(IArchimateElement element){
		return getPropertyValue(element,LEVEL.getPropertyName(), LEVEL.getDefaultValue());
	}
}