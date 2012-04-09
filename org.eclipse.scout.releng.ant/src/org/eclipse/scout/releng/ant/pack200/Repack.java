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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200.Packer;

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
public class Repack extends AbstractPack {

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
          repackFile(inputFile, new File(outputDir.getAbsoluteFile(), fileName));
        }
        catch (IOException e) {
          throw new BuildException("could not repack file '" + inputFile + "'.", e);
        }
      }
    }
  }

  private void repackFile(File inputFile, File outputFile) throws IOException {
    if (!outputFile.exists()) {
      outputFile.getParentFile().mkdirs();
    }
    if (dropinExists(inputFile)) {
      log("pack200: skipping file '" + inputFile.getName() + "'", Project.MSG_INFO);
      FileUtility.copyToDir(inputFile, outputFile.getParentFile());
    }
    else {
      log("pack200: repack file '" + inputFile.getName() + "'", Project.MSG_INFO);
      Packer packer = Pack200Utility.createPacker();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      packer.pack(new JarFile(inputFile), out);
      FileOutputStream fos = new FileOutputStream(outputFile);
      try {
        JarOutputStream jos = new JarOutputStream(fos);
        try {
          Pack200Utility.createUnpacker().unpack(new ByteArrayInputStream(out.toByteArray()), jos);
        }
        finally {
          jos.close();
        }
      }
      finally {
        fos.close();
      }
    }
  }

}
