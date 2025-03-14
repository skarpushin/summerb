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
package integr.org.summerb.easycrud.dtos;

import java.io.Serializable;
import org.summerb.easycrud.api.row.HasUuid;

public class TestDto3 implements HasUuid, Serializable {
  private static final long serialVersionUID = 2232705400887262676L;

  private String id;
  private String linkToDtoOneOptional;
  private long linkToDtoTwo;
  private Long linkToDtoTwoOptional;
  private String linkToSelfOptional;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getLinkToDtoOneOptional() {
    return linkToDtoOneOptional;
  }

  public void setLinkToDtoOneOptional(String linkToDto1Optional) {
    this.linkToDtoOneOptional = linkToDto1Optional;
  }

  public long getLinkToDtoTwo() {
    return linkToDtoTwo;
  }

  public void setLinkToDtoTwo(long linkToDto2) {
    this.linkToDtoTwo = linkToDto2;
  }

  public Long getLinkToDtoTwoOptional() {
    return linkToDtoTwoOptional;
  }

  public void setLinkToDtoTwoOptional(Long linkToDto2Optional) {
    this.linkToDtoTwoOptional = linkToDto2Optional;
  }

  public String getLinkToSelfOptional() {
    return linkToSelfOptional;
  }

  public void setLinkToSelfOptional(String linkToSelfOptional) {
    this.linkToSelfOptional = linkToSelfOptional;
  }
}
