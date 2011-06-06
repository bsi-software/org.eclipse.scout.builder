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
package org.eclipse.scout.releng.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <h4>WaitForFile</h4>
 * 
 * @author aho
 * @since 1.1.0 (26.01.2011)
 */
public class WaitForFile extends Task {

  private File file;
  private int maxMinutes = 30;
  private int maxSecounds = 0;
  private int pollSecounds = 5;

  /**
   * @param file
   *          the file to set
   */
  public void setFile(File file) {
    this.file = file;
  }

  /**
   * @return the file
   */
  public File getFile() {
    return file;
  }

  /**
   * @param maxMinutes
   *          the maxMinutes to set
   */
  public void setMaxMinutes(int maxMinutes) {
    this.maxMinutes = maxMinutes;
  }

  /**
   * @return the maxMinutes
   */
  public int getMaxMinutes() {
    return maxMinutes;
  }

  /**
   * @param maxSecounds
   *          the maxSecounds to set
   */
  public void setMaxSecounds(int maxSecounds) {
    this.maxSecounds = maxSecounds;
  }

  /**
   * @return the maxSecounds
   */
  public int getMaxSecounds() {
    return maxSecounds;
  }

  /**
   * @return the pollSecounds
   */
  public int getPollSecounds() {
    return pollSecounds;
  }

  /**
   * @param pollSecounds
   *          the pollSecounds to set
   */
  public void setPollSecounds(int pollSecounds) {
    this.pollSecounds = pollSecounds;
  }

  @Override
  public void execute() throws BuildException {
    validate();
    long maxWaitTime = System.currentTimeMillis() + getMaxMinutes() * 60 * 1000 + getMaxSecounds() * 1000;
    waitForFile(maxWaitTime, getPollSecounds(), -1);
  }

  private void waitForFile(long maxWaitTime, long pollSecounds, long fileLengh) throws BuildException {
    log("wait for '" + getFile() + "'.");
    if (System.currentTimeMillis() > maxWaitTime) {
      throw new BuildException("wait for file '" + getFile() + "' timed out after '" + getMaxMinutes() + "' minutes and '" + getMaxSecounds() + "' secounds.");
    }
    if (getFile().exists() && file.isFile() && file.canRead()) {
      if(getFile().length() == fileLengh){
        log("file '" + getFile() + "' found .");
        return;
      }else{
        fileLengh = getFile().length();
        try {
          Thread.sleep(getPollSecounds() * 1000);
          waitForFile(maxWaitTime, Math.max(pollSecounds, 5), fileLengh);
        }
        catch (InterruptedException e) {
          throw new BuildException("wait for file interrupted. ", e);
        } 
      }
    }
    else {
      try {
        Thread.sleep(getPollSecounds() * 1000);
        waitForFile(maxWaitTime, pollSecounds, fileLengh);
      }
      catch (InterruptedException e) {
        throw new BuildException("wait for file interrupted. ", e);
      }
    }

  }

  private void validate() throws BuildException {
    if (getFile() == null) {
      throw new BuildException("no file to wait for specified");
    }
  }

  public static void main(String[] args) {
    WaitForFile waitForFile = new WaitForFile();
    waitForFile.setFile(new File("D:/Temp/max24h/eclipseBuild/sign/out/sign.zip"));
    waitForFile.setPollSecounds(2);
    waitForFile.execute();
  }
}
