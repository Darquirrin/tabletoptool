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

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.t3.client.AppStyle;
import com.t3.client.TabletopTool;
import com.t3.client.ui.zone.ZoneRenderer;
import com.t3.model.Zone;
import com.t3.model.ZonePoint;
import com.t3.model.drawing.DrawableColorPaint;
import com.t3.model.drawing.Pen;
import com.t3.model.drawing.Rectangle;

/**
 * @author drice
 * 
 *         TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style -
 *         Code Templates
 */
public class RectangleTopologyTool extends AbstractDrawingTool implements MouseMotionListener {
	private static final long serialVersionUID = 3258413928311830323L;

	protected Rectangle rectangle;

	public RectangleTopologyTool() {
		try {
			setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream("com/t3/client/image/tool/top-blue-rect.png"))));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	@Override
	// Override abstracttool to prevent color palette from
	// showing up
	protected void attachTo(ZoneRenderer renderer) {
		super.attachTo(renderer);
		// Hide the drawable color palette
		TabletopTool.getFrame().hideControlPanel();
	}

	@Override
	public boolean isAvailable() {
		return TabletopTool.getPlayer().isGM();
	}

	@Override
	public String getInstructions() {
		return "tool.recttopology.instructions";
	}

	@Override
	public String getTooltip() {
		return "tool.recttopology.tooltip";
	}

	@Override
	public void paintOverlay(ZoneRenderer renderer, Graphics2D g) {
		if (TabletopTool.getPlayer().isGM()) {
			Zone zone = renderer.getZone();
			Area topology = zone.getTopology();

			Graphics2D g2 = (Graphics2D) g.create();
			g2.translate(renderer.getViewOffsetX(), renderer.getViewOffsetY());
			g2.scale(renderer.getScale(), renderer.getScale());

			g2.setColor(AppStyle.topologyColor);
			g2.fill(topology);

			g2.dispose();
		}
		if (rectangle != null) {
			Pen pen = new Pen();
			pen.setEraser(getPen().isEraser());
			pen.setOpacity(AppStyle.topologyRemoveColor.getAlpha() / 255.0f);
			pen.setBackgroundMode(Pen.MODE_SOLID);

			if (pen.isEraser()) {
				pen.setEraser(false);
			}
			if (isEraser()) {
				pen.setBackgroundPaint(new DrawableColorPaint(AppStyle.topologyRemoveColor));
			} else {
				pen.setBackgroundPaint(new DrawableColorPaint(AppStyle.topologyAddColor));
			}
			paintTransformed(g, renderer, rectangle, pen);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			ZonePoint sp = getPoint(e);

			if (rectangle == null) {
				rectangle = new Rectangle(sp.x, sp.y, sp.x, sp.y);
			} else {
				rectangle.getEndPoint().x = sp.x;
				rectangle.getEndPoint().y = sp.y;

				int x1 = Math.min(rectangle.getStartPoint().x, rectangle.getEndPoint().x);
				int x2 = Math.max(rectangle.getStartPoint().x, rectangle.getEndPoint().x);
				int y1 = Math.min(rectangle.getStartPoint().y, rectangle.getEndPoint().y);
				int y2 = Math.max(rectangle.getStartPoint().y, rectangle.getEndPoint().y);

				Area area = new Area(new java.awt.Rectangle(x1, y1, x2 - x1, y2 - y1));
				if (isEraser(e)) {
					renderer.getZone().removeTopology(area);
					TabletopTool.serverCommand().removeTopology(renderer.getZone().getId(), area);
				} else {
					renderer.getZone().addTopology(area);
					TabletopTool.serverCommand().addTopology(renderer.getZone().getId(), area);
				}
				renderer.repaint();
				// TODO: send this to the server

				rectangle = null;
			}
			setIsEraser(isEraser(e));
		}
		super.mousePressed(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (rectangle == null) {
			super.mouseDragged(e);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		setIsEraser(isEraser(e));

		ZonePoint p = getPoint(e);
		if (rectangle != null) {
			if (rectangle != null) {
				rectangle.getEndPoint().x = p.x;
				rectangle.getEndPoint().y = p.y;
			}
			renderer.repaint();
		}
	}

	/**
	 * Stop drawing a rectangle and repaint the zone.
	 */
	@Override
	public void resetTool() {
		if (rectangle != null) {
			rectangle = null;
			renderer.repaint();
		} else {
			super.resetTool();
		}
	}
}
