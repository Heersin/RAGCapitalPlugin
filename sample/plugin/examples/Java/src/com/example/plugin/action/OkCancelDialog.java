/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example plugin.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

package com.example.plugin.action;

import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXAction;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 */
public class OkCancelDialog extends JDialog
{

	protected boolean sucessful;

	public void addContent(JComponent component)
	{
		getContentPane().add(component, BorderLayout.CENTER);
	}

	public OkCancelDialog(IXAction action, IXApplicationContext context)
	{
		super(context.getParentFrame(), true);
		setTitle(action.getName());

		sucessful = false;

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		JButton okButton = new JButton();
		okButton.setText("OK");
		okButton.addActionListener(new OKButtonListener());

		JButton cancelButton = new JButton();
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new CancelButtonListener());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
	}

	public boolean wasSuccessful()
	{
		return sucessful;
	}

	public void displayDialog()
	{
		pack();
		BaseAction.centerWindow(this);
		setVisible(true);
	}

	protected void okButtonPressed()
	{
		sucessful = true;
		setVisible(false);
	}

	private class OKButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			okButtonPressed();
		}
	}

	protected void cancelButtonPressed()
	{
		sucessful = false;
		setVisible(false);
	}

	private class CancelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			cancelButtonPressed();
		}
	}
}
