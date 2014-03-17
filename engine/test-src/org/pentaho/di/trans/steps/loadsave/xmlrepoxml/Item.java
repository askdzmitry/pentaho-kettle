package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import org.pentaho.di.repository.Repository;

import java.nio.file.Path;

public interface Item {

  Path getSourceFile();

  Type getType();

  String getId();

  String getName();

  String toXml();

  void fromXml(String xml);

  void toRepository(Repository rep);

  void fromRepositiry(Repository rep);

  Item shallowCopy();

  public static enum Type {
    STEP,
    JOB_ENTRY
  }

}
