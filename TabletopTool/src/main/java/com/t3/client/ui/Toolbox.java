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
package com.t3.client.ui;

import java.awt.EventQueue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;

import com.t3.client.TabletopTool;
import com.t3.client.ui.zone.ZoneOverlay;
import com.t3.client.ui.zone.ZoneRenderer;
import com.t3.language.I18N;

/**
 */
public class Toolbox {
	private ZoneRenderer currentRenderer;
	private Tool currentTool;
	private final Map<Class<? extends Tool>, Tool> toolMap = new HashMap<Class<? extends Tool>, Tool>();
	private final ButtonGroup buttonGroup = new ButtonGroup();

	public void updateTools() {
		for (Tool tool : toolMap.values()) {
			tool.setEnabled(tool.isAvailable());
		}
	}

	public Tool getSelectedTool() {
		return currentTool;
	}

	public Tool getTool(Class<? extends Tool> toolClass) {
		return toolMap.get(toolClass);
	}

	public Tool createTool(Class<? extends Tool> toolClass) {
		Tool tool;
		try {
			Constructor<? extends Tool> constructor = toolClass.getDeclaredConstructor(new Class[] {});
			tool = constructor.newInstance(new Object[] {});
//			tool = constructor.newInstance((Object) null);

			buttonGroup.add(tool);
			toolMap.put(toolClass, tool);
			tool.setToolbox(this);
			return tool;
		} catch (InstantiationException e) {
			TabletopTool.showError(I18N.getText("msg.error.toolCannotInstantiate", toolClass.getName()), e);
		} catch (IllegalAccessException e) {
			TabletopTool.showError(I18N.getText("msg.error.toolNeedPublicConstructor", toolClass.getName()), e);
		} catch (NoSuchMethodException nsme) {
			TabletopTool.showError(I18N.getText("msg.error.toolNeedValidConstructor", toolClass.getName()), nsme);
		} catch (InvocationTargetException ite) {
			TabletopTool.showError(I18N.getText("msg.error.toolConstructorFailed", toolClass.getName()), ite);
		}
		return null;
	}

	public void setTargetRenderer(final ZoneRenderer renderer) {
		// Need to be synchronous with the timing of the invokes within this method
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Tool oldTool = currentTool;

				// Disconnect the current tool from the current renderer
				setSelectedTool((Tool) null);

				// Update the renderer
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						currentRenderer = renderer;
					}
				});
				// Attach the old tool to the new renderer
				setSelectedTool(oldTool);
			}
		});
	}

	public void setSelectedTool(Class<? extends Tool> toolClass) {
		Tool tool = toolMap.get(toolClass);
		if (tool != null && tool.isAvailable()) {
			tool.setSelected(true);
			setSelectedTool(tool);
		}
	}

	public void setSelectedTool(final Tool tool) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (tool == currentTool) {
					return;
				}
				if (currentTool != null) {
					if (currentRenderer != null) {
						currentTool.removeListeners(currentRenderer);
//						currentTool.addGridBasedKeys(currentRenderer, false);
						currentTool.detachFrom(currentRenderer);

						if (currentTool instanceof ZoneOverlay) {
							currentRenderer.removeOverlay((ZoneOverlay) currentTool);
						}
					}
				}
				// Update
				currentTool = tool;

				if (currentTool != null) {
					if (currentRenderer != null) {
						// We have a renderer at this point so we can figure out the grid type and add its keystrokes
						// to the PointerTool.
//						currentTool.addGridBasedKeys(currentRenderer, true);
						currentTool.addListeners(currentRenderer);
						currentTool.attachTo(currentRenderer);

						if (currentTool instanceof ZoneOverlay) {
							currentRenderer.addOverlay((ZoneOverlay) currentTool);
						}
					}
					if (TabletopTool.getFrame() != null) {
						TabletopTool.getFrame().setStatusMessage(I18N.getText(currentTool.getInstructions()));
					}
				}
			}
		});
	}
}
