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
package org.eclipse.scout.releng.ant.p2;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** <h4> StatsTask </h4>
 *
 * @author jgu
 * @since 1.1.0 (01.05.2012)
 *
 */
public class StatsTask extends AbstractArtifactPropertyTask {

  private String statsURI;

  public String getStatsURI() {
    return statsURI;
  }

  public void setStatsURI(String statsURI) {
    this.statsURI = statsURI;
  }


  @Override
  public void execute() throws BuildException {
    validate();
    try {
      // read file
      Document doc = readArtifactsDocument();

      //add property
      Node repoProperties = getRepoProperties(doc);
      repoProperties.appendChild(createMirrorProperty(doc));
      increaseSize(repoProperties);

      //create out file
      writeArtifactsDocument(doc);
    }
    catch (Exception e) {
      throw new BuildException("could not add mirrors file.", e);
    }
  }

  protected void validate() throws BuildException {
    super.validate();
    if (getStatsURI() == null) {
      throw new BuildException("getStatsURI must be set.");
    }
  }

  private Element createMirrorProperty(Document doc) {
    Element property = doc.createElement("property");
    property.setAttribute("name", "p2.statsURI");
    property.setAttribute("value", getStatsURI());
    return property;
  }


}
