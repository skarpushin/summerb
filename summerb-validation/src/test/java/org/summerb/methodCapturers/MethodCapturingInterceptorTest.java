package org.summerb.methodCapturers;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class MethodCapturingInterceptorTest {

  MethodCapturerProxyClassFactory methodCapturerProxyClassFactory =
      new MethodCapturerProxyClassFactoryImpl();

  @Test
  void test() {
    var f =
        new PropertyNameObtainerImpl<RowWithAllTypes>(
            () -> methodCapturerProxyClassFactory.buildProxyFor(RowWithAllTypes.class));

    assertEquals("booleanValue", f.obtainFrom(RowWithAllTypes::isBooleanValue));
    assertEquals("byteValue", f.obtainFrom(RowWithAllTypes::getByteValue));
    assertEquals("charValue", f.obtainFrom(RowWithAllTypes::getCharValue));
    assertEquals("shortValue", f.obtainFrom(RowWithAllTypes::getShortValue));
    assertEquals("intValue", f.obtainFrom(RowWithAllTypes::getIntValue));
    assertEquals("longValue", f.obtainFrom(RowWithAllTypes::getLongValue));
    assertEquals("floatValue", f.obtainFrom(RowWithAllTypes::getFloatValue));
    assertEquals("doubleValue", f.obtainFrom(RowWithAllTypes::getDoubleValue));
    assertEquals("stringValue", f.obtainFrom(RowWithAllTypes::getStringValue));
  }

  public static class RowWithAllTypes {
    private boolean booleanValue;
    private byte byteValue;
    private char charValue;
    private short shortValue;
    private int intValue;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private String stringValue;

    public boolean isBooleanValue() {
      return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
      this.booleanValue = booleanValue;
    }

    public byte getByteValue() {
      return byteValue;
    }

    public void setByteValue(byte byteValue) {
      this.byteValue = byteValue;
    }

    public char getCharValue() {
      return charValue;
    }

    public void setCharValue(char charValue) {
      this.charValue = charValue;
    }

    public short getShortValue() {
      return shortValue;
    }

    public void setShortValue(short shortValue) {
      this.shortValue = shortValue;
    }

    public int getIntValue() {
      return intValue;
    }

    public void setIntValue(int intValue) {
      this.intValue = intValue;
    }

    public long getLongValue() {
      return longValue;
    }

    public void setLongValue(long longValue) {
      this.longValue = longValue;
    }

    public float getFloatValue() {
      return floatValue;
    }

    public void setFloatValue(float floatValue) {
      this.floatValue = floatValue;
    }

    public double getDoubleValue() {
      return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
      this.doubleValue = doubleValue;
    }

    public String getStringValue() {
      return stringValue;
    }

    public void setStringValue(String stringValue) {
      this.stringValue = stringValue;
    }
  }
}
