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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.scout.releng.ant.util.FileUtility;

/**
 * <h4>CreateArchive</h4>
 * 
 * @author aho
 * @since 1.1.0 (21.01.2011)
 */
public class ExtractArchive extends Task {

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
    Pattern pattern = Pattern.compile("^(.*)" + getSuffix() + ".jar$");
    for (File f : getInputDir().listFiles()) {
      try {
        if (f.isDirectory()) {
          FileUtility.copyToDir(f, getOutputDir());
        }
        else if (f.isFile()) {
          Matcher m = pattern.matcher(f.getName());
          if (m.matches()) {
            if (unpack(f, getOutputDir().getAbsolutePath() + File.separator + m.group(1))) {
              if (outputEqualsInput) {
                FileUtility.deleteFile(f);
              }
            }
          }
          else {
            FileUtility.copyToDir(f, getOutputDir());
          }
        }
      }
      catch (IOException e) {
        throw new BuildException("could not unpack jar file '" + f.getAbsolutePath() + "'.", e);
      }
    }

  }

  private boolean unpack(File jarFile, String destName) throws IOException {
    File dest = new File(destName);
    if (!dest.exists()) {
      dest.mkdirs();
    }
    ZipInputStream jin = null;
    try {
      jin = new ZipInputStream(new FileInputStream(jarFile));
      byte[] buffer = new byte[1024];

      ZipEntry entry = jin.getNextEntry();
      while (entry != null) {
        String fileName = entry.getName();
        if (fileName.charAt(fileName.length() - 1) == '/') {
          fileName = fileName.substring(0, fileName.length() - 1);
        }
        if (fileName.charAt(0) == '/') {
          fileName = fileName.substring(1);
        }
        if (File.separatorChar != '/') {
          fileName = fileName.replace('/', File.separatorChar);
        }
        File file = new File(dest, fileName);
        if (entry.isDirectory()) {
          // make sure the directory exists
          file.mkdirs();
          jin.closeEntry();
        }
        else {
          // make sure the directory exists
          File parent = file.getParentFile();
          if (parent != null && !parent.exists()) {
            parent.mkdirs();
          }

          // dump the file
          OutputStream out = new FileOutputStream(file);
          int len = 0;
          while ((len = jin.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, len);
          }
          out.flush();
          out.close();
          jin.closeEntry();
          file.setLastModified(entry.getTime());
        }
        entry = jin.getNextEntry();

      }
      return true;
    }

    finally {
      try {
        jin.close();
      }
      catch (IOException e) {
        // void
      }
    }
  }

}
