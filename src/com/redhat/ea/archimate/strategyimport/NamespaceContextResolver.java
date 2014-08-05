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
package com.redhat.ea.archimate.strategyimport;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class NamespaceContextResolver implements NamespaceContext {

	@Override
	public String getNamespaceURI(String prefix) {
		if(prefix == null){
			throw new IllegalArgumentException("No prefix provided!");
		}else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)){
			return "http://www.omg.org/spec/UML/20110701";
		}else if(prefix.equals("uml")){
			return "http://www.omg.org/spec/UML/20110701";
		}else if(prefix.equals("xmi")){
			return "http://www.omg.org/spec/XMI/20110701";
		}else if(prefix.equals("thecustomprofile")){
			return "http://www.sparxsystems.com/profiles/thecustomprofile/1.0";
		}else{
			return XMLConstants.NULL_NS_URI;
		}
	}

	@Override
	public String getPrefix(String namespaceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<?> getPrefixes(String namespaceURI) {
		// TODO Auto-generated method stub
		return null;
	}

}
