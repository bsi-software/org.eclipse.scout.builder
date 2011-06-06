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
package org.eclipse.scout.releng.ant.incubation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.omg.CORBA.StringHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * <h4>MarkIncubation</h4>
 * 
 * @author aho
 * @since 1.1.0 (28.01.2011)
 */
public class MarkIncubation extends Task {
  public static final String INCUBATION_APPENDIX = " (Incubation)";
  private final ArrayList<FileSet> filesets;

  public MarkIncubation() {
    this.filesets = new ArrayList<FileSet>();
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
      for (String fileName : ds.getIncludedFiles()) {
        File inputFile = new File(inputDir, fileName);
        if (inputFile.isFile() && fileName.endsWith("jar")) {
          try {
            processJarFile(inputFile);
          }
          catch (Exception e) {
            throw new BuildException("could not repack file '" + inputFile + "'.", e);
          }
        }
      }
    }

  }

  protected void processJarFile(File file) throws Exception {
    log("mark file '"+file.getName()+"' as incubation.");
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    JarOutputStream out = new JarOutputStream(byteStream);
    JarFile jarFile = null;
    HashMap<String, ByteArrayOutputStream> newEntries = new HashMap<String, ByteArrayOutputStream>();
    try {
      jarFile = new JarFile(file);
      JarEntry featureXml = jarFile.getJarEntry("feature.xml");
      if (featureXml != null) {
        StringHolder labelKeyHolder = new StringHolder();
        ByteArrayOutputStream featureOutputStream = processFeatureXml(jarFile.getInputStream(featureXml), labelKeyHolder);
        if (labelKeyHolder.value != null) {
          JarEntry propertyEntry = jarFile.getJarEntry("feature.properties");
          if (propertyEntry != null) {
            newEntries.put(propertyEntry.getName(), processFeatureProperties(jarFile.getInputStream(propertyEntry), labelKeyHolder.value));
          }
        }
        else {
          newEntries.put(featureXml.getName(), featureOutputStream);
        }
      }
      JarEntry manifestEntry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
      if (manifestEntry != null) {
        StringHolder labelKeyHolder = new StringHolder();
        ByteArrayOutputStream manifestOutputStream = processManifest(jarFile.getInputStream(manifestEntry), labelKeyHolder);
        if (labelKeyHolder.value != null) {
          JarEntry propertyEntry = jarFile.getJarEntry("plugin.properties");
          if (propertyEntry != null) {
            newEntries.put(propertyEntry.getName(), processPluginProperties(jarFile.getInputStream(propertyEntry), labelKeyHolder.value));
          }
        }
        else {
          newEntries.put(manifestEntry.getName(), manifestOutputStream);
        }
      }
      // rewrite jar
      if (!newEntries.isEmpty()) {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          JarEntry entry = entries.nextElement();
          JarEntry newEntry = null;
          ByteArrayOutputStream entryStream = null;
          if (newEntries.containsKey(entry.getName())) {
            entryStream = newEntries.remove(entry.getName());
            newEntry = new JarEntry(entry.getName());
            newEntry.setTime(entry.getTime());
          }
          else {
            entryStream = processAnyFile(jarFile.getInputStream(entry));
            newEntry = new JarEntry(entry);
          }
          out.putNextEntry(newEntry);
          out.write(entryStream.toByteArray());
          out.closeEntry();

        }
        out.finish();
        out.flush();
      }

    }
    finally {
      if (jarFile != null) {
        jarFile.close();
      }
    }
    FileOutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(file);
      outputStream.write(byteStream.toByteArray());
    }
    finally {
      if (outputStream != null) {
        outputStream.flush();
        outputStream.close();
      }
    }
  }

  private ByteArrayOutputStream processFeatureXml(InputStream fileStream, StringHolder labelKeyHolder) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setExpandEntityReferences(false);
    Document doc = factory.newDocumentBuilder().parse(fileStream);
    NodeList nodeList = doc.getElementsByTagName("feature");
    if (nodeList.getLength() == 1) {
      Element originalFeatureElement = (Element) nodeList.item(0);
      String label = originalFeatureElement.getAttribute("label");
      if (label.startsWith("%")) {
        labelKeyHolder.value = label.substring(1);
      }
      else {
        originalFeatureElement.setAttribute("label", label + INCUBATION_APPENDIX);
      }
    }
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    DOMSource source = new DOMSource(doc);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamResult result = new StreamResult(outputStream);
    transformer.transform(source, result);
    return outputStream;
  }

  private ByteArrayOutputStream processFeatureProperties(InputStream fileStream, String labelKey) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
    try {
      Pattern pattern = Pattern.compile("^(" + labelKey + "\\=\\s*)(.*)$");
      BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
      String line = reader.readLine();
      while (line != null) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
          if (!m.group(2).startsWith("%")) {
            line = m.group(1) + m.group(2) + INCUBATION_APPENDIX;
          }
        }
        writer.write(line);
        writer.newLine();
        line = reader.readLine();
      }
      writer.flush();
    }
    finally {
      writer.close();
    }
    return out;
  }

  private ByteArrayOutputStream processManifest(InputStream manifest, StringHolder nameKey) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
    try {
      Pattern pattern = Pattern.compile("^(Bundle-Name\\:\\s*)(.*)$");
      BufferedReader reader = new BufferedReader(new InputStreamReader(manifest));
      String line = reader.readLine();
      while (line != null) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
          if (m.group(2).startsWith("%")) {
            nameKey.value = m.group(2).substring(1);
          }
          else {
            line = m.group(1) + m.group(2) + INCUBATION_APPENDIX;
          }
        }
        writer.write(line);
        writer.newLine();
        line = reader.readLine();
      }
      writer.flush();
    }
    finally {
      writer.close();
    }
    return out;
  }

  private ByteArrayOutputStream processPluginProperties(InputStream fileStream, String nameKey) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
    try {
      Pattern pattern = Pattern.compile("^(" + nameKey + "\\=\\s*)(.*)$");
      BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
      String line = reader.readLine();
      while (line != null) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
          if (!m.group(2).startsWith("%")) {
            line = m.group(1) + m.group(2) + INCUBATION_APPENDIX;
          }
        }
        writer.write(line);
        writer.newLine();
        line = reader.readLine();
      }
      writer.flush();
    }
    finally {
      writer.close();
    }
    return out;
  }

  private ByteArrayOutputStream processAnyFile(InputStream inputStream) throws IOException {
    ByteArrayOutputStream out = null;
    out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int read;
    while ((read = inputStream.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }
    return out;
  }

  private void validate() throws BuildException {
    if (filesets.isEmpty()) {
      throw new BuildException("fileset can not be null.");
    }
  }

}
