/**
 * Copyright 2012 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.task;

import com.example.plugin.action.BaseAction;
import com.mentor.chs.plugin.task.IXTaskProgress;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TaskProgressBar extends JDialog
{

	private IXTaskProgress progress = null;
	private JProgressBar progBar = null;
	private JLabel lblstatusText = new JLabel();

	public TaskProgressBar(IXTaskProgress prog)
	{
		progress = prog;
	}

	public TaskProgressBar(JProgressBar in_progBar, Frame frame)
	{ //progressbar
		super(frame, true);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, 2, 2, 2);

		setPreferredSize(new Dimension(450, 200));
		setTitle("Running PDF Generation Task ");
		progBar = in_progBar;
		progBar.setMinimum(0);
		progBar.setMaximum(0);
		progBar.setStringPainted(true);
		progBar.setString("");
		panel.add(lblstatusText, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridy++;

		panel.add(progBar, gbc);
		getContentPane().add(panel, BorderLayout.CENTER);
		BaseAction.centerWindow(this);
		pack();
	}

	public void setMinimumMaximum(int min, int max)
	{
		if (progBar != null) {
			progBar.setMinimum(min);
			progBar.setMaximum(max);
		}
	}

	public void progressText(String text)
	{
		if (progress != null) {
			progress.progressText(text);
		}
		else {
			lblstatusText.setText(text);
		}
	}

	public void progressPercentage(final long percentage)
	{
		if (progress != null) {
			progress.progressPercentage((int)percentage);
		}
		else {
			progBar.setValue((int) percentage);
		}
	}
}
