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
package org.eclipse.scout.releng.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.util.FileUtility;
import org.eclipse.scout.releng.ant.util.ReverseStringComparator;

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
      int i =0;
      for(List<File> files : filter.getOrderedScripts()){
        if(i >= keep){
          for(File f : files){
            FileUtility.deleteFile(f);
          }
        }
        i++;
      }

    }
  }

  private void validate() throws BuildException {
    if (getRepositoryLocation() == null) {
      throw new BuildException("parameter repositoryLocation missing.");
    }
  }

  private class P_ShellScriptFilter implements FilenameFilter {
    private TreeMap<String, List<File>> orderedScripts;
    Pattern zipPattern = Pattern.compile("^[A-Z]\\-scout\\-([0-9]{1,2})\\.([0-9]{1,2})\\.(([0-9]{1,2}))?([^\\-]*)?\\-([0-9]{8})\\-([0-9]{4})(-Incubation)?\\.zip$");
    Pattern versionPattern = Pattern.compile("^.*([0-9]{12})\\.jar(\\.pack\\.gz)?$");

    public P_ShellScriptFilter() {
      this.orderedScripts = new TreeMap<String, List<File>>(new ReverseStringComparator());
    }

    @Override
    public boolean accept(File dir, String name) {

      File file = new File(dir.getAbsolutePath()+File.separator+name);
      if(file.exists() && file.isDirectory()){
        file.list(P_ShellScriptFilter.this);
      }else{
        Matcher zipMatcher = zipPattern.matcher(name);
        if(zipMatcher.matches()){
          String timeStamp = zipMatcher.group(6)+zipMatcher.group(7);
          addFile(timeStamp, file);
        }else{
          Matcher jarMatcher = versionPattern.matcher(name);
          if(jarMatcher.matches()){
            addFile(jarMatcher.group(1), file);
          }
        }
      }
      return false;
    }
    
    private void addFile(String timeStamp, File file){
      List<File> fileList = orderedScripts.get(timeStamp);
      if(fileList == null){
        fileList = new ArrayList<File>();
        orderedScripts.put(timeStamp, fileList);
      }
      fileList.add(file);
    }

    public int size() {
      return orderedScripts.size();
    }

    /**
     * @return the orderedScripts
     */
    public Collection<List<File>> getOrderedScripts() {
      return orderedScripts.values(); 
    }
  }
}
