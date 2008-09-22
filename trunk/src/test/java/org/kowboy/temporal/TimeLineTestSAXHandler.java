/*
 * Temporal Collections - Hibernate implementation of temporal data patterns.
 * Copyright (C) 2008  Craig McDaniel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.kowboy.temporal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Abstract base class for time line unit tests. Provides a SAX handler
 * for parsing timeline test files.
 * 
 * @version $Rev$ $Date$
 * @author $Author$
 */
public abstract class TimeLineTestSAXHandler 
implements ContentHandler {

    private Locator locator = null;
    private int testStartLine = 0;
    private String testName = null;
    private List<TemporalData> before = null;
    private List<TemporalData> after = null;
    private SimpleTemporalData tempData = null;
    private SimpleTemporalData operation = null;
    
    private StringBuffer buf = null;
    private boolean inBefore = false;
    private boolean inAfter = false;
    
    protected abstract void runTest() throws Exception;
    
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void startDocument() throws SAXException {
        
    }

    public void endDocument() throws SAXException {
        
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    public void endPrefixMapping(String prefix) throws SAXException {
        
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        buf = new StringBuffer();
        if (localName.equals("TimeLineUnitTest")) {
            testStartLine = locator.getLineNumber();
            operation = null;
        } else if (localName.equals("TimeLine")) {
            tempData = null;
        } else if (localName.equals("TimePeriod")) {
            tempData = new SimpleTemporalData();
        } else if (localName.equals("Before")) {
            before = new ArrayList<TemporalData>();
            inBefore = true;
        } else if (localName.equals("After")) {
            after = new ArrayList<TemporalData>();
            inAfter = true;
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("UnitName")) {
            testName = buf.toString();
        } else if (localName.equals("Start")) {
            try {
                tempData.getTimePeriod().setStartDate(buf.toString());
            } catch (ParseException e) {
                throw new SAXException(e);
            }
        } else if (localName.equals("End")) {
            try {
                tempData.getTimePeriod().setEndDate(buf.toString());
            } catch (ParseException e) {
                throw new SAXException(e);
            }
        } else if (localName.equals("Key")) {
            tempData.setData(buf.toString());
        } else if (localName.equals("TimePeriod")) {
            if (inBefore) {
                before.add(tempData);
            } else if (inAfter) {
                after.add(tempData);
            } else {
                operation = tempData;
            }
        } else if (localName.equals("Before")) {
            inBefore = false;
        } else if (localName.equals("After")) {
            inAfter = false;
        } else if (localName.equals("TimeLineUnitTest")) {
            try {
                runTest();
            } catch (Exception e) {
                throw new SAXException(e);
            }
        }
        
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        buf.append(ch, start, length);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    public void processingInstruction(String target, String data) throws SAXException {
        
    }

    public void skippedEntity(String name) throws SAXException {

    }

    public List<TemporalData> getAfter() {
        return after;
    }

    public List<TemporalData> getBefore() {
        return before;
    }

    public SimpleTemporalData getOperation() {
        return operation;
    }

    public String getTestName() {
        return testName;
    }

    public int getTestStartLine() {
        return testStartLine;
    }
    
    
    @SuppressWarnings("unchecked")
	public String toString() {
        String eol = System.getProperty("line.separator");
        StringBuffer buffer = new StringBuffer();
        buffer.append("Test ").append(getTestName()).append(eol);
        buffer.append("Before: ").append(eol);
        Iterator it = getBefore().iterator();
        while (it.hasNext()) {
            buffer.append("  ").append(it.next()).append(eol);
        }
        buffer.append("Operation: ").append(eol).append("  ")
            .append(getOperation()).append(eol);
        buffer.append("After: ").append(eol);
        it = getAfter().iterator();
        while (it.hasNext()) {
            buffer.append("  ").append(it.next()).append(eol);
        }
        return buffer.toString();
    }
}
