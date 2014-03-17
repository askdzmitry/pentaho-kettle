package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import java.nio.file.Path;

public abstract class BaseItem implements Item {

  protected String id;

  protected String name;

  protected Path sourceFile;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public Path getSourceFile() {
    return sourceFile;
  }

  public String getName() {
    return name;
  }
}
