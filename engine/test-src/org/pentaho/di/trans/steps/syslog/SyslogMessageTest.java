package org.pentaho.di.trans.steps.syslog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.di.trans.steps.rest.RestData;
import org.pentaho.di.trans.steps.rest.RestMeta;
import org.productivity.java.syslog4j.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * User: Dzmitry Stsiapanau Date: 1/23/14 Time: 11:04 AM
 */
public class SyslogMessageTest {
  private class SyslogIFThrowException implements SyslogIF {

    @Override
    public void initialize( String protocol, SyslogConfigIF config ) throws SyslogRuntimeException {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getProtocol() {
      return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SyslogConfigIF getConfig() {
      return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void backLog( int level, String message, Throwable reasonThrowable ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void backLog( int level, String message, String reason ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log( int level, String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void debug( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void info( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void notice( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void warn( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void error( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void critical( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void alert( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void emergency( String message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log( int level, SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void debug( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void info( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void notice( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void warn( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void error( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void critical( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void alert( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void emergency( SyslogMessageIF message ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void flush() throws SyslogRuntimeException {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void shutdown() throws SyslogRuntimeException {
      throw new NullPointerException( "this.socket is null" );
    }

    @Override
    public void setMessageProcessor( SyslogMessageProcessorIF messageProcessor ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SyslogMessageProcessorIF getMessageProcessor() {
      return null; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStructuredMessageProcessor( SyslogMessageProcessorIF messageProcessor ) {
      // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SyslogMessageProcessorIF getStructuredMessageProcessor() {
      return null; // To change body of implemented methods use File | Settings | File Templates.
    }
  }

  private StepMockHelper<SyslogMessageMeta, SyslogMessageData> stepMockHelper;

  @Before
  public void setUp() throws Exception {
    stepMockHelper =
        new StepMockHelper<SyslogMessageMeta, SyslogMessageData>( "SYSLOG_MESSAGE TEST", SyslogMessageMeta.class,
            SyslogMessageData.class );
    when( stepMockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) ).thenReturn(
        stepMockHelper.logChannelInterface );

  }

  @Test
  public void testDispose() throws Exception {
    SyslogMessageData data = new SyslogMessageData();
    data.syslog = new SyslogIFThrowException();
    SyslogMessageMeta meta = new SyslogMessageMeta();
    SyslogMessage syslogMessage =
        new SyslogMessage( stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta,
            stepMockHelper.trans );
    syslogMessage.dispose( meta, data );
  }
}
