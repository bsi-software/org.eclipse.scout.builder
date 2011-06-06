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
package org.eclipse.scout.releng.ant.undo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;

/**
 * <h4>CreateUndoScript</h4>
 * 
 * @author aho
 * @since 1.1.0 (28.01.2011)
 */
public class CreateUndoScript extends Task {

  private final ArrayList<DirSet> filesets;
  private File undoScript;

  public CreateUndoScript() {
    filesets = new ArrayList<DirSet>();
  }

  /**
   * @param undoScript
   *          the undoScript to set
   */
  public void setUndoScript(File undoScript) {
    this.undoScript = undoScript;
  }

  /**
   * @return the undoScript
   */
  public File getUndoScript() {
    return undoScript;
  }

  public void addDirset(DirSet set) {
    filesets.add(set);
  }

  @Override
  public void execute() throws BuildException {
    validate();
    BufferedWriter writer = null;
    try {
      if (!getUndoScript().exists()) {
        getUndoScript().getParentFile().mkdirs();
        getUndoScript().createNewFile();
      }
      writer = new BufferedWriter(new FileWriter(getUndoScript()));
      for (DirSet fs : filesets) {
        DirectoryScanner ds = fs.getDirectoryScanner(getProject());
        File inputDir = fs.getDir(getProject());
        for (String fileName : ds.getIncludedDirectories()) {
          File inputFile = new File(inputDir, fileName);
          if (inputFile.isDirectory() && inputFile.getName().matches("plugins|features")) {
            append(inputFile, writer);
          }
        }
      }
      writer.flush();
    }
    catch (IOException e) {
      throw new BuildException("could not create undo script.", e);
    }
    finally {
      if (writer != null) {
        try {
          writer.close();

        }
        catch (IOException e1) {
          // void
        }
      }

    }
  }

  private void append(File folder, BufferedWriter writer) throws IOException {
    for (File f : folder.listFiles()) {
      writer.append("rm -rf " + folder.getName() + "/" + f.getName());
      writer.newLine();
    }
  }

  private void validate() throws BuildException {
    if(filesets.isEmpty()){
      throw new BuildException("fileset must be specified.");
    }
    
  }
}
