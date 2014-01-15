package org.pentaho.di.repository;

import java.util.List;

import org.pentaho.di.core.ProgressMonitorListener;
import org.pentaho.di.core.exception.KettleException;

public interface IRepositoryExporterFeedback extends IRepositoryExporter {

  public List<ExportFeedback> exportAllObjectsWithFeedback( ProgressMonitorListener monitor, String xmlFilename,
      RepositoryDirectoryInterface root, String exportType ) throws KettleException;

  public boolean isRulesViolation();
}
