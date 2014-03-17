package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.MemoryRepository;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public class Tester {

  private String dir;

  private List<Item> items;

  private int loadedJobTransCount;

  private Map<String, Throwable> fileLoadErrors = new HashMap<String, Throwable>();

  public Tester( String dir ) {
    this.dir = dir;
    items = new ArrayList<Item>();
    try {
      KettleEnvironment.init();
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }


  private void loadFiles() throws IOException {
    fileLoadErrors.clear();
    loadedJobTransCount = 0;
    Path path = FileSystems.getDefault().getPath( dir );
    Files.walkFileTree( path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile( Path file, BasicFileAttributes attrs )
        throws IOException {
        try {
          if ( isFileTrans( file ) ) {
            loadTrans( file );
          } else if ( isFileJob( file ) ) {
            loadJob( file );
          }
          loadedJobTransCount++;
        } catch ( Throwable e ) {
          fileLoadErrors.put( file.toString(), e );
        }
        return FileVisitResult.CONTINUE;
      }
    } );
  }

  private boolean isFileTrans( Path file ) {
    return file.toString().toLowerCase().endsWith( ".ktr" );
  }

  private boolean isFileJob( Path file ) {
    return file.toString().toLowerCase().endsWith( ".kjb" );
  }

  private void loadTrans( Path file ) throws KettleXMLException, KettleMissingPluginsException {
    TransMeta trans = new TransMeta( file.toString() );
    for ( StepMeta stepMeta : trans.getSteps() ) {
      Item step = new StepItem( stepMeta.getStepMetaInterface(), stepMeta.getTypeId(), stepMeta.getName(), file, stepMeta.getObjectId() );
      items.add( step );
    }
  }

  private void loadJob( Path file ) throws KettleXMLException {
    JobMeta job = new JobMeta( file.toString(), null );
    for ( JobEntryCopy jobEntryCopy : job.getJobCopies() ) {
      Item item = new JobEntryItem( jobEntryCopy.getEntry(), jobEntryCopy.getEntry().getPluginId(), jobEntryCopy.getName(), file );
      items.add( item );
    }
  }


  /**
   * 1) Get xml representation of step/job entry meta interface
   * 2) Persist it in mock repository
   * 3) Read from mock repository
   * 4) Get xml representation again and compare with #1
   *
   * @return
   */
  public List<Result> testXmlRepoXmlTrip() {

    List<Result> results = new ArrayList<Result>(  );
    try {
      loadFiles();
      int i = 0;
      for ( Item item : items ) {
        Result res = null;
        try {
          System.out.println(i++);
          Repository rep = new MemoryRepository();

          // roundtrip
          String xml1 = item.toXml();
          item.toRepository( rep );
          Item item2 = item.shallowCopy();
          item2.fromRepositiry( rep );
          String xml2 = item.toXml();

          // Add a root element to make documents well-formed
          xml1 = "<root>\n" + xml1 + "</root>";
          xml2 = "<root>\n" + xml2 + "</root>";

          // find diff
          DetailedDiff diff = new DetailedDiff( new Diff( xml1, xml2 ) );

          // result
          Result.Status status = Result.Status.DIFFERENT;
          if (diff.similar()) {
            status = Result.Status.SIMILAR;
          }
          res = new Result( status, item, xml1, xml2 );
          res.setDiff( diff );
          results.add( res );
        } catch ( Exception e ) {
          res = new Result( Result.Status.CANT_LOAD, item, "", "" );
          res.setThrowable( e );
          e.printStackTrace(); // TEST
        } finally {
          results.add( res );
        }
      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }
    return results;
  }


  public static void main(String[] args) {
    Tester tester = new Tester("d:\\Work\\Pentaho\\Repos\\F\\pentaho-kettle\\assembly\\package-res\\samples\\");
    Reporter reporter = new Reporter("D:\\temp\\");
    List<Result> results = tester.testXmlRepoXmlTrip();

    int ok = 0;
    for (int i = 0; i < results.size(); i++) {
      if ( results.get( i ).isOk() ) {
        ok++;
      }
    }
    System.out.println("OK - " + ok);
    System.out.println("FAIL - " + (results.size() - ok));

    try {
      reporter.generate( results );
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  // -----------------------

  public void appendFailedJobsTransHtml( StringBuilder sb ) {

    sb.append( "<table border = \"1\">" );
    for (Map.Entry<String, Throwable> entry : fileLoadErrors.entrySet()) {
      sb.append( "<tr><td>" ).append( entry.getKey() ).append( "</td><td>" )
        .append( entry.getValue().toString() ).append(  )
    }


  }

  private void appendStackTraceHtml(StringBuilder sb, Throwable t) {

    t.getStackTrace();

    StringWriter stringWriter = new StringWriter( );
    t.printStackTrace( stringWriter );
    t.pri

  }


}
