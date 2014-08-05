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
import java.util.ArrayList;

public class TreeNode<T>
{

	private  T data;
	private List<TreeNode<T>> children;
	private int level;

	public TreeNode ()
	{
	}

	public TreeNode(T data, int l){
		this.data = data;
		this.children = new ArrayList<TreeNode<T>> ();
		this.level = l;
	}

	public TreeNode (T data, int l, List<TreeNode<T>> children)
	{
		this.children = children;
		this.data = data;
		this.level = l;
	}

	public void addChild (TreeNode<T> child)
	{
		this.children.add (child);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode<T>> children) {
		this.children = children;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}


}
