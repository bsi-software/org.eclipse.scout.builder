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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarFile;
import java.util.jar.Pack200.Packer;
import java.util.zip.GZIPOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.scout.releng.ant.util.FileUtility;

/**
 * <h4>Repack</h4>
 *
 * @author aho
 * @since 1.1.0 (26.01.2011)
 */
public class Pack extends AbstractPack {
  private boolean gzip = true;

  /**
   * @param gzip
   *          the gzip to set
   */
  public void setGzip(boolean gzip) {
    this.gzip = gzip;
  }

  /**
   * @return the gzip
   */
  public boolean isGzip() {
    return gzip;
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
          packFile(inputFile, outputDir.getAbsoluteFile() + File.separator + fileName + ".pack");
        }
        catch (IOException e) {
          throw new BuildException("could not repack file '" + inputFile + "'.", e);
        }
      }
    }
  }

  private void packFile(File inputFile, String outputFilename) throws IOException {
    log("pack200: pack file '" + inputFile.getName() + "'", Project.MSG_INFO);
    Packer packer = Pack200Utility.createPacker();
    OutputStream out = null;

    if (isGzip()) {
      outputFilename += ".gz";
    }
    File outputFile = new File(outputFilename);
    if (!outputFile.exists()) {
      outputFile.getParentFile().mkdirs();
    }

    File existingDropin = getExistingDropin(outputFile.getName());
    if (existingDropin!=null) {
      log("pack200: skipping file '" + inputFile.getName() + "'", Project.MSG_INFO);
      FileUtility.copyToDir(existingDropin, outputFile.getParentFile());
    }
    else {
      try {
        out = new FileOutputStream(outputFile);
        if (isGzip()) {
          out = new GZIPOutputStream(out);
        }
        packer.pack(new JarFile(inputFile), out);
      }
      finally {
        if (out != null) {
          out.close();
        }
      }
    }

  }

}
