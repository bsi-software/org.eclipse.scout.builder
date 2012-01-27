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

import java.io.IOException;
import java.util.Properties;

import org.junit.Assert;

/** <h4> AbstractTestCase </h4>
 *
 * @author aho
 * @since 1.1.0 (27.01.2011)
 *
 */
public abstract class AbstractTestCase {
  public static final String PROP_TEST_DATA_DIR = "testDataDir";
  private Properties m_properties;
  
  public AbstractTestCase(){
    m_properties= new Properties();
    try {
      m_properties.load(AbstractTestCase.class.getClassLoader().getResourceAsStream("testCase.properties"));
    }
    catch (IOException e) {
      Assert.fail(e.getMessage());
    }
  }
  
  protected String getTestDataDir(){
    return getProperty(PROP_TEST_DATA_DIR);
 }
  
  protected String getProperty(String key){
    return m_properties.getProperty(key);
  }

}
