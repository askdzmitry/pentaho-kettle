package org.pentaho.di.core.database;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class DatabaseMetaTest {
  @Test
  public void testGetDatabaseInterfacesMapWontReturnNullIfCalledSimultaneouslyWithClear() throws InterruptedException, ExecutionException {
    final AtomicBoolean done = new AtomicBoolean( false );
    ExecutorService executorService = Executors.newCachedThreadPool();
    executorService.submit( new Runnable() {

      @Override
      public void run() {
        while ( !done.get() ) {
          DatabaseMeta.clearDatabaseInterfacesMap();
        }
      }
    } );
    Future<Exception> getFuture = executorService.submit( new Callable<Exception>() {

      @Override
      public Exception call() throws Exception {
        int i = 0;
        while ( !done.get() ) {
          assertNotNull( "Got null on try: " + i++, DatabaseMeta.getDatabaseInterfacesMap() );
          if ( i > 30000 ) {
            done.set( true );
          }
        }
        return null;
      }
    } );
    getFuture.get();
  }

  @Test
  public void testDatabaseAccessTypeCode() throws Exception {
    String expectedJndi = "JNDI";
    String access = DatabaseMeta.getAccessTypeDesc( DatabaseMeta.getAccessType( expectedJndi ) );
    assertEquals( expectedJndi, access );
  }

  @Test
  public void testCheckParametersSAPR3DatabaseMeta() {
    testCheckParams( Mockito.mock( SAPDBDatabaseMeta.class ) );
  }

  @Test
  public void testCheckParametersGenericDatabaseMeta() {
    testCheckParams( Mockito.mock( GenericDatabaseMeta.class ) );
  }

  private void testCheckParams( DatabaseInterface databaseInterface ) {
    DatabaseMeta dbMeta = new DatabaseMeta();
    dbMeta.setDatabaseInterface( databaseInterface );

    String[] params = dbMeta.checkParameters();
    String remark = "Please specify the name of the database";
    assertFalse( Arrays.asList( params ).contains( remark ) );
  }

}
