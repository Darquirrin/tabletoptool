/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package com.t3.swing;

import java.awt.Component;

import javax.swing.JPanel;

/**
 */
public class PositionalPanel extends JPanel {

	public PositionalPanel() {
		setLayout(new PositionalLayout());
	}
	
	public void addImpl(Component comp, Object constraints, int index) {
		
		if (!(constraints instanceof PositionalLayout.Position)) {
			throw new IllegalArgumentException("Use add(Component, PositionalLayout.Position)");
		}
		
		super.addImpl(comp, constraints, index);

		if (((PositionalLayout.Position) constraints) == PositionalLayout.Position.CENTER) {
		
			setComponentZOrder(comp, getComponents().length - 1);
		} else {
			setComponentZOrder(comp, 0);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#isOptimizedDrawingEnabled()
	 */
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
}
