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
package org.eclipse.scout.releng.ant.pack200;

import java.io.File;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * <h4>AbstractPack</h4>
 *
 * @author jgu
 * @since 1.1.0 (09.04.2012)
 */
public abstract class AbstractPack extends Task {

  private File outputDir;
  protected final ArrayList<FileSet> filesets;
  private File dropinDir;

  public File getDropinDir() {
    return dropinDir;
  }

  public void setDropinDir(File dropinDir) {
    this.dropinDir = dropinDir;
  }

  public AbstractPack() {
    this.filesets = new ArrayList<FileSet>();
  }

  /**
   * @param outputDir
   *          the outputDir to set
   */
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

  protected void validate() throws BuildException {
    if (filesets.size() == 0) {
      throw new BuildException("need to specify a fileset");
    }
  }

  protected boolean dropinExists(File inputFile) {
    File dropinExists = getExistingDropin(inputFile.getName());
    if(dropinExists!=null){
      return true;
    }else{
      return false;
    }
  }

  protected File getExistingDropin(String filename) {
    File exclusionDir = getDropinDir();
    if (exclusionDir != null && exclusionDir.exists() && exclusionDir.isDirectory()) {
      for (File f : exclusionDir.listFiles()) {
        if (f.getName().equals(filename)) {
          return f;
        }
      }
    }
    return null;
  }

}
