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

public final class NameFunctions {

	// / <summary>
	// / Name functions. Handles a variety of functions for normalizing names
	// for the output formats.
	// / </summary>

	public NameFunctions() {
	}

	// / <summary>
	// / Normalizes the name to replace the { and } brackets with "x" and "-"
	// with "_".
	// / This is used to produce a name form the GUID in the EA object model for
	// output in a standard format
	// / (i.e. .dot or .xml or .html).
	// / </summary>
	// / <returns>
	// / The name.
	// / </returns>
	// / <param name='n'>
	// / N.
	// / </param>
	public static String normalizeName(String n) {
		return n.replace("{", "x").replace("}", "x").replace("-", "_");
	}

	// / <summary>
	// / Splits the name
	// / </summary>
	// / <returns>
	// / The name.
	// / </returns>
	// / <param name='n'>
	// / N.
	// / </param>
	public static String splitName(String n) {
		String nn = null;
		if (n.indexOf("1.") == 0) {
			int _pos = n.indexOf(" ");
			nn = n.substring(0, _pos) + "\\n" + n.substring(_pos + 1);
		} else {
			nn = n;
		}
		return shortenName(nn);

	}

	// / <summary>
	// / Breaks the name with "\n" based on 15 characters or whitespace. Used
	// for text wrapping.
	// / </summary>
	// / <returns>
	// / The name.
	// / </returns>
	// / <param name='n'>
	// / N.
	// / </param>
	public static String shortenName(String n) {
		int start = 0;
		String nn = null;
		while (start < 15 && start > -1 && n.length() >= 15) {
			start = n.indexOf(" ", start + 1);
		}

		if (start > 0) {
			nn = n.substring(0, start) + "\\n"
					+ shortenName(n.substring(start + 1));
		} else {
			nn = n;
		}
		return nn;
	}

	// / <summary>
	// / Removes the spaces and ":" from the String.
	// / </summary>
	// / <returns>
	// / The spaces.
	// / </returns>
	// / <param name='n'>
	// / N.
	// / </param>
	public static String removeSpaces(String n) {
		return cleanup(n.replace(" ", "").replace(":", ""));
	}

	// / <summary>
	// / Cleanup the specified String by replacing & < and > with XML escaped
	// sequences.
	// / </summary>
	// / <param name='s'>
	// / S.
	// / </param>
	public static String cleanup(String s) {

		if (s != null) {
			
			return s.replace("&", "&amp;").replace("<", "&lt;")
					.replace(">", "&gt;").replace("\n", "\\n")
					.replace("\r", "\\n").replace("/", "")
					.replace("\"", "&quot;");
			// return
			// s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\n","\\n").replace("\r","\\n").replace("/","");
		} else {
			return s;
		}

	}

}
