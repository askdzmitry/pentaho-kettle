package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class JobEntryItem extends BaseItem {

  private JobEntryInterface jobEntry;

  private static ObjectId JOB_ID = new StringObjectId( "job_id" );
  private static ObjectId JOB_ENTRY_ID = new StringObjectId("job_entry_id");


  public JobEntryItem( JobEntryInterface jobEntry, String jobEntryId, String name, Path sourceFile ) {
    this.jobEntry = jobEntry;
    this.sourceFile = sourceFile;
    this.id = jobEntryId;
    this.name = name;
  }

  @Override
  public Type getType() {
    return Type.JOB_ENTRY;
  }

  @Override
  public String toXml() {
    String result = "";
    try {
      result = jobEntry.getXML();
    } catch ( Exception e ) {
      throw new RuntimeException( "Cannot save meta to XML for job entry " + jobEntry.toString(), e );
    }
    return result;
  }

  @Override
  public void fromXml( String xml ) {
    String validXml = "<jobentry>" + xml + "</jobentry>";
    try {
      InputStream is = new ByteArrayInputStream( validXml.getBytes() );
      jobEntry.loadXML( XMLHandler.getSubNode( XMLHandler.loadXMLFile( is, null, false, false ), "jobentry" ),
        null, null, null, null );
    } catch ( KettleXMLException e ) {
      throw new RuntimeException( "Cannot read step meta from XML for step " + jobEntry.toString(), e );
    }
  }

  @Override
  public void toRepository( Repository rep ) {
    try {
      jobEntry.saveRep( rep, null, JOB_ID );
    } catch ( KettleException e ) {
      throw new RuntimeException( "Cannot save meta to repository for job entry " + jobEntry.toString(), e );
    }
  }

  @Override
  public void fromRepositiry( Repository rep ) {
    try {
      jobEntry.loadRep( rep, null, jobEntry.getObjectId(), null, null );
    } catch ( KettleException e ) {
      throw new RuntimeException( "Cannot read meta from repository for job entry " + jobEntry.toString(), e );
    }
  }


  @Override
  public Item shallowCopy() {
    return new JobEntryItem( jobEntry, id, name, sourceFile );
  }
}
