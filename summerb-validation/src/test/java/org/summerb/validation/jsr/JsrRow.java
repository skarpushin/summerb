package org.summerb.validation.jsr;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.validation.constraints.AssertTrue;

public class JsrRow {

  @AssertTrue private boolean booleanValue;
  @AssertTrue private boolean booleanValue2;
  private byte byteValue;
  private char charValue;
  private short shortValue;
  private int intValue;
  private long longValue;
  private float floatValue;
  private double doubleValue;
  private String stringValue;
  private BigDecimal bigDecimalValue;
  private BigInteger bigIntegerValue;

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

  public BigDecimal getBigDecimalValue() {
    return bigDecimalValue;
  }

  public void setBigDecimalValue(BigDecimal bigDecimalValue) {
    this.bigDecimalValue = bigDecimalValue;
  }

  public BigInteger getBigIntegerValue() {
    return bigIntegerValue;
  }

  public void setBigIntegerValue(BigInteger bigIntegerValue) {
    this.bigIntegerValue = bigIntegerValue;
  }

  public boolean isBooleanValue2() {
    return booleanValue2;
  }

  public void setBooleanValue2(boolean booleanValue2) {
    this.booleanValue2 = booleanValue2;
  }
}
