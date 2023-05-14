package org.summerb.validation.testDtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * This Pojo is for testing edge cases of supported jakarta validation annotations
 *
 * @author Sergey Karpushin
 */
public class JakartaEdgeBean {

  @DecimalMax(value = "10.5", inclusive = false)
  String decimalMaxString;

  @DecimalMin(value = "10.5", inclusive = false)
  String decimalMinString;

  @Negative Long negativeOnNegaive;

  @Negative Long negativeOnPositive;

  @Negative Long negativeOnZero;

  @NotEmpty private String notEmptyString;
  @NotBlank private String notBlankString;

  @Size(min = 3, max = 5)
  private String sizeMinInclusiveBoundaryString;

  @Size(min = 3, max = 5)
  private String sizeMaxInclusiveBoundaryString;

  @Size(min = 3, max = 5)
  private String sizeAboveMaxString;

  @Size(min = 3, max = 5)
  private String sizeBelowMinString;

  public String getDecimalMaxString() {
    return decimalMaxString;
  }

  public void setDecimalMaxString(String decimalMaxString) {
    this.decimalMaxString = decimalMaxString;
  }

  public String getDecimalMinString() {
    return decimalMinString;
  }

  public void setDecimalMinString(String decimalMinString) {
    this.decimalMinString = decimalMinString;
  }

  public Long getNegativeOnNegaive() {
    return negativeOnNegaive;
  }

  public void setNegativeOnNegaive(Long negativeOnNegaive) {
    this.negativeOnNegaive = negativeOnNegaive;
  }

  public Long getNegativeOnPositive() {
    return negativeOnPositive;
  }

  public void setNegativeOnPositive(Long negativeOnPositive) {
    this.negativeOnPositive = negativeOnPositive;
  }

  public Long getNegativeOnZero() {
    return negativeOnZero;
  }

  public void setNegativeOnZero(Long negativeOnZero) {
    this.negativeOnZero = negativeOnZero;
  }

  public String getNotEmptyString() {
    return notEmptyString;
  }

  public void setNotEmptyString(String notEmptyString) {
    this.notEmptyString = notEmptyString;
  }

  public String getNotBlankString() {
    return notBlankString;
  }

  public void setNotBlankString(String notBlankString) {
    this.notBlankString = notBlankString;
  }

  public String getSizeMinInclusiveBoundaryString() {
    return sizeMinInclusiveBoundaryString;
  }

  public void setSizeMinInclusiveBoundaryString(String sizeString) {
    this.sizeMinInclusiveBoundaryString = sizeString;
  }

  public String getSizeMaxInclusiveBoundaryString() {
    return sizeMaxInclusiveBoundaryString;
  }

  public void setSizeMaxInclusiveBoundaryString(String sizeMaxInclusiveBoundaryString) {
    this.sizeMaxInclusiveBoundaryString = sizeMaxInclusiveBoundaryString;
  }

  public String getSizeAboveMaxString() {
    return sizeAboveMaxString;
  }

  public void setSizeAboveMaxString(String sizeAboveMaxString) {
    this.sizeAboveMaxString = sizeAboveMaxString;
  }

  public String getSizeBelowMinString() {
    return sizeBelowMinString;
  }

  public void setSizeBelowMinString(String sizeBelowMinString) {
    this.sizeBelowMinString = sizeBelowMinString;
  }
}
