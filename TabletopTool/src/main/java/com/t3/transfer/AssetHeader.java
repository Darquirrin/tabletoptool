/*
 * Copyright (c) 2014 tabletoptool.com team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     rptools.com team - initial implementation
 *     tabletoptool.com team - further development
 */
package com.t3.transfer;

import java.io.Serializable;

public class AssetHeader implements Serializable {
	private Serializable id;
	private String name;
	private long size;
	
	public AssetHeader(Serializable id, String name, long size) {
		this.id = id;
		this.size = size;
		this.name = name;
	}

	public Serializable getId() {
		return id;
	}

	public long getSize() {
		return size;
	}
	
	public String getName() {
		return name;
	}
}
