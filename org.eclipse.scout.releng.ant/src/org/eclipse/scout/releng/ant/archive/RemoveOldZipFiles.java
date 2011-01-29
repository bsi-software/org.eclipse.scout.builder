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

import java.io.File;
import java.io.FilenameFilter;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.FileUtility;

/**
 * <h4>TrunkateRepository</h4>
 * 
 * @author aho
 * @since 1.1.0 (29.01.2011)
 */
public class RemoveOldZipFiles extends Task {

  private File dir;
  private int keep = 3;

  /**
   * @return the dir
   */
  public File getDir() {
    return dir;
  }

  /**
   * @param dir
   *          the dir to set
   */
  public void setDir(File dir) {
    this.dir = dir;
  }

  /**
   * @return the keep
   */
  public int getKeep() {
    return keep;
  }

  /**
   * @param keep
   *          the keep to set
   */
  public void setKeep(int keep) {
    this.keep = keep;
  }
  @Override
  public void execute() throws BuildException {
    validate();
    P_ZipFileFilter filter = new P_ZipFileFilter();
    getDir().list(filter);
    if (filter.size() > keep) {
      File[] toRemove = filter.getOrderedZipFiles();
      for (int i = keep; i < toRemove.length; i++) {
        FileUtility.deleteFile(toRemove[i]);
      }
    }
  }


  private void validate() throws BuildException {
    if (getDir() == null) {
      throw new BuildException("parameter dir missing.");
    }
  }

  private class P_ZipFileFilter implements FilenameFilter {
    private TreeMap<String, File> orderedZipFiles;
    Pattern versionPattern = Pattern.compile("([0-9]{8}\\-[0-9]{4})(-Incubation)?\\.zip$");

    public P_ZipFileFilter() {
      this.orderedZipFiles = new TreeMap<String, File>();
    }

    @Override
    public boolean accept(File dir, String name) {
      Matcher matcher = versionPattern.matcher(name);
      if (matcher.find()) {
        orderedZipFiles.put(matcher.group(1), new File(dir.getAbsolutePath() + File.separator + name));
      }
      return false;
    }

    public int size() {
      return orderedZipFiles.size();
    }

    /**
     * @return the orderedScripts
     */
    public File[] getOrderedZipFiles() {
      int i = orderedZipFiles.size();
      File[] files = new File[i];
      for (File f : orderedZipFiles.values()) {
        files[--i] = f;
      }
      return files;
    }
  }
}
