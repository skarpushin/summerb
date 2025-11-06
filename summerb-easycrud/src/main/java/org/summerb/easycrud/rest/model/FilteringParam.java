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
package org.summerb.easycrud.rest.model;

public class FilteringParam {
  public static final String CMD_EQUALS = "equal";
  public static final String CMD_NOT_EQUALS = "not.equal";
  public static final String CMD_CONTAIN = "contain";
  public static final String CMD_NOT_CONTAIN = "not.contain";
  public static final String CMD_IN = "in";
  public static final String CMD_NOT_IN = "not.in";
  public static final String CMD_BETWEEN = "between";
  public static final String CMD_NOT_BETWEEN = "not.between";
  public static final String CMD_LESS = "less";
  public static final String CMD_LESS_OR_EQUAL = "less.or.equal";
  public static final String CMD_GREATER = "greater";
  public static final String CMD_GREATER_OR_EQUAL = "greater.or.equal";

  protected String command;
  protected String[] values;

  public static FilteringParam build(String command, String value) {
    FilteringParam ret = new FilteringParam();
    ret.setCommand(command);
    ret.setValues(new String[] {value});
    return ret;
  }

  public static FilteringParam build(String command, long value) {
    FilteringParam ret = new FilteringParam();
    ret.setCommand(command);
    ret.setValues(new String[] {String.valueOf(value)});
    return ret;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String[] getValues() {
    return values;
  }

  public void setValues(String[] values) {
    this.values = values;
  }
}
