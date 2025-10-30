package org.summerb.validation.testDtos;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahDate;
import java.time.chrono.JapaneseDate;
import java.time.chrono.MinguoDate;
import java.time.chrono.ThaiBuddhistDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This Pojo has all kinds of supported jakarta validation annotations
 *
 * @author Sergey Karpushin
 */
public class JakartaFullBean {

  @Size(min = 3, max = 7)
  String sizeString;

  @Size(min = 3, max = 7)
  StringBuilder sizeStringBuilder;

  @Size(min = 1, max = 3)
  List<String> sizeCollection;

  @Size(min = 1, max = 3)
  Map<String, String> sizeMap;

  @Size(min = 1, max = 3)
  String[] sizeArray;

  @NotEmpty String notEmptyString;
  @NotEmpty StringBuilder notEmptyStringBuilder;
  @NotEmpty List<String> notEmptyCollection;
  @NotEmpty Map<String, String> notEmptyMap;
  @NotEmpty String[] notEmptyArray;

  @NotBlank String notBlankString;
  @NotBlank StringBuilder notBlankStringBuilder;

  @Positive BigDecimal positiveBigDecimal;
  @Positive BigInteger positiveBigInteger;
  @Positive byte positiveByte;
  @Positive short positiveShort;
  @Positive int positiveInt;
  @Positive Long positiveLong;
  @Positive float positiveFloat;
  @Positive Double positiveDouble;

  @PositiveOrZero long positiveOrZeroLong;
  @Negative long negativeLong;
  @NegativeOrZero long negativeOrZeroLong;

  @Min(value = 5)
  BigDecimal minBigDecimal;

  @Min(value = 5)
  BigInteger minBigInteger;

  @Min(value = 5)
  byte minByte;

  @Min(value = 5)
  short minShort;

  @Min(value = 5)
  Integer minInt;

  @Min(value = 5)
  Long minLong;

  @Max(value = 10)
  Long maxLong;

  @DecimalMax(value = "10.05")
  String decimalMaxString;

  @DecimalMin(value = "5.05")
  BigDecimal decimalMinBigDecimal;

  @Digits(integer = 3, fraction = 2)
  BigDecimal digitsBigDecimal;

  @Pattern(regexp = "[a-z]+", flags = Flag.CASE_INSENSITIVE)
  String patternString;

  @Pattern(regexp = "\\d+")
  StringBuilder patternStringBuilder;

  @Email String emailString;
  @Email StringBuilder emailStringBuilder;

  @Email(regexp = "[a-z]+@[a-z]+\\.com")
  String emailStringRegex;

  @Future Date futureDate;
  @Future Calendar futureCalendar;
  @Future Instant futureInstant;
  @Future LocalDate futureLocalDate;
  @Future LocalDateTime futureLocalDateTime;
  LocalTime futureLocalTime;
  @Future MonthDay futureMonthDay;
  @Future OffsetDateTime futureOffsetDateTime;
  OffsetTime futureOffsetTime;
  @Future Year futureYear;
  @Future YearMonth futureYearMonth;
  @Future ZonedDateTime futureZonedDateTime;
  @Future HijrahDate futureHijrahDate;
  @Future JapaneseDate futureJapaneseDate;
  @Future MinguoDate futureMinguoDate;
  @Future ThaiBuddhistDate futureThaiBuddhistDate;

  @FutureOrPresent Year futureOrPresentYear;
  @PastOrPresent Year pastOrPresentYear;
  @Past Year pastYear;

  @Null String nullString;
  @NotNull String notNullString;

  @AssertTrue Boolean assertTrueBoolean;
  @AssertFalse boolean assertFalseBoolean;

  public String getSizeString() {
    return sizeString;
  }

  public void setSizeString(String sizeString) {
    this.sizeString = sizeString;
  }

  public StringBuilder getSizeStringBuilder() {
    return sizeStringBuilder;
  }

  public void setSizeStringBuilder(StringBuilder sizeStringBuilder) {
    this.sizeStringBuilder = sizeStringBuilder;
  }

