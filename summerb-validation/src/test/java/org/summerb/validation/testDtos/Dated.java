package org.summerb.validation.testDtos;

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

public class Dated {
  private Date valueDate;
  private Calendar valueCalendar;
  private Instant valueInstant;
  private LocalDate valueLocalDate;
  private LocalDateTime valueLocalDateTime;
  private LocalTime valueLocalTime;
  private MonthDay valueMonthDay;
  private OffsetDateTime valueOffsetDateTime;
  private OffsetTime valueOffsetTime;
  private Year valueYear;
  private YearMonth valueYearMonth;
  private ZonedDateTime valueZonedDateTime;
  private HijrahDate valueHijrahDate;
  private JapaneseDate valueJapaneseDate;
  private MinguoDate valueMinguoDate;
  private ThaiBuddhistDate valueThaiBuddhistDate;

  public Date getValueDate() {
    return valueDate;
  }

  public void setValueDate(Date valueDate) {
    this.valueDate = valueDate;
  }

  public Calendar getValueCalendar() {
    return valueCalendar;
  }

  public void setValueCalendar(Calendar valueCalendar) {
    this.valueCalendar = valueCalendar;
  }

  public Instant getValueInstant() {
    return valueInstant;
  }

  public void setValueInstant(Instant valueInstant) {
    this.valueInstant = valueInstant;
  }

  public LocalDate getValueLocalDate() {
    return valueLocalDate;
  }

  public void setValueLocalDate(LocalDate valueLocalDate) {
    this.valueLocalDate = valueLocalDate;
  }

  public LocalDateTime getValueLocalDateTime() {
    return valueLocalDateTime;
  }

  public void setValueLocalDateTime(LocalDateTime valueLocalDateTime) {
    this.valueLocalDateTime = valueLocalDateTime;
  }

  public LocalTime getValueLocalTime() {
    return valueLocalTime;
  }

  public void setValueLocalTime(LocalTime valueLocalTime) {
    this.valueLocalTime = valueLocalTime;
  }

  public MonthDay getValueMonthDay() {
    return valueMonthDay;
  }

  public void setValueMonthDay(MonthDay valueMonthDay) {
    this.valueMonthDay = valueMonthDay;
  }

  public OffsetDateTime getValueOffsetDateTime() {
    return valueOffsetDateTime;
  }

  public void setValueOffsetDateTime(OffsetDateTime valueOffsetDateTime) {
    this.valueOffsetDateTime = valueOffsetDateTime;
  }

  public OffsetTime getValueOffsetTime() {
    return valueOffsetTime;
  }

  public void setValueOffsetTime(OffsetTime valueOffsetTime) {
    this.valueOffsetTime = valueOffsetTime;
  }

  public Year getValueYear() {
    return valueYear;
  }

  public void setValueYear(Year valueYear) {
    this.valueYear = valueYear;
  }

  public YearMonth getValueYearMonth() {
    return valueYearMonth;
  }

  public void setValueYearMonth(YearMonth valueYearMonth) {
    this.valueYearMonth = valueYearMonth;
  }

  public ZonedDateTime getValueZonedDateTime() {
    return valueZonedDateTime;
  }

  public void setValueZonedDateTime(ZonedDateTime valueZonedDateTime) {
    this.valueZonedDateTime = valueZonedDateTime;
  }

  public HijrahDate getValueHijrahDate() {
    return valueHijrahDate;
  }

  public void setValueHijrahDate(HijrahDate valueHijrahDate) {
    this.valueHijrahDate = valueHijrahDate;
  }

  public JapaneseDate getValueJapaneseDate() {
    return valueJapaneseDate;
  }

  public void setValueJapaneseDate(JapaneseDate valueJapaneseDate) {
    this.valueJapaneseDate = valueJapaneseDate;
  }

  public MinguoDate getValueMinguoDate() {
    return valueMinguoDate;
  }

  public void setValueMinguoDate(MinguoDate valueMinguoDate) {
    this.valueMinguoDate = valueMinguoDate;
  }

  public ThaiBuddhistDate getValueThaiBuddhistDate() {
    return valueThaiBuddhistDate;
  }

  public void setValueThaiBuddhistDate(ThaiBuddhistDate valueThaiBuddhistDate) {
    this.valueThaiBuddhistDate = valueThaiBuddhistDate;
  }
}
