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

/// <summary>
/// Colors used for the export formats for the node types and states. Also used for the background elements.
/// </summary>
public class Colors {
	public static int NUMCOLORS = 7;

	public static String Decision = "#fdae61";

	public static String Text = "#000000";

	public static String Warning = "#ca0020";
	
	public static String None = "#ffffff";

	public class Outcome {
		public static final String Future = "#c2e699";
		
		public static final String InProgress = "#78c679";

		public static final String Finished = "#238443";

		public static final String Proposed = "#ffffcc";
	}

	public class Node {
		public static final String Future = "#c2e699";

		public static final String InProgress = "#78c679";

		public static final String Finished = "#238443";

		public static final String Proposed = "#ffffcc";
	}

	// "fffbf0"= activity light color from SparxEA
	// private static String[] _nodeColors = {"#bfdce8", "#8daae8", "#5b78e8",
	// "#2946e8", "#0014de", "#0000b6", "#000084" };
	// private static String[] _nodeColors = {"#fffbf0", "#fff6dd", "#fff1c9",
	// "#ffedb5", "#ffe8a1", "#ffe38d", "#ffde79"};
	// private static String[] _textColors = { "#000000", "#000000", "#000000",
	// "#000000", "#000000", "#000000", "#000000" };

	// "#ccffcc"= outcome light color from SparxEA
	// private static String[] _outcomeColors = { "#e8ffc8", "#c8ff96",
	// "#96ff64", "#78ff32", "#46db00", "#14a900", "#007700" };
	// private static String[] outcomeColors = {"#ccffcc", "#bbffbb", "#aaffaa",
	// "#99ff99", "#88ff88", "#77ff77", "#66ff66"};

	// private static String[] _decisionColors = { "#ffffc0", "#ffff64",
	// "#ffd132", "#ff9f00", "#f56d00", "#c33b00", "#910900" };
	// private static String[] _phaseColors = { "#e9ddaf", "#decd87", "#d3bc5f",
	// "#c8ab37", "#a0892c", "#786721", "#504416" };
	private static final String[] _phaseColors = { "#eff3ff", "#c6dbef",
			"#9ecae1", "#6baed6", "#4292c6", "#2171b5", "#084594" };

	// private static String[] _completedColors = {"#00b800", "#00b400",
	// "#00ac00", "#00a400", "#009800","#009400","#008c00"};
	// private static String[] _inProgressColors = {"","","","","","",""};
	// private static String[] _warningColors =
	// {"#ff5555","#ff4141","#ff2d2d","#ff1919","#ff0505","#f00000","#dc0000"};

	// / <summary>
	// / Gets the phase colors.
	// / </summary>
	// / <value>
	// / The phase colors.
	// / </value>
	public static final String[] PhaseColors = _phaseColors;

}
