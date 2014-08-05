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
package com.redhat.ea.archimate;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IProperty;

public class FillDefaultPropertiesAction extends Action implements IWorkbenchAction {
	
	private IArchimateModel model;

	public enum DefaultProperties {
		STATUS    ("Status", "Proposed"),
		PHASE     ("Phase", "1.0"),
		LEVEL     ("Complexity", "Easy"); 
		
		private final String propertyName;
		private final String defaultValue;
		
		private DefaultProperties(String propertyName, String defaultValue) {
			this.propertyName = propertyName;
			this.defaultValue = defaultValue;
		}
		
		public String getPropertyName() {
			return propertyName;
		}
		
		public String getDefaultValue() {
			return defaultValue;
		}
		
		public IProperty getProperty() {
			IProperty property = IArchimateFactory.eINSTANCE.createProperty();
			property.setKey(getPropertyName());
			property.setValue(getDefaultValue());
			return property;
		}
	}
	
	public FillDefaultPropertiesAction(IArchimateModel model) {
		this.model = model;
	}
	
	@Override
	public void run() {
    	for (DefaultProperties defaultProperty : DefaultProperties.values()) {
    		
    	}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}