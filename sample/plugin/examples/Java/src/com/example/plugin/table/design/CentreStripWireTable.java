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

package com.example.plugin.table.design;

import com.mentor.chs.plugin.table.IXHarnessTable;
import com.mentor.chs.plugin.table.IXTableData;
import com.mentor.chs.api.*;

import java.util.Set;

public class CentreStripWireTable implements IXHarnessTable
{
    private IXHarnessDesign design;
    private IXTableData m_xTableData;
    private String[][] data;

    public String getDescription() {
        return "Sample table plug-in for Wires & Centre Strip Info ";
    }

    public String getName() {
        return "Wires Table Plugin with centre strip  info";
    }

    public String getVersion() {
        return "1.0";
    }

    public Class getTableContext() {
        return IXHarnessDesign.class;
    }

    public void initialize(IXObject xObject) {
        try {
            if (xObject instanceof IXHarnessDesign) {
                design = (IXHarnessDesign) xObject;
            }
            m_xTableData = new CentreStripWireTable.XTableData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IXTableData populate(IXObject xObject) {
        return m_xTableData;
    }

    class XTableData implements IXTableData {

        XTableData() 
        {
            Set<IXWire> wires = design.getConnectivity().getWires();           
            data = new String[getRowCount()][getColumnCount()];

            int i = 0;
            for (IXWire wire : wires) 
            {

                if (wire != null) 
                {
                    data[i][0] = wire.getAttribute("Name");
                    data[i][1] = wire.getAttribute("WireColor");
                    data[i][2] = wire.getAttribute("WireCSA");

                    data[i][3] = wire.getAttribute("");
                    data[i][4] = wire.getAttribute("");
                    data[i][5] = wire.getAttribute("");
                    data[i][6] = wire.getAttribute("");

                    int endNo = 0;
                    for (IXWireEnd end : wire.getWireEnds()) 
                    {
                    	if(end.getPin()!=null && end.getPin().getAttribute("Name") != null)
                    	{
                    		String pinName = end.getPin().getAttribute("Name");
                    		String pinListName = end.getPin().getOwner().getAttribute("Name");
                    	

                    		if (endNo == 0) 
                    		{
                    			if (end.getPin().getOwner() instanceof IXSplice)
                    			{
                                    IXSplice splice = (IXSplice) end.getPin().getOwner();
                                   
                                    if (splice.isCenterstrip()) 
                                    {

                                        Set<IXWire> csWires = splice.getCenterstripWires();
                                        for (IXWire csWire : csWires) 
                                        {
                                            pinListName = csWire.getAttribute("Name");
                                        }
                                    } 
                                    else
                                        pinListName = end.getPin().getOwner().getAttribute("Name");
                                } 
                    			else
                                    pinListName = end.getPin().getOwner().getAttribute("Name");
                    			
                    			
                    			data[i][3] = pinListName;
                                data[i][4] = pinName;
                           
                            endNo++;
                    		} 
                    		else 
                    		{
                    			if (end.getPin().getOwner() instanceof IXSplice)
                    			{
                                    IXSplice splice = (IXSplice) end.getPin().getOwner();
                                    
                                    if (splice.isCenterstrip()) 
                                    {

                                        Set<IXWire> csWires = splice.getCenterstripWires();
                                        for (IXWire csWire : csWires) 
                                        {
                                            pinListName = csWire.getAttribute("Name");
                                        }
                                    } 
                                    else
                                        pinListName = end.getPin().getOwner().getAttribute("Name");
                                } 
                    			else
                                    pinListName = end.getPin().getOwner().getAttribute("Name");
                    			
                    			data[i][5] = pinListName;
                                data[i][6] = pinName;
                            
                    		}
                    	}
                    }
                    data[i][7] = wire.getAttribute("OptionExpression");
                }
                i++;
            }
        }

        public int getColumnCount() {
            return 8;
        }

        public int getRowCount() {
            return design.getConnectivity().getWires().size();
        }


        public int getHeaderCount() {
            return 1;
        }

        public Object getCellValueAt(int row, int column) {
            Object obj = "x";
            switch (column) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    obj = data[row][column];
                    break;
            }
            return obj;
        }

        public Object getHeaderValueAt(int rowIndex, int colIndex) {
            Object obj = null;
            switch (colIndex) {
                case 0:
                    obj = "Wire No";
                    break;
                case 1:
                    obj = "Col";
                    break;
                case 2:
                    obj = "Size";
                    break;
                case 3:
                    obj = "From";
                    break;
                case 4:
                    obj = "Cav";
                    break;
                case 5:
                    obj = "To";
                    break;
                case 6:
                    obj = "Cav";
                    break;
                case 7:
                    obj = "Option";
                    break;
            }
            return obj;
        }
    }
}




