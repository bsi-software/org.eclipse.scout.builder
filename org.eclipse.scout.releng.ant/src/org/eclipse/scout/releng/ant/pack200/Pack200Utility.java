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

import java.util.SortedMap;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;

/** <h4> Pack200Utility </h4>
 *
 * @author aho
 * @since 1.1.0 (26.01.2011)
 *
 */
public final class Pack200Utility {

  public static final Packer createPacker(){
    Pack200.Packer p = Pack200.newPacker();
    SortedMap<String, String> props = p.properties();
//    props.put(Pack200.Packer.KEEP_FILE_ORDER, Pack200.Packer.TRUE);
    props.put(Pack200.Packer.MODIFICATION_TIME, Pack200.Packer.KEEP);
    props.put(Pack200.Packer.EFFORT, "0");
//    props.put(Pack200.Packer.CODE_ATTRIBUTE_PFX+"LocalVariableTable", Pack200.Packer.STRIP);
    return p;
  }

  public static final Unpacker createUnpacker(){
    Pack200.Unpacker p = Pack200.newUnpacker();
    SortedMap<String, String> props = p.properties();
//    props.put(Pack200.Packer.KEEP_FILE_ORDER, Pack200.Packer.TRUE);
    props.put(Pack200.Packer.MODIFICATION_TIME, Pack200.Packer.KEEP);
    props.put(Pack200.Packer.EFFORT, "0");
//    props.put(Pack200.Packer.CODE_ATTRIBUTE_PFX+"LocalVariableTable", Pack200.Packer.STRIP);
    return p;
  }

}
