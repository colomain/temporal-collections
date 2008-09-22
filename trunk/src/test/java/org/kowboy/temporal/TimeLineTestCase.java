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

import java.util.Iterator;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import junit.framework.TestCase;

public abstract class TimeLineTestCase extends TestCase {

    public TimeLineTestCase() {
        this("TimeLine Test");
    }

    public TimeLineTestCase(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
	protected static void checkTimeLine(String error, 
            List<TemporalData> fromFile, 
            TimeLine line) {
        Iterator lineIt = line.iterator();
        Iterator fileIt = fromFile.iterator();
        
        int elementCount = 0;
        while (lineIt.hasNext() && fileIt.hasNext()) {
            String message = error + " Record index " + elementCount + ":";
            assertEquals(message, fileIt.next(), lineIt.next());
            elementCount++;
        }
        if (fileIt.hasNext()) {
            fail(error + " Expected another element at index " + elementCount);
        } else if (lineIt.hasNext()) {
            fail(error + " No elements expected after index " + (elementCount - 1));
        }
    }
    
    /**
     * This test reads test data from an input file, runs the test with
     * that data, and checks the results against expected output as defined
     * in the data file.
     * 
     * @throws Exception
     */
    public void testSampleData() throws Exception {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(new TimeLineTestSAXHandler() {
            @SuppressWarnings("unchecked")
			@Override
            protected void runTest() throws Exception {
                String error = "Test " + getTestName() + ", line " 
                    + getTestStartLine() + " failed.";
                TimeLine line = getTimeLineFactory().createTimeLine();
                line.addAll(getBefore());
                checkTimeLine(error, getBefore(), line);
                line.add(getOperation());
                checkTimeLine(error, getAfter(), line);
            }
        });
        InputSource is = new InputSource(getClass()
        		.getResourceAsStream(getTestDataFileName()));
        reader.parse(is);
    }
    
    protected abstract TimeLineFactory getTimeLineFactory();
    
    protected abstract String getTestDataFileName();
}
