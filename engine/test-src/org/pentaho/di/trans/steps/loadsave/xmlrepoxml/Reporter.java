package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import difflib.DiffUtils;
import difflib.Patch;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.apache.commons.io.FileUtils;
import org.pentaho.di.core.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Reporter {

  private static int ID = 0;

  private String reportDir;

  public Reporter( String reportDir ) {
    this.reportDir = reportDir;
  }

  public void generate(List<Result> results) throws IOException  {
     generateReport( results );
  }

  public void generateReport( List<Result> results ) throws IOException {
    // failed first
    Collections.sort( results, new FailedItemFirstComparator() );

    File rd = new File(reportDir + "/results");
    FileUtils.deleteDirectory( rd );

    // generate view objects
    List<ResultView> views = createResultsView(results);

    StringTemplateGroup stGroup = new StringTemplateGroup("group", "d:\\Work\\Pentaho\\Repos\\F\\pentaho-kettle\\engine\\test-src\\org\\pentaho\\di" +
      "\\trans\\steps\\loadsave\\xmlrepoxml\\");
    StringTemplate template = stGroup.getInstanceOf( "main" );
    template.setAttribute( "views", views );
    String html = template.toString();

    // to file
    File file = new File(reportDir + "tmp.html");
    FileOutputStream fos = new FileOutputStream( file );
    PrintWriter writer = new PrintWriter( fos );
    writer.print( html );
    writer.flush();
    writer.close();

  }

  private void createFileWithDirs(File file) throws IOException {
    Path path = file.toPath();
    Files.createDirectories( path.getParent() );
    Files.createFile( path );
  }

  private String generateSideBySideDiff( Result res ) throws IOException  {

    String report = createDiffUtilReport(res);

    // write to file
    File file = new File(reportDir + "/results/" + ID++ + ".html" );
    createFileWithDirs(file);

    FileOutputStream fos = new FileOutputStream( file );
    PrintWriter writer = new PrintWriter( fos );
    writer.print( report.toString() );
    writer.flush();
    writer.close();
    return file.getAbsolutePath();
  }


  private List<ResultView> createResultsView(List<Result> results) throws IOException {

    List<ResultView>  resultsView = new ArrayList<ResultView>();
    for (Result res : results) {
      String singleDiff = generateSideBySideDiff( res );
      ResultView resultView = new ResultView();
      resultView.setResult( res );
      resultView.setComparisonFile( singleDiff );
      resultsView.add( resultView );
    }

    return resultsView;

  }


  private String createDiffUtilReport(Result result) {

    String xml1 = result.getXml1();
    String xml2 = result.getXml2();

    if (Const.isEmpty( xml1 ) || Const.isEmpty( xml2 )) {
      return "";
    }

    // split line to pass to diff-util
    List<String> lines1 = Arrays.asList( result.getXml1().split( System.lineSeparator() ));
    List<String> lines2 = Arrays.asList( result.getXml2().split( System.lineSeparator() ));

    // calculate the difference
    Patch patch = DiffUtils.diff( lines1, lines2 );

    String desc = "Type:" + result.getItem().getType() + " | ID: " + result.getItem().getId() + " | Name:" + result.getItem().getName();
    DiffUtilSideBySideReport report = new DiffUtilSideBySideReport( result.getItem().getSourceFile().toString(),
      desc, "original xml", "after writing to repository", patch, lines1, lines2 );

    return report.generate();
  }

  private String htmlReport(List<String> lines) {

    StringBuilder res = new StringBuilder(  );

    res.append( "<html><head></head><body>" );

    for (String line : lines) {
      line = Const.escapeHtml( line );
      if (line.startsWith( "-" )){
        res.append( "<font color=\"red\">" ).append( line ).append( "</font>" );
      } else if (line.startsWith( "+" ) ) {
        res.append( "<font color=\"green\">" ).append( line ).append( "</font>" );
      }
      res.append( "<br/>" );
    }

    res.append( "</body></html>" );

    return res.toString();

  }




//  public class FileFirstComparator implements Comparator<XmlRepoRoundTripTester.Result> {
//
//    @Override
//    public int compare( XmlRepoRoundTripTester.Result o1, XmlRepoRoundTripTester.Result o2 ) {
//      if (o1 == o2) {
//        return 0;
//      }
//      int result = o1.getTransName().compareTo( o2.getTransName() );
//      if (result == 0) {
//        result = o1.getStepName().compareTo( o1.getStepName() );
//      }
//      return result;
//    }
//  }

  public class FailedItemFirstComparator implements Comparator<Result> {

    @Override
    public int compare(Result o1, Result o2 ) {
      if (o1 == o2) {
        return 0;
      }

      int result = 0;
      if (o1.isOk() != o2.isOk()) {
        if (o1.isOk() == false) {
          result = -1;
        } else {
          result = 1;
        }
        return result;
      }

      Item i1 = o1.getItem();
      Item i2 = o2.getItem();

      if (i1 == i2) {
        return 0;
      }

      result = i1.getType().compareTo( i2.getType() );
      if (result == 0) {
        result = i1.getId().compareTo( i2.getId() );
        if (result == 0) {
          result = i1.getSourceFile().compareTo( i2.getSourceFile() );
        }
      }

      return result;
    }
  }

  public class ResultView {

    private String comparisonFile;
    private Result result;

    public String getComparisonFile() {
      return comparisonFile;
    }

    public void setComparisonFile( String comparisonFile ) {
      this.comparisonFile = comparisonFile;
    }

    public Result getResult() {
      return result;
    }

    public void setResult( Result result ) {
      this.result = result;
    }
  }



}
