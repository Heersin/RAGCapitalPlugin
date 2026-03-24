/**
 * Copyright 2011 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.sbom;

import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXWireEnd;
import com.mentor.chs.api.IXTerminal;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.sbom.IXSBOMContext;
import com.mentor.chs.plugin.sbom.IXOperationParametersSerializer;
import com.mentor.chs.plugin.sbom.IXOperationUI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Icon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.awt.GridBagLayout;
import java.text.ParseException;
import java.net.URL;

/**
 * Wire preparation opertion: cut and strip
 */
public class CutAndStripWireOperation extends AbstractOperation implements IXOperationParametersSerializer, IXOperationUI
{

	// Minimum length parameter
	private static final double DEFAULT_PARAM_MIN_LENGTH_MM = 10;
	private static final int TEXT_FIELD_MIN_WIDTH = 5;
	private double minLengthValue = DEFAULT_PARAM_MIN_LENGTH_MM;

	public CutAndStripWireOperation()
	{
		super("Example operation: Cut and strip wires",
				"1.0",
				"Cut and strip wire at both ends");
	}

	@Override
	public IXOperationType getType()
	{
		return OperationTypeImpl.WIRE_PREPARATION;
	}

	@Override
	public int getOrderHint()
	{
		return 100;
	}

	@Override public IXOperationParametersSerializer getParametersSerializer()
	{
		return this;
	}

	@Override public IXOperationUI getUI()
	{
		return this;
	}
	
	@Override public String serialize()
	{
		return Double.toString(minLengthValue);
	}

	@Override public void deSerialize(String s) throws ParseException
	{
		if (s == null) {
			return;
		}
		try {
			minLengthValue = Double.parseDouble(s);
		}
		catch (NumberFormatException e) {
			throw new ParseException(e.getMessage() + " - Not a number: " + s, 0);
		}
	}

	@Override
	public JPanel getParametersEditor(final IXParameterChangeListener listener)
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("Min. length: "));

		JTextField txt = new JTextField(Double.toString(minLengthValue), TEXT_FIELD_MIN_WIDTH);

		// Validate entry
		txt.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override public void insertUpdate(DocumentEvent e)
			{
				textFieldUpdated(e);
			}

			public void removeUpdate(DocumentEvent e)
			{
				textFieldUpdated(e);
			}

			public void changedUpdate(DocumentEvent e)
			{
				textFieldUpdated(e);
			}

			private void textFieldUpdated(DocumentEvent documentEvent)
			{
				// Update the parameter value
				Document doc = documentEvent.getDocument();
				int docLength = doc.getLength();
				boolean isValid = false;

				try {
					String strValue = doc.getText(0, docLength);
					try {
						minLengthValue = Double.parseDouble(strValue);
						if (minLengthValue >= 0) {
							isValid = true;
						}
					}
					catch (NumberFormatException ignore) {
					}
				}
				catch (BadLocationException e) {
					e.printStackTrace();
				}

				listener.valueChanged();
				listener.validityChanged(isValid);
			}
		});

		panel.add(txt);

		return panel;
	}

	@Override
	public boolean runSelector(IXSubAssembly subAssembly, IXSBOMContext context,
			Set<IXSubAssembly> combinedObjects)
	{
		IXObject harnessObj = subAssembly.getHarnessObject();
		context.logMessage("Invoked Operation: " + getName() + "- harness obj = " + harnessObj);

		//
		// ========> Object type and process filtering steps:
		// - only look at leaf wires
		// i.e. wires not already consumed by another sub-assembly
		if (harnessObj instanceof IXWire) {
			IXWire wire = (IXWire) harnessObj;

			//
			// ========> Harness filtering and combining
			// - conditionned by minimum length parameter
			// - select wire ends and single-crimp terminals
			double length = Double.parseDouble(wire.getAttribute("ModifiedLength"));

			if (length >= minLengthValue) {
				// Strip by combining with the wire ends
				for (IXWireEnd wireEnd : wire.getWireEnds()) {
					combinedObjects.add(context.getOwnerRootSubAssembly(wireEnd));
				}

				// Terminate by combining with the terminals - don't include multi-crimp terminals
//				for (IXTerminal terminal : getWireSingleTerminals(wire)) {
//					combinedObjects.add(context.getOwnerRootSubAssembly(terminal));
//				}

				// Operation fires for this wire - implicitely means the wire is cut
				return true;
			}
		}

		return false;
	}

	/**
	 * Retrieves terminals at the ends of a wire, only if they are single-crimp
	 * @param wire The wire
	 * @return The collection of single-crimp terminals
	 */
    private Collection<IXTerminal> getWireSingleTerminals(IXWire wire)
    {
        Collection<IXTerminal> terms = new ArrayList<IXTerminal>();

		for (IXWireEnd end : wire.getWireEnds()) {
			IXAbstractPin pin = end.getPin();
			if (pin instanceof IXCavity) {
				IXCavity cavity = (IXCavity) pin;
				if (cavity.getConductors().size() == 1) {
					for (IXCavityDetail det : cavity.getCavityDetails()) {
						IXTerminal term = det.getTerminal();
						if (term != null) {
							terms.add(term);
						}
					}
				}
            }
        }

        return terms;
    }

	@Override public Icon getIcon()
	{
		return null;
	}

	@Override public URL getInformationAsHTML()
	{
		return null;
	}

	@Override public String getInformationAsText()
	{
		return null;
	}
}