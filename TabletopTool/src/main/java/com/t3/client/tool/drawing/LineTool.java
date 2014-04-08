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
package com.t3.client.tool.drawing;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 * Tool for drawing freehand lines.
 */
public class LineTool extends AbstractLineTool implements MouseMotionListener {
	private static final long serialVersionUID = 3258132466219627316L;
	private Point tempPoint;

	public LineTool() {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("com/t3/client/image/tool/draw-blue-strtlines.png"))));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	public String getTooltip() {
		return "tool.line.tooltip";
	}

	@Override
	public String getInstructions() {
		return "tool.line.instructions";
	}

	////
	// MOUSE LISTENER
	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (getLine() == null) {
				startLine(e);
				setIsEraser(isEraser(e));
			} else {
				tempPoint = null;
				stopLine(e);
			}
		} else if (getLine() != null) {
			// Create a joint
			tempPoint = null;
			return;
		}
		super.mousePressed(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (getLine() == null) {
			super.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (getLine() != null) {
			if (tempPoint != null) {
				removePoint(tempPoint);
			}
			tempPoint = addPoint(e);
		}
		super.mouseMoved(e);
	}
}
