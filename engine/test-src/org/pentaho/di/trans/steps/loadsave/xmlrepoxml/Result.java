package org.pentaho.di.trans.steps.loadsave.xmlrepoxml;

import org.custommonkey.xmlunit.Diff;

public class Result {

  private Status status;

  private Item item;

  private String xml1, xml2;

  private Diff diff;

  private Throwable throwable;

  public Status getStatus() {
    return status;
  }

  public void setStatus( Status status ) {
    this.status = status;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable( Throwable throwable ) {
    this.throwable = throwable;
  }

  public Result(Status status, Item item, String xml1, String xml2) {
    this.status = status;
    this.item = item;

    this.xml1 = xml1;
    this.xml2 = xml2;
  }

  public boolean isOk() {
    return diff.similar();
  }

  public Item getItem() {
    return item;
  }

  public void setItem( Item item ) {
    this.item = item;
  }

  public String getXml1() {

    return xml1;
  }

  public void setXml1( String xml1 ) {
    this.xml1 = xml1;
  }

  public String getXml2() {
    return xml2;
  }

  public void setXml2( String xml2 ) {
    this.xml2 = xml2;
  }

  public Diff getDiff() {
    return diff;
  }

  public void setDiff( Diff diff ) {
    this.diff = diff;
  }

  public static enum Status {
    SIMILAR,
    DIFFERENT,
    CANT_LOAD,
  }

}
