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
package org.eclipse.scout.releng.ant.sign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.VerifyJar;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.scout.releng.ant.util.FileUtility;

/**
 * <h4>MoveSignedFiles</h4>
 * Moves all files that are correctly signed to the output dir.
 *
 * @author jgu
 * @since 1.1.0 (08.04.2012)
 */
public class MoveSignedFiles extends Task {

  private File outputDir;
  private final ArrayList<FileSet> filesets;

  public MoveSignedFiles() {
    this.filesets = new ArrayList<FileSet>();
  }

  public File getOutputDir() {
    return outputDir;
  }

  public void setOutputDir(File outputDir) {
    this.outputDir = outputDir;
  }

  public void addFileset(FileSet set) {
    filesets.add(set);
  }

  @Override
  public void execute() throws BuildException {
    validate();
    for (FileSet fs : filesets) {
      DirectoryScanner ds = fs.getDirectoryScanner(getProject());
      File inputDir = fs.getDir(getProject());
      File outputDir = getOutputDir();
      if (outputDir == null) {
        outputDir = inputDir;
      }
      for (String fileName : ds.getIncludedFiles()) {
        File inputFile = new File(inputDir, fileName);
        try {
          if (inputFile.isFile() && isSignatureValid(inputFile)) {
            FileUtility.copyToDir(inputFile, getOutputDir(),inputDir.getAbsoluteFile().toURI());
            boolean deleted = inputFile.delete();
            if(!deleted){
              log("Could not delete file: " + inputFile.getAbsolutePath());
            }
          }
        }
        catch (IOException e) {
          throw new BuildException("could not move file '" + inputFile + "'.", e);
        }
      }
    }
  }

  private boolean isSignatureValid(File f){
    System.out.println("testing " + f.getName());
    VerifyJar jarVerifier = new VerifyJar();
    jarVerifier.setJar(f);
    jarVerifier.setProject(getProject());
    try {
      jarVerifier.execute();
      return true;
    }
    catch (BuildException b) {
      if(b.getMessage() !=null && b.getMessage().contains("jarsigner returned: 1")){
        //warning messages
        return true;
      }
      return false;
    }
  }

  private void validate() throws BuildException {
    if (filesets.size() == 0) {
      throw new BuildException("need to specify a fileset");
    }
  }

}
