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

package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;


import difflib.Delta;
import difflib.Patch;
import org.pentaho.di.core.Const;

import java.util.ArrayList;
import java.util.List;

public class DiffUtilSideBySideReport {

  private String DELETE_COLOR = "#FF0000";
  private String INSERT_COLOR = "#00FF00";
  private String CHANGE_COLOR = "#FFBF00";

  private String header;
  private String description;
  private String originalTextCaption;
  private String revisedTextCaption;
  private List<String> formattedOriginalLines;
  private List<String> formattedRevisedLines;
  private Patch patch;
  private List<String> originalLines;
  private List<String> revisedLines;
  private List<int[]> originalNewLines;  // List of pairs (start, end) - positions of empty line block which will be added to corresponding list of lines
  private List<int[]> revisedNewLines;   // List of pairs (start, end) - positions of empty line block which will be added to corresponding list of lines

  public DiffUtilSideBySideReport( String header, String description, String originalTextCaption, String revisedTextCaption,
                                   Patch patch, List<String> originalLines,
                                   List<String> revisedLines ) {
    this.header = header;
    this.description = description;
    this.originalTextCaption = originalTextCaption;
    this.revisedTextCaption = revisedTextCaption;
    this.patch = patch;
    this.originalLines = originalLines;
    this.revisedLines = revisedLines;
    originalNewLines = new ArrayList<int[]>();
    revisedNewLines = new ArrayList<int[]>();
  }

  public String generate() {

    StringBuilder sb = new StringBuilder(  );
    appendHeader( sb );
    appendBody( sb );
    appendFooter( sb );

    return sb.toString();
  }

  private void formatLines( ) {

    List<Delta> deltas = patch.getDeltas();

    formattedOriginalLines = new ArrayList<String>(originalLines);
    formattedRevisedLines = new ArrayList<String>(revisedLines);

    // escape lines
    for (int i = 0 ; i < formattedOriginalLines.size(); i++) {
      String line = formattedOriginalLines.get( i );
      formattedOriginalLines.set( i, Const.escapeHtml( line ) );
    }
    for (int i = 0 ; i < formattedRevisedLines.size(); i++) {
      String line = formattedRevisedLines.get( i );
      formattedRevisedLines.set( i, Const.escapeHtml( line ) );
    }

    for (Delta d : deltas) {
      switch( d.getType() ) {
        case DELETE:
          for (int i = d.getOriginal().getPosition() ; i <= d.getOriginal().last(); i++) {
            String line = formattedOriginalLines.get(i);
            formattedOriginalLines.set( i, addColor( line, DELETE_COLOR ) );
          }
          // lines are removed in revised list. For pretty formatting, save it positions to insert empty further
          revisedNewLines.add(new int[]{d.getOriginal().getPosition(), d.getOriginal().last()});
          break;
        case INSERT:
          for (int i = d.getRevised().getPosition() ; i <= d.getRevised().last(); i++) {
            String line = formattedRevisedLines.get(i);
            formattedRevisedLines.set( i, addColor( line, INSERT_COLOR ) );
          }
          // lines are added to revised list. For pretty formatting, save it positions to further insert empty lines to the other list
          originalNewLines.add(new int[]{d.getRevised().getPosition(), d.getRevised().last()});
          break;
        case CHANGE:
          for (int i = d.getRevised().getPosition() ; i <= d.getRevised().last(); i++) {
            String line = formattedRevisedLines.get(i);
            formattedRevisedLines.set( i, addColor( line, CHANGE_COLOR ) );
          }
          for (int i = d.getOriginal().getPosition() ; i <= d.getOriginal().last(); i++) {
            String line = formattedOriginalLines.get(i);
            formattedOriginalLines.set( i, addColor( line, CHANGE_COLOR ) );
          }
          break;
      }
    }

  }

  private List<String> insertNewLines(List<String> linesList, List<int[]> newLinesList) {
    // TODO sort it
    // for now, say, newLinesList is sorted
    int offset = 0;
    List<String> result = new ArrayList<String>(  );
    int startPos = 0;
    for (int[] pair : newLinesList) {
      int size = pair[1] - pair[0] + 1;
      // add all lines before next empty line position
      for (int i = startPos; i < pair[0]; i++) {
        result.add(linesList.get(i));
      }
      // add empty lines
      for (int i = 0; i < size; i++) {
        result.add( "" );
      }
      offset += size;
      startPos = pair[0];
    }
    // add the rest of lines
    for (int i = startPos; i < linesList.size(); i++) {
      result.add(linesList.get(i));
    }
    return result;

  }

  private StringBuilder appendBody(StringBuilder sb) {

    formatLines();

    // insert new lines instead of removed/added lines for pretty formatting
    formattedOriginalLines = insertNewLines( formattedOriginalLines, originalNewLines );
    formattedRevisedLines = insertNewLines( formattedRevisedLines, revisedNewLines );

    // add original lines
    sb.append( "<tr><td valign=\"top\">" );
    for (String line : formattedOriginalLines) {
      sb.append( line ).append( "<br/>" );
    }

    // add revised lines
    sb.append( "</td><td valign=\"top\">" );
    for (String line : formattedRevisedLines) {
      sb.append( line ).append( "<br/>" );
    }
    sb.append( "</td></tr>" );

    return sb;

  }

  private String addColor(String line, String color) {
    return "<font color = \"" + color + "\">" + line + "</font>" ;
  }

  private StringBuilder appendHeader(StringBuilder sb) {
    sb.append( "<html><head><title>" ).append( header ).append( "</title></head><body>" )
      .append( "<h2>" ).append( header ).append( "</h2>" )
      .append( "<h3>" ).append( description ).append( "</h3>" )
      .append( "<table border = \"1\">" )
      .append( "<tr><th>" ).append( originalTextCaption ).append( "</th><th>" )
      .append( revisedTextCaption ).append( "</th>" ) ;
    return sb;
  }

  private StringBuilder appendFooter(StringBuilder sb) {
    sb.append( "</table></body></html>" );
    return sb;
  }

}
