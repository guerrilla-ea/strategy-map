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
package com.redhat.ea.archimate.strategyexport.global;

import com.redhat.ea.archimate.strategyexport.global.types.Colors;
import com.redhat.ea.archimate.strategyexport.global.types.LevelType;
import com.redhat.ea.archimate.strategyexport.global.types.ShapeEnum;
import com.redhat.ea.archimate.strategyexport.global.types.StateEnum;
import com.redhat.ea.archimate.strategyexport.global.types.StyleEnum;
import com.redhat.ea.archimate.strategyexport.util.ExportUtils;
import com.redhat.ea.archimate.strategyexport.util.NameFunctions;

import com.archimatetool.model.IArchimateElement;

public abstract class TNode extends BaseNode {

	private String _label = null;

	public String getName() {
		return _label;
	}

	public void setName(String value) {
		if (value != null) {
			_label = cleanup(value);
		}
	}

	private ShapeEnum shape;

	private String fillColor;
	private String fontColor;
	private String realColor;

	private String uID;
	private double width;
	private double height;
	private StateEnum state;
	private LevelType level;
	private StyleEnum style;

	private IArchimateElement source;

	public TNode(String uid, String name, IArchimateElement source) {
		this.uID = uid;
		this.setName(NameFunctions.splitName(name));
		this.source = source;

		this.fontColor = Colors.Text;

		// lookup the properties
		String _l = ExportUtils.getLevel(source);
		String _s = ExportUtils.getStatus(source);
		this.state = StateEnum.valueOf(_s);
		this.level = LevelType.valueOf(_l);

		this.style = StyleEnum.filled;

		setNodeColor();
		setSize();
	}

	public ShapeEnum getShape() {
		return shape;
	}

	public void setShape(ShapeEnum shape) {
		this.shape = shape;
	}

	public String getFillColor() {
		return fillColor;
	}

	public void setFillColor(String fillColor) {
		this.fillColor = fillColor;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public String getRealColor() {
		return realColor;
	}

	public void setRealColor(String realColor) {
		this.realColor = realColor;
	}

	public String getuID() {
		return uID;
	}

	public void setuID(String uID) {
		this.uID = uID;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public StateEnum getState() {
		return state;
	}

	public void setState(StateEnum state) {
		this.state = state;
	}

	public LevelType getLevel() {
		return level;
	}

	public void setLevel(LevelType level) {
		this.level = level;
	}

	public StyleEnum getStyle() {
		return style;
	}

	public void setStyle(StyleEnum style) {
		this.style = style;
	}

	public IArchimateElement getSource() {
		return source;
	}

	public void setSource(IArchimateElement source) {
		this.source = source;
	}

	protected abstract void setNodeColor();

	protected void setSize() {
		// this is here so by default, we do the default size. Subclasses can
		// override if they want to.
	}

	@Override
	public int hashCode() {
		return this.uID.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		TNode tnodeObj = (TNode) obj;
		if (tnodeObj == null) {
			return false;
		} else {
			return this.hashCode() == tnodeObj.hashCode();
		}
	}

	private String cleanup(String s) {
		return NameFunctions.cleanup(s);
	}

}
