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
package com.redhat.ea.archimate.strategyexport.objectives;

import java.util.List;

public class Objective {

	public String name;
	public String notes;
	public List<Outcome> outcomes;

	public Objective() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<Outcome> getOutcomes() {
		return outcomes;
	}

	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}

	public Objective(String name, String notes) {
		this.name = name;
		this.notes = notes;
	}

	private String formatMilestones() {
		StringBuilder s = new StringBuilder();
		s.append("<ol>");
		for (Outcome m : outcomes) {
			s.append(String.format("<li><strong>Phase: %s</strong>- %s</li>",
					m.getPhase(), m.getDescription()));
		}
		s.append("</ol>");
		return s.toString();
	}

	@Override
	public String toString() {
		return String.format("<tr><td>%s<td>%s</td><td>%s</td></tr>\n",
				this.name, this.notes, formatMilestones());
	}
}
