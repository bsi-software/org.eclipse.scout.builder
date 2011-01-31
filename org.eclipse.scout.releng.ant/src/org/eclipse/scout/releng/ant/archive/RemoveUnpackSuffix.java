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
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.scout.releng.ant.util.FileUtility;

/**
 * <h4>CreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (21.01.2011)
 */
public class RemoveUnpackSuffix extends Task {

  private final ArrayList<FileSet> filesets;
  private File dir;
  private String suffix = "_unpack";

  public RemoveUnpackSuffix() {
    filesets = new ArrayList<FileSet>();
  }

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

  public void addFileset(FileSet fileset) {
    this.filesets.add(fileset);
  }

  /**
   * @return the suffix
   */
  public String getSuffix() {
    return suffix;
  }

  /**
   * @param suffix
   *          the suffix to set
   */
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  @Override
  public void execute() throws BuildException {
    validate();
    setup();
    Pattern pattern = Pattern.compile("^(.*)" + getSuffix() + ".jar$");
    for (FileSet fs : filesets) {
      DirectoryScanner ds = fs.getDirectoryScanner(getProject());
      File inputDir = fs.getDir(getProject());
      for (String fileName : ds.getIncludedFiles()) {
        File inputFile = new File(inputDir, fileName);
        Matcher m = pattern.matcher(inputFile.getAbsolutePath());
        if (m.matches()) {
          log("remove unpack suffix from file '"+inputFile.getName()+"'", Project.MSG_INFO);
          try {
            FileUtility.copy(inputFile, new File(m.group(1) + ".jar"));
            inputFile.delete();
          }
          catch (IOException e) {
            throw new BuildException("could not rename file '" + inputFile + "'.", e);
          }
        }
      }
    }
  }
  
  
  private void validate() throws BuildException{
    if(filesets.isEmpty()){
      if(getDir() == null){
        throw new BuildException("dir or fileset must be set.");
      }else if(!getDir().exists()){
        throw new BuildException("dir '"+getDir()+"' does not exist.");
      }else if(!getDir().canWrite()){
        throw new BuildException("dir '"+getDir()+"' is not accessable.");
      }
    }
  }
  
  protected void setup(){
    if(getDir() != null){
      FileSet set = new FileSet();
      set.setDir(getDir());
      set.createInclude().setName("**/*.jar");
      addFileset(set);
    }
  }

}
