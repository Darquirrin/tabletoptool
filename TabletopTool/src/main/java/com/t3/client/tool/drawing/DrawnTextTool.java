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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.Style;

import com.t3.client.swing.TwoToneTextPane;
import com.t3.client.ui.zone.ZoneRenderer;
import com.t3.model.drawing.DrawnLabel;
import com.t3.model.drawing.Pen;
import com.t3.util.guidreference.ZoneReference;

/**
 * A text tool that uses a text component to allow text to be entered on the
 * display and then renders it as an image.
 * 
 * @author jgorrell
 * @version $Revision: 5945 $ $Date: 2006-03-11 02:57:18 -0600 (Sat, 11 Mar
 *          2006) $ $Author: azhrei_fje $
 */
public class DrawnTextTool extends AbstractDrawingTool implements
		MouseMotionListener {

	/*---------------------------------------------------------------------------------------------
	 * Instance Variables
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Flag used to indicate that the anchor has been set.
	 */
	private boolean anchorSet;

	/**
	 * The anchor point originally selected
	 */
	private Point anchor = new Point();

	/**
	 * The bounds of the display rectangle
	 */
	private Rectangle bounds = new Rectangle();

	/**
	 * The text pane used to paint the text.
	 */
	private TwoToneTextPane textPane;

	/*---------------------------------------------------------------------------------------------
	 * Constructors
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * A transparent color used in the background
	 */
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	/*---------------------------------------------------------------------------------------------
	 * Constructors
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Initialize the tool icon
	 */
	public DrawnTextTool() {
//		try {
//			setIcon(new ImageIcon(
//					ImageIO
//							.read(getClass()
//									.getClassLoader()
//									.getResourceAsStream(
//											"com/t3/client/image/Tool_Draw_Write.gif"))));
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		} // endtry
	}

	/*---------------------------------------------------------------------------------------------
	 * Tool & AbstractDrawingTool Abstract Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * @see com.t3.client.tool.drawing.AbstractDrawingTool#paintOverlay(com.t3.client.ui.zone.ZoneRenderer,
	 *      java.awt.Graphics2D)
	 */
	@Override
	public void paintOverlay(ZoneRenderer aRenderer, Graphics2D aG) {
		if (!anchorSet)
			return;
		aG.setColor(Color.BLACK);
		aG.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	/**
	 * @see com.t3.client.ui.Tool#getTooltip()
	 */
	@Override
	public String getTooltip() {
		return "tool.text.tooltip";
	}

	/**
	 * @see com.t3.client.ui.Tool#getInstructions()
	 */
	@Override
	public String getInstructions() {
		return "tool.text.instructions";
	}

	/**
	 * @see com.t3.client.ui.Tool#resetTool()
	 */
	@Override
	protected void resetTool() {
		anchorSet = false;
		if (textPane != null)
			renderer.remove(textPane);
		textPane = null;
		renderer.repaint();
	}

	/*---------------------------------------------------------------------------------------------
	 * MouseListener Interface Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent event) {
		// Do nothing
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		if (!anchorSet) {
			anchor.x = event.getX();
			anchor.y = event.getY();
			anchorSet = true;
		} else {
			setBounds(event);

			// Create a text component and place it on the renderer's component
			textPane = createTextPane(bounds, getPen(), "sanserif-BOLD-20");
			renderer.add(textPane);
			textPane.requestFocusInWindow();

			// Make the enter key addthe text
			KeyStroke k = KeyStroke.getKeyStroke("ENTER");
			textPane.getKeymap().removeKeyStrokeBinding(k);
			textPane.getKeymap().addActionForKeyStroke(k, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent aE) {
					completeDrawable();
				}
			});
		}
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent aE) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent aE) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent aE) {
		// TODO Auto-generated method stub

	}

	/*---------------------------------------------------------------------------------------------
	 * MouseMotionListener Interface Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent event) {
		if (!anchorSet)
			return;
		if (textPane != null)
			return;
		setBounds(event);
		renderer.repaint();
	}

	/**
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent aE) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void installKeystrokes(Map<KeyStroke, Action> actionMap) {
		super.installKeystrokes(actionMap);
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), null);
		actionMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), null);
	}

	/*---------------------------------------------------------------------------------------------
	 * Instance Methods
	 *-------------------------------------------------------------------------------------------*/

	/**
	 * Set the bounds for the text area.
	 * 
	 * @param event
	 *            The mouse event used in the calculation.
	 */
	private void setBounds(MouseEvent event) {
		bounds.x = Math.min(anchor.x, event.getX());
		bounds.y = Math.min(anchor.y, event.getY());
		bounds.width = Math.abs(anchor.x - event.getX());
		bounds.height = Math.abs(anchor.y - event.getY());
	}

	/**
	 * Finish drawing the text.
	 */
	private void completeDrawable() {

		// Create a drawable from the data and clean up the component.
		DrawnLabel label = new DrawnLabel(textPane.getText(), bounds,
				TwoToneTextPane.getFontString(textPane.getLogicalStyle()));
		textPane.setVisible(false);
		textPane.getParent().remove(textPane);
		textPane = null;

		// Tell everybody else
		completeDrawable(new ZoneReference(renderer.getZone()), getPen(), label);
		resetTool();
	}

	/**
	 * Create a text pane with the passed bounds, pen, and font data
	 * 
	 * @param bounds
	 *            Bounds of the new text pane
	 * @param pen
	 *            Pen used for foreground and background text colors.
	 * @param font
	 *            Font used to pain the text
	 * @return A text pane used to draw text
	 */
	public static TwoToneTextPane createTextPane(Rectangle bounds, Pen pen,
			String font) {
		// Create a text component and place it on the renderer's component
		TwoToneTextPane textPane = new TwoToneTextPane();
		textPane.setBounds(bounds);
		textPane.setOpaque(false);
		textPane.setBackground(TRANSPARENT);

		// Create a style for the component
		Style style = textPane.addStyle("default", null);
		TwoToneTextPane.setFont(style, Font.decode(font));
//		style.addAttribute(StyleConstants.Foreground, new Color(pen.getColor()));
//		style.addAttribute(StyleConstants.Background, new Color(pen.getBackgroundColor()));
		textPane.setLogicalStyle(style);
		return textPane;
	}
}
