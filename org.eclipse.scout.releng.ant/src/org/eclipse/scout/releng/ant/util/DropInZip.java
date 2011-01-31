/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.releng.ant.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;

/**
 * <h4>DropInZip</h4>
 * 
 * @author aho
 * @since 1.1.0 (31.01.2011)
 */
public class DropInZip {
  private Integer versionMajor = 0;
  private Integer versionMinor = 0;
  private Integer versionMicro = 0;
  private File zipFile;
  private Date buildDate;

  public DropInZip(File file, String buildDate, Integer versionMajor, Integer versionMinor) {
    this(file, buildDate, versionMajor, versionMinor, null);
  }
  
  public DropInZip(File file, String buildDate, Integer versionMajor, Integer versionMinor, Integer versionMicro) {
    this.zipFile = file;
    try {
      SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmm");
      this.buildDate = format.parse(buildDate);
    }
    catch (ParseException e) {
      throw new BuildException("could not parse date '"+buildDate+"' into format 'yyyyMMdd-HHmm'.",e);
    }
    this.versionMajor = versionMajor;
    this.versionMinor = versionMinor;
    this.versionMicro = versionMicro;
  }

  /**
   * @return the versionMajor
   */
  public Integer getVersionMajor() {
    return versionMajor;
  }

  /**
   * @param versionMajor
   *          the versionMajor to set
   */
  public void setVersionMajor(Integer versionMajor) {
    this.versionMajor = versionMajor;
  }

  /**
   * @return the versionMinor
   */
  public Integer getVersionMinor() {
    return versionMinor;
  }

  /**
   * @param versionMinor
   *          the versionMinor to set
   */
  public void setVersionMinor(Integer versionMinor) {
    this.versionMinor = versionMinor;
  }

  /**
   * @return the versionMicro
   */
  public Integer getVersionMicro() {
    return versionMicro;
  }

  /**
   * @param versionMicro
   *          the versionMicro to set
   */
  public void setVersionMicro(Integer versionMicro) {
    this.versionMicro = versionMicro;
  }

  /**
   * @return the zipFile
   */
  public File getZipFile() {
    return zipFile;
  }

  /**
   * @param zipFile
   *          the zipFile to set
   */
  public void setZipFile(File zipFile) {
    this.zipFile = zipFile;
  }

  /**
   * @return the buildDate
   */
  public Date getBuildDate() {
    return buildDate;
  }

  /**
   * @param buildDate
   *          the buildDate to set
   */
  public void setBuildDate(Date buildDate) {
    this.buildDate = buildDate;
  }

}
