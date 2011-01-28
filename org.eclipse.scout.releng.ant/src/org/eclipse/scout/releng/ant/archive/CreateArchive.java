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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.FileUtility;

/**
 * <h4>CreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (21.01.2011)
 */
public class CreateArchive extends Task {

  private File inputDir;
  private File outputDir;

  private String suffix = "_unpack";

  /**
   * @param inputDir
   *          the inputDir to set
   */
  public void setInputDir(File inputDir) {
    this.inputDir = inputDir;
  }

  /**
   * @return the inputDir
   */
  public File getInputDir() {
    return inputDir;
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
    if (getInputDir() == null) {
      throw new BuildException("the directory is null. Please set the property 'dir'.");
    }
    if (!getInputDir().exists()) {
      throw new BuildException("the directory '" + getInputDir() + "' does not exist.");
    }
    if (!getInputDir().isDirectory()) {
      throw new BuildException("'" + getInputDir() + "' must be a directory.");
    }
    if (!getInputDir().canWrite()) {
      throw new BuildException("'" + getInputDir() + "' must be accessable to write.");
    }
    if (getOutputDir() == null) {
      setOutputDir(getInputDir());
    }
    if (!getOutputDir().exists()) {
      getOutputDir().mkdirs();
    }
    boolean outputEqualsInput = getInputDir().equals(getOutputDir());
    for (File f : getInputDir().listFiles()) {
      try {
        if (f.isFile()) {
          FileUtility.copyToDir(f, getOutputDir());
        }
        else if (f.isDirectory()) {
          File jarFile = new File(getOutputDir().getAbsolutePath() + File.separator + f.getName() + getSuffix() + ".jar");
          if (archive(f, jarFile)) {
            if (outputEqualsInput) {
              FileUtility.deleteFile(f);
            }
          }
        }
      }
      catch (IOException e) {
        throw new BuildException("could not create jar of '" + f.getAbsolutePath() + "'.", e);
      }
    }
  }

  private boolean archive(File folderToArchive, File jarFile) throws IOException {
    JarOutputStream target = null;
    try {
      FileOutputStream dest = new FileOutputStream(jarFile);
      target = new JarOutputStream(new BufferedOutputStream(dest));
      add(folderToArchive, target, folderToArchive.getAbsoluteFile().toURI());
      return true;
    }
    finally {
      if (target != null) {
        try {
          target.finish();
          target.close();
        }
        catch (IOException e) {
          // void
        }
      }
    }
  }

  private void add(File source, JarOutputStream target, URI relUri) throws IOException {
    BufferedInputStream in = null;
    try {
      if (source.isDirectory()) {
        String name = relUri.relativize(source.toURI()).toString().replace("\\", "/");
        if (!name.isEmpty()) {
          if (!name.endsWith("/")) name += "/";
          JarEntry entry = new JarEntry(relUri.relativize(source.toURI()).toString());
          entry.setTime(source.lastModified());
          target.putNextEntry(entry);
          target.closeEntry();
        }
        for (File nestedFile : source.listFiles())
          add(nestedFile, target, relUri);
        return;
      }

      JarEntry entry = new JarEntry(relUri.relativize(source.toURI()).toString().replace("\\", "/"));
      entry.setTime(source.lastModified());
      target.putNextEntry(entry);
      in = new BufferedInputStream(new FileInputStream(source));

      byte[] buffer = new byte[20480];
      while (true) {
        int count = in.read(buffer);
        if (count == -1) break;
        target.write(buffer, 0, count);
      }
      target.closeEntry();
    }
    finally {
      if (in != null) in.close();
    }
  }

}
