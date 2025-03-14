/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.dbupgrade.api;

import java.io.InputStream;
import java.util.function.Supplier;

public class UpgradePackageMeta {
  private int version;
  private String name;
  private String type;
  private Supplier<InputStream> source;

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String extension) {
    this.type = extension;
  }

  @Override
  public String toString() {
    return "UpgradePackageMeta [version=" + version + ", name=" + name + ", type=" + type + "]";
  }

  public Supplier<InputStream> getSource() {
    return source;
  }

  public void setSource(Supplier<InputStream> source) {
    this.source = source;
  }
}
