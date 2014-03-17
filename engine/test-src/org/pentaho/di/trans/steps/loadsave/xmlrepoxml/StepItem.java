package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;

public class StepItem extends BaseItem {

  private StepMetaInterface stepMetaInterface;
  private StepMeta stepMeta;
  private ObjectId objectId;

  public StepItem( StepMetaInterface step, String id, String name, Path sourceFile, ObjectId stepId ) {
    this.stepMetaInterface = step;
    this.sourceFile = sourceFile;
    this.id = id;
    this.objectId = objectId;
    this.name = name;
  }

  public StepItem( StepMeta stepMeta, Path sourceFile) {
    this.stepMeta = stepMeta;
    this.sourceFile = sourceFile;
    this.stepMetaInterface = stepMeta.getStepMetaInterface();
    this.id = stepMeta.getTypeId();
    this.name = stepMeta.getName();
  }

  @Override
  public Type getType() {
    return Type.STEP;
  }

  @Override
  public String toXml() {
    String result = "";
    try {
      result = stepMetaInterface.getXML();
    } catch ( Exception e ) {
      throw new RuntimeException( "Cannot save step meta to XML for step " + stepMetaInterface.toString(), e );
    }
    return result;
  }

  @Override
  public void fromXml( String xml ) {
    String validXml = "<step>" + xml + "</step>";
    try {
      InputStream is = new ByteArrayInputStream( validXml.getBytes() );
      stepMetaInterface.loadXML( XMLHandler.getSubNode( XMLHandler.loadXMLFile( is, null, false, false ), "step" ),
        null, (IMetaStore) null );
    } catch ( KettleXMLException e ) {
      throw new RuntimeException( "Cannot read step meta from XML for step " + stepMetaInterface.toString(), e );
    }
  }

  @Override
  public void toRepository( Repository rep ) {
    try {
      stepMetaInterface.saveRep( rep, null, null, null );
    } catch ( KettleException e ) {
      throw new RuntimeException( "Cannot save step meta to repository for step " + stepMetaInterface.toString(), e );
    }
  }

  @Override
  public void fromRepositiry( Repository rep ) {
    try {
      stepMetaInterface.readRep( rep, (IMetaStore) null, objectId, null );
    } catch ( KettleException e ) {
      throw new RuntimeException( "Cannot read step meta from repository for step " + stepMetaInterface.toString(), e );
    }
  }

  @Override
  public Item shallowCopy() {
    return new StepItem( stepMetaInterface, id, name, sourceFile, objectId );
  }
}
