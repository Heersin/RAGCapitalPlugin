/**
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.library;

import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryColorCode;
import com.mentor.chs.api.IXLibraryNoteSetter;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.library.IXLibraryTabNotifier;
import com.mentor.chs.plugin.library.IXLibraryTabPanel;

import javax.swing.JPanel;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.util.Set;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * This Modification Panel provides an example for adding modiciation history to the currently edited library part.
 *It demonstrates how to create a tab panel that uses the IXLibraryNoteSetter of IXLibraryObject to add a note to modification
 * history of a library part
 *  All the custom panels must inherit from JPanel
 * and implement  IXLibraryTabPanel
 */
public class LibraryModificationHistoryPanel extends JPanel implements IXLibraryTabPanel
{

	private static IXLibraryTabNotifier tabNotifier = null;
	private JTextArea notesText = new JTextArea();


	public LibraryModificationHistoryPanel()
	{
		notesText.setName("notesText");
		notesText.setLineWrap(true);
		notesText.setBorder(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setMinimumSize(new Dimension(350, 40));
		scrollPane.setPreferredSize(new Dimension(350, 40));
       	scrollPane.getViewport().add(notesText, null);

		JPanel jPanel1 = new JPanel();
		GridBagLayout gridBagLayout1 = new GridBagLayout();
		jPanel1.setLayout(gridBagLayout1);

		JLabel notesLabel = new JLabel("Modification History");
		jPanel1.add(notesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));
		jPanel1.add(scrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		setLayout(new GridBagLayout());
		add(jPanel1, new GridBagConstraints(0, 0, 0, 0, 1.0, 1.0
				, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 10), 0, 0));
		revalidate();
		repaint();
		notesText.addKeyListener(new KeyListener()
		{

			public void keyTyped(KeyEvent e)
			{
				if (tabNotifier != null) {
					tabNotifier.edited();
				}
			}

			public void keyPressed(KeyEvent e)
			{

			}

			public void keyReleased(KeyEvent e)
			{

			}
		});
	}

	/*
		  col span is two
	   */
	public int getColSpan()
	{
		return 2;
	}

	public int getRowSpan()
	{
		return 1;
	}

	public void refresh(IXApplicationContext libContext)
	{
	   Set<IXObject> objs = libContext.getSelectedObjects();
		if (objs == null || objs.isEmpty()) {
			return;
		}
		if (!objs.isEmpty()) {
			IXObject selObject = objs.iterator().next();
			if (selObject instanceof IXLibraryObject) {
				IXLibraryNoteSetter curnoteSetter = ((IXLibraryObject)selObject).getLibraryNoteSetter();
				if(curnoteSetter != null && curnoteSetter.getCurrentNote() != null){
					notesText.setText(curnoteSetter.getCurrentNote());
				}
				else{
					notesText.setText("");
				}
			}
		}
	}

	/*
	 * notifier is passed as a parameter during initilize of the tab panel.
	 * Custom tab panel needs to call edited() on notifier when ever there is a data modification made
	 * here we are adding notes to current object if part description or color code is changed 
	*/
	public void update(IXApplicationContext libContext)
	{
		Set<IXObject> objs = libContext.getSelectedObjects();
		if (objs == null || objs.isEmpty()) {
			return;
		}
		if (!objs.isEmpty()) {
			IXObject selObject = objs.iterator().next();
			if (selObject instanceof IXLibraryObject) {
				IXLibraryObject storedObject = ((IXLibraryObject)selObject).getStoredLibraryObject();
				if(storedObject != null){
				String existingDesc = "";
				String existingColCode = "";
				existingDesc = storedObject.getAttribute("description");
				IXLibraryColorCode colCode = storedObject.getColorCode();
				if(colCode != null){
					existingColCode = colCode.getAttribute("ColorCode");
				}
				String modifiedDesc =  selObject.getAttribute("description");
				colCode = ((IXLibraryObject)selObject).getColorCode();
				String modifiedColor =  "";
				if(colCode != null){
					modifiedColor = colCode.getAttribute("ColorCode");
				}
				String descChangeNote = "";
				String colCodeChangeNote = "";
				if(existingDesc != null && !existingDesc.equalsIgnoreCase(modifiedDesc)){
					descChangeNote = "Description modified : old value = " + existingDesc + ", new value = " +  modifiedDesc;
				}
				if(existingColCode != null && !existingColCode.equalsIgnoreCase(modifiedColor)){
					colCodeChangeNote = "Color Code modified : old value = " + existingColCode + ", new value = " +  modifiedColor;
				}
				IXLibraryNoteSetter attSetter = ((IXLibraryObject) selObject).getLibraryNoteSetter();
				if(attSetter != null){
					String finalString = "";
					if (!notesText.getText().isEmpty()) {
						finalString = "Message : " + notesText.getText();
					}
					 if(!descChangeNote.isEmpty()){
						finalString = finalString + "\\n" + descChangeNote;
					 }
					 if(!colCodeChangeNote.isEmpty()){
						finalString = finalString + "\\n" + colCodeChangeNote;
					 }
					attSetter.setCurrentNote(finalString);
					tabNotifier.edited();
				}
			}
			}
		}
	}

	/*
		tab notifier is cached
		 */
	public void initialize(IXLibraryTabNotifier notifier)
	{
		tabNotifier = notifier;
	}
}




