package org.summerb.validation.testDtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** This Pojo is of an average size -- we use it to estimate performance */
public class TypicalBean {

  private long headerId;
  private boolean applicable;
  private boolean propagate;

  @NotBlank private String projectId;
  @NotNull private LocalDate period;
  @NotBlank private String type;

  @NotNull
  @Min(0)
  @Max(100)
  private BigDecimal score;

  @Size(max = 10)
  private String workProductLocation;

  @Size(max = 10)
  private String workProductLocationComment;

  @Size(max = 10)
  private String comment;

  public long getHeaderId() {
    return headerId;
  }

  public void setHeaderId(long headerId) {
    this.headerId = headerId;
  }

  public boolean isApplicable() {
    return applicable;
  }

  public void setApplicable(boolean applicable) {
    this.applicable = applicable;
  }

  public boolean isPropagate() {
    return propagate;
  }

  public void setPropagate(boolean propagate) {
    this.propagate = propagate;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public LocalDate getPeriod() {
    return period;
  }

  public void setPeriod(LocalDate period) {
    this.period = period;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getWorkProductLocation() {
    return workProductLocation;
  }

  public void setWorkProductLocation(String workProductLocation) {
    this.workProductLocation = workProductLocation;
  }

  public String getWorkProductLocationComment() {
    return workProductLocationComment;
  }

  public void setWorkProductLocationComment(String workProductLocationComment) {
    this.workProductLocationComment = workProductLocationComment;
  }

  public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
