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
package org.eclipse.scout.releng.ant.p2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.util.FileUtility;

/**
 * <h4>TrunkateRepository</h4>
 * 
 * @author aho
 * @since 1.1.0 (29.01.2011)
 */
public class TrunkateRepository extends Task {

  private File repositoryLocation;
  private int keep = 3;

  /**
   * @return the repositoryLocation
   */
  public File getRepositoryLocation() {
    return repositoryLocation;
  }

  /**
   * @param repositoryLocation
   *          the repositoryLocation to set
   */
  public void setRepositoryLocation(File repositoryLocation) {
    this.repositoryLocation = repositoryLocation;
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
    P_ShellScriptFilter filter = new P_ShellScriptFilter();
    getRepositoryLocation().list(filter);
    if (filter.size() > keep) {
      File[] toRemove = filter.getOrderedScripts();
      for (int i = keep; i < toRemove.length; i++) {
        try {
          remove(toRemove[i]);
        }
        catch (IOException e) {
          throw new BuildException("coud not trunkate repository to size '" + getKeep() + "'.", e);
        }
      }
    }
  }

  /**
   * @param file
   */
  private void remove(File file) throws IOException {
    String workingDir = file.getParent();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      Pattern p = Pattern.compile("^rm\\s*\\-rf\\s*(.*)$");
      String line = reader.readLine();
      while(line != null){
        Matcher m = p.matcher(line);
        if(m.matches()){
          FileUtility.deleteFile(new File(workingDir+File.separator+m.group(1)));
        }
        line = reader.readLine();
      }
      FileUtility.deleteFile(file);
    }
    finally {
      if (reader != null) {
        reader.close();
      }
    }
  }

  private void validate() throws BuildException {
    if (getRepositoryLocation() == null) {
      throw new BuildException("parameter repositoryLocation missing.");
    }
  }

  private class P_ShellScriptFilter implements FilenameFilter {
    private TreeMap<String, File> orderedScripts;
    Pattern versionPattern = Pattern.compile("([0-9]{8}\\-[0-9]{4})\\.sh$");

    public P_ShellScriptFilter() {
      this.orderedScripts = new TreeMap<String, File>();
    }

    @Override
    public boolean accept(File dir, String name) {
      Matcher matcher = versionPattern.matcher(name);
      if (matcher.find()) {
        orderedScripts.put(matcher.group(1), new File(dir.getAbsolutePath() + File.separator + name));
      }
      return false;
    }

    public int size() {
      return orderedScripts.size();
    }

    /**
     * @return the orderedScripts
     */
    public File[] getOrderedScripts() {
      int i = orderedScripts.size();
      File[] files = new File[i];
      for(File f : orderedScripts.values()){
        files[--i] = f;
      }
      return files;
    }
  }
}
