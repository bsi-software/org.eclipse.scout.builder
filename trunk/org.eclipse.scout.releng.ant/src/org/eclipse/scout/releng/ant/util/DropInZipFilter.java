package org.eclipse.scout.releng.ant.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropInZipFilter implements FilenameFilter {
  private TreeMap<String, DropInZip> orderedZipFiles;
  // N-scout-3.7.0M5-20115630-1256-Incubation.zip
  Pattern versionPattern = Pattern.compile("[A-Z]\\-scout\\-([0-9]{1,2})\\.([0-9]{1,2})\\.(([0-9]{1,2}))?([0-9A-Za-z]*)?\\-([0-9]{8}\\-[0-9]{4})(-Incubation)?\\.zip$");

  public DropInZipFilter() {
    this.orderedZipFiles = new TreeMap<String, DropInZip>(new ReverseStringComparator());
  }

  @Override
  public boolean accept(File dir, String name) {
    Matcher matcher = versionPattern.matcher(name);
    if (matcher.matches()) {
      DropInZip zip = new DropInZip(new File(dir.getAbsolutePath() + File.separator + name), matcher.group(6), new Integer(matcher.group(1)), new Integer(matcher.group(2)), new Integer(matcher.group(3)));
      orderedZipFiles.put(matcher.group(6), zip);
    }
    return false;
  }

  public int size() {
    return orderedZipFiles.size();
  }

  /**
   * @return the orderedScripts
   */
  public DropInZip[] getOrderedZipFiles() {
    return orderedZipFiles.values().toArray(new DropInZip[orderedZipFiles.size()]);
  }
}
