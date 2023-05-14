package org.summerb.validation.testDtos;

import java.util.List;

public class Beans {
  private Bean bean1;
  private Bean bean2;

  private List<Bean> beans;

  public Bean getBean1() {
    return bean1;
  }

  public void setBean1(Bean bean1) {
    this.bean1 = bean1;
  }

  public Bean getBean2() {
    return bean2;
  }

  public void setBean2(Bean bean2) {
    this.bean2 = bean2;
  }

  public List<Bean> getBeans() {
    return beans;
  }

  public void setBeans(List<Bean> beans) {
    this.beans = beans;
  }
}
