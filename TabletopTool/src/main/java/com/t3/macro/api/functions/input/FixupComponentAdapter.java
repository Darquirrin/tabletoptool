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
package com.t3.macro.api.functions.input;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/** Adjusts the runtime behavior of components */
public class FixupComponentAdapter extends ComponentAdapter {
	final InputPanel ip;

	public FixupComponentAdapter(InputPanel ip) {
		super();
		this.ip = ip;
	}

	@Override
	public void componentShown(ComponentEvent ce) {
		ip.runtimeFixup();
	}
}
