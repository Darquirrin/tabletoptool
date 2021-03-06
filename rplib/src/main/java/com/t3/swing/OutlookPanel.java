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
package com.t3.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * ButtonPanel.java
 * 
 * Trevor Croft 2002 CopyRight
 */
public class OutlookPanel extends JPanel {
    // TODO: Variable size buttons ?
    public static final int BUTTON_HEIGHT = 20;

    public OutlookPanel() {
        m_compList = new ArrayList<JButtonEx>();

        setLayout(null);
    }
    
    /**
     * @param label
     * @param component
     * @return index of the button
     */
    public int addButton(String label, JComponent component) {
        // Create the button
        JButtonEx button = new JButtonEx(label, m_compList.size(), component);
        button.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent ae) {
                int index = ((JButtonEx) ae.getSource()).getIndex();
                if (m_active.getIndex() == index && index > 0) {
                    index --;
                }
                setActive(index);
            }

        });

        // Update
        component.setVisible(false);
        m_compList.add(button);
        add(label, button);
        add(label, component);

        if (m_compList.size() == 1) {
            setActive(0);
        }
        
        repaint();
        return m_compList.size() - 1;
    }

    public int getButtonCount() {
        return m_compList.size();
    }
    
    public void setActive(int index) {
        // Sanity check
        if (index < 0 || index >= m_compList.size()) { return; }

        // Update old active
        if (m_active != null) {
            m_active.getComponent().setVisible(false);
        }

        // Set it
        m_active = m_compList.get(index);
        m_active.getComponent().setVisible(true);

        repaint();
    }

	public void setActive(String name) {
		setActive(getButtonIndex(name));
	}
	
	public int getButtonIndex(String name) {
		
		for (JButtonEx button : m_compList) {
			
			if (button.getText().equals(name)) {
				return button.m_index;
			}
		}
		
		return -1;
	}
	
    @Override
	public void paint(Graphics g) {
        int y = 0;

        Dimension size = getSize();

        g.setColor(getBackground());
        g.fillRect(0, 0, size.width, size.height);

        // TODO: This can be pulled out and put in a layout manager
        for (int count = 0; count < m_compList.size(); count++) {
            JButtonEx button = m_compList.get(count);

            // Position the button
            button.setBounds(0, y, size.width, BUTTON_HEIGHT);

            // Update
            y += BUTTON_HEIGHT;

            // Active Panel ?
            if (button == m_active) {
                // Calculate
                int height = getSize().height - (m_compList.size() * BUTTON_HEIGHT);

                // Stretch to take the available space
                button.m_component.setBounds(5, y, size.width - 6, height - 2);
                button.m_component.revalidate();

                y += height;
            }

        }

        // Paint them
        paintChildren(g);
    }

    // For convenience
    private class JButtonEx extends JButton {
        public JButtonEx(String label, int index, JComponent component) {
            super(label);
            m_index = index;
            setHorizontalAlignment(LEFT);
            m_component = component;
            setFocusPainted(false);
        }

        public int getIndex() {
            return m_index;
        }

        /**
         * We don't ever want the focus 
         */
        @Override
		public boolean isRequestFocusEnabled() {
            return false;
        }

        public JComponent getComponent() {
            return m_component;
        }

        private int        m_index;
        private JComponent m_component;
    }

    // Internal
    private List<JButtonEx> m_compList;
    private JButtonEx   m_active;
}
