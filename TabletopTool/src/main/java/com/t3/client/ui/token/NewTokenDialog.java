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
package com.t3.client.ui.token;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import com.jeta.forms.components.image.ImageComponent;
import com.t3.client.AppPreferences;
import com.t3.client.TabletopTool;
import com.t3.client.swing.AbeillePanel;
import com.t3.client.swing.GenericDialog;
import com.t3.language.I18N;
import com.t3.model.Token;
import com.t3.swing.SwingUtil;
import com.t3.util.ImageManager;

/**
 * This dialog is used to display all of the token states and notes to the user.
 */
public class NewTokenDialog extends AbeillePanel<Token> {

	/**
	 * The size used to constrain the icon.
	 */
	public static final int SIZE = 64;

	private final Token token;
	private boolean success;

	private final int centerX;
	private final int centerY;

	private GenericDialog dialog;

	/**
	 * Create a new token notes dialog.
	 * 
	 * @param token
	 *            The token being displayed.
	 */
	public NewTokenDialog(Token token, int x, int y) {
		super("com/t3/client/ui/forms/newTokenDialog.xml");

		this.token = token;
		centerX = x;
		centerY = y;

		panelInit();
	}

	public void showDialog() {
		dialog = new GenericDialog("New Token", TabletopTool.getFrame(), this) {
			@Override
			protected void positionInitialView() {

				// Position over the drop spot
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				Dimension size = getSize();
				int x = centerX - size.width / 2;
				int y = centerY - size.height / 2;
				if (x < 0) {
					x = 0;
				}
				if (y < 0) {
					y = 0;
				}
				if (x + size.width > screenSize.width) {
					x = screenSize.width - size.width;
				}
				if (y + size.height > screenSize.height) {
					y = screenSize.height - size.height;
				}

				setLocation(x, y);
			}
		};

		bind(token);

		getRootPane().setDefaultButton(getOKButton());
		dialog.showDialog();
	}

	public ImageComponent getTokenIconPanel() {
		return (ImageComponent) getComponent("tokenIcon");
	}

	public JTextField getNameTextField() {
		return (JTextField) getComponent("@name");
	}

//	
//	public JTextField getGMNameTextField() {
//		return (JTextField) getComponent("gmName");
//	}
//
//	public JRadioButton getNPCTypeRadio() {
//		return (JRadioButton) getComponent("npcType");
//	}
//	
//	public JRadioButton getMarkerTypeRadio() {
//		return (JRadioButton) getComponent("markerType");
//	}
//	
//	public JRadioButton getPCTypeRadio() {
//		return (JRadioButton) getComponent("pcType");
//	}
//	
	public JButton getOKButton() {
		return (JButton) getComponent("okButton");
	}

	public JButton getCancelButton() {
		return (JButton) getComponent("cancelButton");
	}

	public JCheckBox getShowDialogCheckbox() {
		return (JCheckBox) getComponent("showDialogCheckbox");
	}

//	public void initNameTextField() {
//		getNameTextField().setText(token.getName());
//	}
//	
//	public void initGMNameTextField() {
//		getGMNameTextField().setText(token.getGMName());
//	}
//	
//	public void initNPCTypeRadio() {
//		getNPCTypeRadio().setSelected(true);
//	}
//	
//	public void initMarkerTypeRadio() {
//		getMarkerTypeRadio().setVisible(false);
//	}
//	
	public void initTokenIconPanel() {
		getTokenIconPanel().setPreferredSize(new Dimension(100, 100));
		getTokenIconPanel().setMinimumSize(new Dimension(100, 100));
		getTokenIconPanel().setIcon(getTokenIcon());
	}

	public void initOKButton() {
		getOKButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				success = true;
				if (!getShowDialogCheckbox().isSelected()) {
					AppPreferences.setShowDialogOnNewToken(false);
				}
				if (getNameTextField().getText().equals("")) {
					TabletopTool.showError(I18N.getText("msg.error.emptyTokenName"));
					return;
				}
				if (commit()) {
					dialog.closeDialog();
				}
			}
		});
	}

	public void initCancelButton() {
		getCancelButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				success = false;
				dialog.closeDialog();
			}
		});
	}

	public boolean isSuccess() {
		return success;
	}

//	/**
//	 * Update the token to match the state of the dialog
//	 */
//	public void updateToken() {
//
//		token.setName(getNameTextField().getText());
//		token.setGMName(getGMNameTextField().getText());
//		if (getNPCTypeRadio().isSelected()) {
//			token.setType(Token.Type.NPC);
//		}
//		if (getPCTypeRadio().isSelected()) {
//			token.setType(Token.Type.PC);
//		}
//		if (getMarkerTypeRadio().isSelected()) {
//			token.setType(Token.Type.NPC);
//			token.setLayer(Zone.Layer.OBJECT);
//			token.setGMNote("Marker"); // In order for it to be recognized as a marker, it needs something in the notes field 
//			token.setVisible(false);
//		}
//	}
//
	/**
	 * Get and icon from the asset manager and scale it properly.
	 * 
	 * @return An icon scaled to fit within a cell.
	 */
	private Icon getTokenIcon() {

		// Get the base image && find the new size for the icon
		BufferedImage assetImage = ImageManager.getImageAndWait(token.getImageAssetId());

		// Need to resize?
		Dimension imgSize = new Dimension(assetImage.getWidth(), assetImage.getHeight());
		SwingUtil.constrainTo(imgSize, SIZE);
		BufferedImage image = new BufferedImage(imgSize.width, imgSize.height, Transparency.BITMASK);
		Graphics2D g = image.createGraphics();
		g.drawImage(assetImage, (SIZE - imgSize.width) / 2, (SIZE - imgSize.height) / 2, imgSize.width, imgSize.height, null);
		g.dispose();
		return new ImageIcon(image);
	}

}
