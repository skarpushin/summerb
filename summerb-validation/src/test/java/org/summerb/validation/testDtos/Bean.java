package org.summerb.validation.testDtos;

import java.util.List;

public class Bean {
  private boolean bValue1;
  private Boolean bValue2;
  private int iValue1;
  private Integer iValue2;
  private String string1;
  private String string2;
  private List<Integer> list;

  @Override
  public String toString() {
    return super.toString();
  }

  public void getWrongMethod(int a) {
    // no op
  }

  public boolean isbValue1() {
    return bValue1;
  }

  public void setbValue1(boolean bValue) {
    this.bValue1 = bValue;
  }

  public Boolean getbValue2() {
    return bValue2;
  }

  public void setbValue2(Boolean bValue2) {
    this.bValue2 = bValue2;
  }

  public int getiValue1() {
    return iValue1;
  }

  public void setiValue1(int iValue1) {
    this.iValue1 = iValue1;
  }

  public String getString1() {
    return string1;
  }

  public void setString1(String string1) {
    this.string1 = string1;
  }

  public Integer getiValue2() {
    return iValue2;
  }

  public void setiValue2(Integer iValue2) {
    this.iValue2 = iValue2;
  }

  public String getString2() {
    return string2;
  }

  public void setString2(String string2) {
    this.string2 = string2;
  }

  public List<Integer> getList() {
    return list;
  }

  public void setList(List<Integer> list) {
    this.list = list;
  }
}
