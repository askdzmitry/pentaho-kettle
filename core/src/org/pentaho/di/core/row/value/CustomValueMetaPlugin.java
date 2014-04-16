/*
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2014 by Pentaho : http://www.pentaho.com
 *
 * **************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pentaho.di.core.row.value;

import java.util.ArrayList;
import java.util.List;

public class CustomValueMetaPlugin extends ValueMetaBase {

  private List customList = new ArrayList();

  public ValueMetaBase clone() {
    Object o = super.clone();
    CustomValueMetaPlugin meta = (CustomValueMetaPlugin) super.clone();
    meta.customList = new ArrayList();
    for ( int i = 0; i < customList.size(); i++ ) {
      Object str = (String) customList.get( i );
      meta.customList.add( str );
    }
    return meta;
  }

  @Override
  public int getType() {
    return 1010;
  }

  public List getCustomList() {
    return customList;
  }

  public void setCustomList( List customList ) {
    this.customList = customList;
  }

}