  public List<String> getSizeCollection() {
    return sizeCollection;
  }

  public void setSizeCollection(List<String> sizeCollection) {
    this.sizeCollection = sizeCollection;
  }

  public Map<String, String> getSizeMap() {
    return sizeMap;
  }

  public void setSizeMap(Map<String, String> sizeMap) {
    this.sizeMap = sizeMap;
  }

  public String[] getSizeArray() {
    return sizeArray;
  }

  public void setSizeArray(String[] sizeArray) {
    this.sizeArray = sizeArray;
  }

  public String getNotEmptyString() {
    return notEmptyString;
  }

  public void setNotEmptyString(String notEmptyString) {
    this.notEmptyString = notEmptyString;
  }

  public StringBuilder getNotEmptyStringBuilder() {
    return notEmptyStringBuilder;
  }

  public void setNotEmptyStringBuilder(StringBuilder notEmptyStringBuilder) {
    this.notEmptyStringBuilder = notEmptyStringBuilder;
  }

  public List<String> getNotEmptyCollection() {
    return notEmptyCollection;
  }

  public void setNotEmptyCollection(List<String> notEmptyCollection) {
    this.notEmptyCollection = notEmptyCollection;
  }

  public Map<String, String> getNotEmptyMap() {
    return notEmptyMap;
  }

  public void setNotEmptyMap(Map<String, String> notEmptyMap) {
    this.notEmptyMap = notEmptyMap;
  }

  public String[] getNotEmptyArray() {
    return notEmptyArray;
  }

  public void setNotEmptyArray(String[] notEmptyArray) {
    this.notEmptyArray = notEmptyArray;
  }

  public String getNotBlankString() {
    return notBlankString;
  }

  public void setNotBlankString(String notBlankString) {
    this.notBlankString = notBlankString;
  }

  public StringBuilder getNotBlankStringBuilder() {
    return notBlankStringBuilder;
  }

  public void setNotBlankStringBuilder(StringBuilder notBlankStringBuilder) {
    this.notBlankStringBuilder = notBlankStringBuilder;
  }

  public BigDecimal getPositiveBigDecimal() {
    return positiveBigDecimal;
  }

  public void setPositiveBigDecimal(BigDecimal positiveBigDecimal) {
    this.positiveBigDecimal = positiveBigDecimal;
  }

  public BigInteger getPositiveBigInteger() {
    return positiveBigInteger;
  }

  public void setPositiveBigInteger(BigInteger positiveBigInteger) {
    this.positiveBigInteger = positiveBigInteger;
  }

  public byte getPositiveByte() {
    return positiveByte;
  }

  public void setPositiveByte(byte positiveByte) {
    this.positiveByte = positiveByte;
  }

  public short getPositiveShort() {
    return positiveShort;
  }

  public void setPositiveShort(short positiveShort) {
    this.positiveShort = positiveShort;
  }

  public int getPositiveInt() {
    return positiveInt;
  }

  public void setPositiveInt(int positiveInt) {
    this.positiveInt = positiveInt;
  }

  public Long getPositiveLong() {
    return positiveLong;
  }

  public void setPositiveLong(Long positiveLong) {
    this.positiveLong = positiveLong;
  }

  public float getPositiveFloat() {
    return positiveFloat;
  }

  public void setPositiveFloat(float positiveFloat) {
    this.positiveFloat = positiveFloat;
  }

  public Double getPositiveDouble() {
    return positiveDouble;
  }

  public void setPositiveDouble(Double positiveDouble) {
    this.positiveDouble = positiveDouble;
  }

  public BigDecimal getMinBigDecimal() {
    return minBigDecimal;
  }

  public void setMinBigDecimal(BigDecimal minBigDecimal) {
    this.minBigDecimal = minBigDecimal;
  }

  public BigInteger getMinBigInteger() {
    return minBigInteger;
  }

