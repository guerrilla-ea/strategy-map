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
package com.redhat.ea.archimate.strategyexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.IArchimateElement;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.redhat.ea.archimate.FillDefaultPropertiesAction.DefaultProperties;


public abstract class Exporter extends org.eclipse.ui.progress.UIJob{
	
	protected BufferedWriter _file;
	private File _f;
	protected String _fileLoc;
	protected List<String> inStatus;
	protected IArchimateModel model;
	
	public Exporter(IArchimateModel model, File file, String[] status){
		super(Messages.Plugin_ID);
		_f = file;
		this.model = model;
		this.inStatus = new ArrayList<String>();
		for(String s:status){
			this.inStatus.add(s);
		}
		
		for(IFolder folder : model.getFolders()){
			setDefaultProperties(folder);
		}
		
	}

	protected abstract void writeHeader() throws IOException;
	protected abstract void writeFormat() throws IOException;
	protected abstract void writeFooter() throws IOException;
	public abstract IStatus runInUIThread(IProgressMonitor ipm);
	
	
	public void writeFile(){
		try{
			_file = new BufferedWriter(new FileWriter(_f));
			writeHeader();
			writeFormat();
			writeFooter();
			_file.flush();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}finally{
			try {
				_file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void reportStatus(String message) {
		//Status s = new Status(0, Messages.Plugin_ID, message);
		//StatusManager.getManager().handle(s, StatusManager.LOG);
		System.out.println(message);
	}
	
	/**
	 * Set the default properties that we expect on all elements.
	 */
	private void setPropertiesOnElement(IArchimateElement iae){
		//see which ones are set versus not set
		for (DefaultProperties defaultProperty : DefaultProperties.values()) {
			if(findPropertyByName(iae, defaultProperty.getPropertyName())==null){
				iae.getProperties().add(defaultProperty.getProperty());	
			}
    	}
	}
	
	protected void setDefaultProperties(IFolder currentPackage){
		for (EObject x : currentPackage.getElements()) {
			if(x instanceof IArchimateElement){
				IArchimateElement s = (IArchimateElement) x;
				setPropertiesOnElement(s);
			}
		}
		for(IFolder nextf : currentPackage.getFolders()){
			setDefaultProperties(nextf);
		}
	}
	
	private IProperty findPropertyByName(IArchimateElement iae, String propName){
		IProperty p = null;
		for(IProperty iprop : iae.getProperties()){
			if(iprop.getKey().equals(propName)){
				p = iprop;
			}
		}
		return p;
	}
	
}
