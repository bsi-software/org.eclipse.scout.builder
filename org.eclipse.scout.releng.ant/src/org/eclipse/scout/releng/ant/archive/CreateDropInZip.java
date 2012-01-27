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
package org.eclipse.scout.releng.ant.archive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * <h4>CreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (21.01.2011)
 */
public class CreateDropInZip extends Task {

  private final ArrayList<FileSet> filesets;
  private File outputDir;
  private String zipName;
  private String milestone;
  private String versionMajor;
  private String versionMinor;
  private String versionMicro;
  private String version;
  private String timestamp;
  private boolean incubation;

  public CreateDropInZip() {
    filesets = new ArrayList<FileSet>();
  }

  public void setOutputDir(File outputDir) {
    this.outputDir = outputDir;
  }

  /**
   * @return the outputDir
   */
  public File getOutputDir() {
    return outputDir;
  }

  public void addFileset(FileSet set) {
    filesets.add(set);
  }

  /**
   * @return the zipName
   */
  public String getZipName() {
    return zipName;
  }

  /**
   * @param zipName
   *          the zipName to set
   */
  public void setZipName(String zipName) {
    this.zipName = zipName;
  }

  /**
   * @return the milestone
   */
  public String getMilestone() {
    return milestone;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version
   *          the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * @param milestone
   *          the milestone to set
   */
  public void setMilestone(String milestone) {
    this.milestone = milestone;
  }

  /**
   * @return the versionMajor
   */
  public String getVersionMajor() {
    return versionMajor;
  }

  /**
   * @param versionMajor
   *          the versionMajor to set
   */
  public void setVersionMajor(String versionMajor) {
    this.versionMajor = versionMajor;
  }

  /**
   * @return the versionMinor
   */
  public String getVersionMinor() {
    return versionMinor;
  }

  /**
   * @param versionMinor
   *          the versionMinor to set
   */
  public void setVersionMinor(String versionMinor) {
    this.versionMinor = versionMinor;
  }

  /**
   * @return the versionMicro
   */
  public String getVersionMicro() {
    return versionMicro;
  }

  /**
   * @param versionMicro
   *          the versionMicro to set
   */
  public void setVersionMicro(String versionMicro) {
    this.versionMicro = versionMicro;
  }

  /**
   * @return the timestamp
   */
  public String getTimestamp() {
    return timestamp;
  }

  /**
   * @param timestamp
   *          the timestamp to set
   */
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * @param incubation
   *          the incubation to set
   */
  public void setIncubation(boolean incubation) {
    this.incubation = incubation;
  }

  /**
   * @return the incubation
   */
  public boolean isIncubation() {
    return incubation;
  }

  @Override
  public void execute() throws BuildException {
    validate();
    ZipOutputStream zipOut = null;
    try {
      // pattern name_
      String fileName = getZipName();
      if (isIncubation()) {
        fileName += "-Incubation";
      }
      if (getVersion() != null) {
        fileName += "-" + getVersion();
      }
      else {
        if (getVersionMajor() != null) {
          fileName += "-" + getVersionMajor();
          if (getVersionMinor() != null) {
            fileName += "." + getVersionMinor();
            if (getVersionMicro() != null) {
              fileName += "." + getVersionMicro();
            }
          }
        }
      }
      if (getMilestone() != null) {
        fileName += "" + getMilestone();
      }
      if (getTimestamp() != null) {
        fileName += "-" + getTimestamp();
      }
      fileName += ".zip";
      File zipFile = new File(getOutputDir() + File.separator + fileName);
      if (!zipFile.exists()) {
        zipFile.getParentFile().mkdirs();
      }
      zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
      HashMap<String, File> files = new HashMap<String, File>();
      for (FileSet fs : filesets) {
        DirectoryScanner ds = fs.getDirectoryScanner(getProject());
        File rootDir = fs.getDir(getProject());
        for (String name : ds.getIncludedFiles()) {
          files.put(name, new File(rootDir.getAbsoluteFile(), name));
        }
      }
      for (Entry<String, File> e : files.entrySet()) {
        addFile(zipOut, e.getValue(), e.getKey());
      }
    }
    catch (IOException e) {
      throw new BuildException("could not create zip ''.", e);
    }
    finally {
      if (zipOut != null) {
        try {
          zipOut.finish();
          zipOut.close();
        }
        catch (IOException e) {
          // void
        }
      }
    }
  }

  private void addFile(ZipOutputStream zipStream, File fileToAdd, String relativeFileName) throws IOException {
    BufferedInputStream in = null;
    try {
      ZipEntry entry = new ZipEntry(relativeFileName);
      entry.setTime(fileToAdd.lastModified());
      zipStream.putNextEntry(entry);
      in = new BufferedInputStream(new FileInputStream(fileToAdd));
      byte[] buffer = new byte[20480];
      while (true) {
        int count = in.read(buffer);
        if (count == -1) break;
        zipStream.write(buffer, 0, count);
      }
      zipStream.closeEntry();
    }
    finally {
      if (in != null) {
        in.close();
      }
    }
  }

  private void validate() throws BuildException {
    if (filesets.size() == 0) {
      throw new BuildException("need to specify a fileset");
    }
    if (getZipName() == null) {
      throw new BuildException("zipName can not be null");
    }
  }

}