  public void setMinBigInteger(BigInteger minBigInteger) {
    this.minBigInteger = minBigInteger;
  }

  public byte getMinByte() {
    return minByte;
  }

  public void setMinByte(byte minByte) {
    this.minByte = minByte;
  }

  public short getMinShort() {
    return minShort;
  }

  public void setMinShort(short minShort) {
    this.minShort = minShort;
  }

  public Integer getMinInt() {
    return minInt;
  }

  public void setMinInt(Integer minInt) {
    this.minInt = minInt;
  }

  public Long getMinLong() {
    return minLong;
  }

  public void setMinLong(Long minLong) {
    this.minLong = minLong;
  }

  public Long getMaxLong() {
    return maxLong;
  }

  public void setMaxLong(Long maxLong) {
    this.maxLong = maxLong;
  }

  public String getDecimalMaxString() {
    return decimalMaxString;
  }

  public void setDecimalMaxString(String decimalMaxString) {
    this.decimalMaxString = decimalMaxString;
  }

  public BigDecimal getDecimalMinBigDecimal() {
    return decimalMinBigDecimal;
  }

  public void setDecimalMinBigDecimal(BigDecimal decimalMinBigDecimal) {
    this.decimalMinBigDecimal = decimalMinBigDecimal;
  }

  public BigDecimal getDigitsBigDecimal() {
    return digitsBigDecimal;
  }

  public void setDigitsBigDecimal(BigDecimal digitsBigDecimal) {
    this.digitsBigDecimal = digitsBigDecimal;
  }

  public long getPositiveOrZeroLong() {
    return positiveOrZeroLong;
  }

  public void setPositiveOrZeroLong(long positiveOrZeroLong) {
    this.positiveOrZeroLong = positiveOrZeroLong;
  }

  public long getNegativeLong() {
    return negativeLong;
  }

  public void setNegativeLong(long negativeLong) {
    this.negativeLong = negativeLong;
  }

  public long getNegativeOrZeroLong() {
    return negativeOrZeroLong;
  }

  public void setNegativeOrZeroLong(long negativeOrZeroLong) {
    this.negativeOrZeroLong = negativeOrZeroLong;
  }

  public String getPatternString() {
    return patternString;
  }

  public void setPatternString(String patternString) {
    this.patternString = patternString;
  }

  public StringBuilder getPatternStringBuilder() {
    return patternStringBuilder;
  }

  public void setPatternStringBuilder(StringBuilder patternStringBuilder) {
    this.patternStringBuilder = patternStringBuilder;
  }

  public String getEmailString() {
    return emailString;
  }

  public void setEmailString(String emailString) {
    this.emailString = emailString;
  }

  public StringBuilder getEmailStringBuilder() {
    return emailStringBuilder;
  }

  public void setEmailStringBuilder(StringBuilder emailStringBuilder) {
    this.emailStringBuilder = emailStringBuilder;
  }

  public String getEmailStringRegex() {
    return emailStringRegex;
  }

  public void setEmailStringRegex(String emailStringRegex) {
    this.emailStringRegex = emailStringRegex;
  }

  public Date getFutureDate() {
    return futureDate;
  }

  public void setFutureDate(Date futureDate) {
    this.futureDate = futureDate;
  }

  public Calendar getFutureCalendar() {
    return futureCalendar;
  }

  public void setFutureCalendar(Calendar futureCalendar) {
    this.futureCalendar = futureCalendar;
  }

  public Instant getFutureInstant() {
    return futureInstant;
  }

  public void setFutureInstant(Instant futureInstant) {
    this.futureInstant = futureInstant;
  }

  public LocalDate getFutureLocalDate() {
    return futureLocalDate;
  }

  public void setFutureLocalDate(LocalDate futureLocalDate) {
    this.futureLocalDate = futureLocalDate;
  }

  public LocalDateTime getFutureLocalDateTime() {
    return futureLocalDateTime;
  }

  public void setFutureLocalDateTime(LocalDateTime futureLocalDateTime) {
    this.futureLocalDateTime = futureLocalDateTime;
  }

