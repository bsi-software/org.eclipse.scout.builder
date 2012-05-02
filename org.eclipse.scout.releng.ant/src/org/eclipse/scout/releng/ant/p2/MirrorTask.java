package org.eclipse.scout.releng.ant.p2;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MirrorTask extends AbstractArtifactPropertyTask {

  private String mirrorsURL;

  public String getMirrorsURL() {
    return mirrorsURL;
  }

  public void setMirrorsURL(String mirrorsURL) {
    this.mirrorsURL = mirrorsURL;
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
    if (getMirrorsURL() == null) {
      throw new BuildException("MirrorsURL must be set.");
    }
  }

  private Element createMirrorProperty(Document doc) {
    Element property = doc.createElement("property");
    property.setAttribute("name", "p2.mirrorsURL");
    property.setAttribute("value", getMirrorsURL());
    return property;
  }

}
