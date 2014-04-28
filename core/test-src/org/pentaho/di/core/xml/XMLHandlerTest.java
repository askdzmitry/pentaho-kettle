/*
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
 *
 * **************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pentaho.di.core.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

public class XMLHandlerTest {

  @Test
  public void testStripDuplicatedWhitespaces() throws Exception {

    String whitespacesStr = "1 \t\t 2 \n\n 3 \n 4  5";
    String xml = "<xml><node>" + whitespacesStr
      + "</node><cdata-node><![CDATA[" + whitespacesStr
      + "]]></cdata-node></xml>";

    Document doc = XMLHandler.loadXMLString( xml );
    XMLHandler.stripDuplicatedWhitespaces( doc );

    Assert.assertEquals( "1 2 3 4 5", XMLHandler.getTagValue( doc, "xml", "node" ) );
    Assert.assertEquals( whitespacesStr, XMLHandler.getTagValue( doc, "xml", "cdata-node" ) );

  }
}