  public LocalTime getFutureLocalTime() {
    return futureLocalTime;
  }

  public void setFutureLocalTime(LocalTime futureLocalTime) {
    this.futureLocalTime = futureLocalTime;
  }

  public MonthDay getFutureMonthDay() {
    return futureMonthDay;
  }

  public void setFutureMonthDay(MonthDay futureMonthDay) {
    this.futureMonthDay = futureMonthDay;
  }

  public OffsetDateTime getFutureOffsetDateTime() {
    return futureOffsetDateTime;
  }

  public void setFutureOffsetDateTime(OffsetDateTime futureOffsetDateTime) {
    this.futureOffsetDateTime = futureOffsetDateTime;
  }

  public OffsetTime getFutureOffsetTime() {
    return futureOffsetTime;
  }

  public void setFutureOffsetTime(OffsetTime futureOffsetTime) {
    this.futureOffsetTime = futureOffsetTime;
  }

  public Year getFutureYear() {
    return futureYear;
  }

  public void setFutureYear(Year futureYear) {
    this.futureYear = futureYear;
  }

  public YearMonth getFutureYearMonth() {
    return futureYearMonth;
  }

  public void setFutureYearMonth(YearMonth futureYearMonth) {
    this.futureYearMonth = futureYearMonth;
  }

  public ZonedDateTime getFutureZonedDateTime() {
    return futureZonedDateTime;
  }

  public void setFutureZonedDateTime(ZonedDateTime futureZonedDateTime) {
    this.futureZonedDateTime = futureZonedDateTime;
  }

  public HijrahDate getFutureHijrahDate() {
    return futureHijrahDate;
  }

  public void setFutureHijrahDate(HijrahDate futureHijrahDate) {
    this.futureHijrahDate = futureHijrahDate;
  }

  public JapaneseDate getFutureJapaneseDate() {
    return futureJapaneseDate;
  }

  public void setFutureJapaneseDate(JapaneseDate futureJapaneseDate) {
    this.futureJapaneseDate = futureJapaneseDate;
  }

  public MinguoDate getFutureMinguoDate() {
    return futureMinguoDate;
  }

  public void setFutureMinguoDate(MinguoDate futureMinguoDate) {
    this.futureMinguoDate = futureMinguoDate;
  }

  public ThaiBuddhistDate getFutureThaiBuddhistDate() {
    return futureThaiBuddhistDate;
  }

  public void setFutureThaiBuddhistDate(ThaiBuddhistDate futureThaiBuddhistDate) {
    this.futureThaiBuddhistDate = futureThaiBuddhistDate;
  }

  public Year getFutureOrPresentYear() {
    return futureOrPresentYear;
  }

  public void setFutureOrPresentYear(Year futureOrPresentYear) {
    this.futureOrPresentYear = futureOrPresentYear;
  }

  public Year getPastOrPresentYear() {
    return pastOrPresentYear;
  }

  public void setPastOrPresentYear(Year pastOrPresentYear) {
    this.pastOrPresentYear = pastOrPresentYear;
  }

  public Year getPastYear() {
    return pastYear;
  }

  public void setPastYear(Year pastYear) {
    this.pastYear = pastYear;
  }

  public String getNullString() {
    return nullString;
  }

  public void setNullString(String nullString) {
    this.nullString = nullString;
  }

  public String getNotNullString() {
    return notNullString;
  }

  public void setNotNullString(String notNullString) {
    this.notNullString = notNullString;
  }

  public Boolean getAssertTrueBoolean() {
    return assertTrueBoolean;
  }

  public void setAssertTrueBoolean(Boolean assertTrueBoolean) {
    this.assertTrueBoolean = assertTrueBoolean;
  }

  public boolean isAssertFalseBoolean() {
    return assertFalseBoolean;
  }

  public void setAssertFalseBoolean(boolean assertFalseBoolean) {
    this.assertFalseBoolean = assertFalseBoolean;
  }
}
