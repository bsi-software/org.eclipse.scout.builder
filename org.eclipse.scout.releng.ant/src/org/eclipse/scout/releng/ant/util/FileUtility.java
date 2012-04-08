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
package org.eclipse.scout.releng.ant.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URI;

/**
 * <h4>FileUtility</h4>
 *
 * @author aho
 * @since 1.1.0 (22.01.2011)
 */
public class FileUtility {

  private final static int BUFFER_SIZE = 1024;

  public static void copy(File inputFile, File outputFile) throws IOException {
    if (inputFile.isDirectory()) {
      if (!outputFile.exists()) {
        outputFile.mkdirs();
      }
      for (File f : inputFile.listFiles()) {
        copyToDir(f, outputFile);
      }
    }
    else {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = new FileInputStream(inputFile);
        out = new FileOutputStream(outputFile);
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
          out.write(buffer, 0, read);
        }
      }
      finally {
        if (in != null) {
          try {
            in.close();
          }
          catch (IOException e) {
            // void
          }
        }
        if (out != null) {
          try {
            out.close();
          }
          catch (IOException e) {
            // void
          }

        }

      }
    }
  }

  public static void moveToDir(File input, File inputDir, File outputDir){

  }

  public static void copyToDir(File input, File toDir) throws IOException {
    copyToDir(input, toDir, input.getParentFile().getAbsoluteFile().toURI());
  }

  public static void copyToDir(File input, File toDir, URI relPath) throws IOException {
    if (input.isDirectory()) {
      for (File f : input.listFiles()) {
        copyToDir(f, toDir, relPath);
      }
    }
    else {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = new FileInputStream(input);
        File outFile = new File(toDir.getAbsolutePath() + File.separator + relPath.relativize(input.toURI()).toString());
        if(!outFile.exists()){
          outFile.getParentFile().mkdirs();
        }
        out = new FileOutputStream(outFile);
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
          out.write(buffer, 0, read);
        }
      }
      finally {
        if (in != null) {
          try {
            in.close();
          }
          catch (IOException e) {
            // void
          }
        }
        if (out != null) {
          try {
            out.close();
          }
          catch (IOException e) {
            // void
          }
        }
      }
    }
  }

  public static boolean deleteFile(File file) {
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        boolean success = deleteFile(f);
        if (!success) {
          return false;
        }
      }
    }
    return file.delete();
  }

  /**
   * retrieve content as raw bytes
   */
  public static byte[] getContent(InputStream stream) throws IOException {
    return getContent(stream, true);
  }

  public static byte[] getContent(InputStream stream, boolean autoClose) throws IOException {
    BufferedInputStream in = null;
    try {
      in = new BufferedInputStream(stream);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] b = new byte[10240];
      int len;
      while ((len = in.read(b)) > 0) {
        buffer.write(b, 0, len);
      }
      buffer.close();
      byte[] data = buffer.toByteArray();
      return data;
    }
    finally {
      if (autoClose) {
        if (in != null) {
          in.close();
        }
      }
    }
  }
  public static byte[] getContent(String filename) throws IOException {
    try {
      return getContent(new FileInputStream(filename), true);
    }
    catch (FileNotFoundException e) {
      IOException io = new IOException("filename: " + filename);
      io.initCause(e);
      throw io;
    }
  }

  /**
   * retrieve content as string (correct charcter conversion)
   */
  public static String getContent(Reader stream) throws IOException {
    return getContent(stream, true);
  }

  public static String getContent(Reader stream, boolean autoClose) throws IOException {
    BufferedReader in = null;
    try {
      in = new BufferedReader(stream);
      StringWriter buffer = new StringWriter();
      char[] b = new char[10240];
      int len;
      while ((len = in.read(b)) > 0) {
        buffer.write(b, 0, len);
      }
      buffer.close();
      return buffer.toString();
    }
    finally {
      if (autoClose) {
        if (in != null) {
          in.close();
        }
      }
    }
  }
}
