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
package com.redhat.ea.archimate.strategyexport.global.types;

import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Workflow for element state which is used to denote visual attribues.
/// Proposed -> Validated -> Approved -> Implemented.
/// Mandatory designates an element that is being executed out of the dependency order.
/// </summary>
public enum StateEnum {

	Committed,
	
	Proposed,

	Validated,

	Approved,

	Implemented,

	Mandatory;

	public static String getTranslatedState (StateEnum se)
	{
		String res;
		switch (se) {
		case Approved:
			res = "In Progress";
			break;
		case Implemented:
			res = "Completed";
			break;
		case Mandatory:
			res = "Unmet Dependencies!";
			break;
		case Proposed:
			res = "Proposed";
			break;
		case Validated:
		case Committed:
			res = "Future";
			break;
		default:
			res = "Indeterminate";
			break;
		}
		return res;
	}
	
	public static String getStateColor (StateEnum se)
	{
		String res;
		switch (se) {
		case Approved:
			res = Colors.Node.InProgress;
			break;
		case Implemented:
			res = Colors.Node.Finished;
			break;
		case Mandatory:
			res = Colors.Warning;
			break;
		case Proposed:
			res = Colors.Node.Proposed;
			break;
		case Validated:
		case Committed:
			res = Colors.Node.Future;
			break;
		default:
			res = "#ffffff";
			break;
		}
		return res;
	}
	
	public static List<String> GetStateEnumList()
	{
		List<String> r = new ArrayList<String>();

		r.add(StateEnum.Proposed.toString());
		r.add(StateEnum.Validated.toString());
		r.add(StateEnum.Approved.toString());
		r.add(StateEnum.Implemented.toString());
		r.add(StateEnum.Mandatory.toString());
		return r;
	}
	
}
