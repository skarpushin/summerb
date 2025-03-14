package org.summerb.easycrud.api;

import java.util.Arrays;

public enum EasyCrudWireTapMode {
  /** WireTap invocation is not needed */
  NOT_APPLICABLE(0),
  /** WireTap invocation is needed, but no information about DTO is required */
  ONLY_INVOKE_WIRETAP(1),
  /** WireTap invocation is needed, and full dto is required to be passed to wiretap */
  FULL_DTO_NEEDED(2),
  /**
   * Special case for UPDATE WireTap. Both: currently persisted row version and new version that is
   * about to be persisted are required to be passed to wiretap
   */
  FULL_DTO_AND_CURRENT_VERSION_NEEDED(3);

  final int level;

  EasyCrudWireTapMode(int level) {
    this.level = level;
  }

  /**
   * Compare this instance to other
   *
   * @param other instance to compare to
   * @return EasyCrudWireTapMode with the highest level
   */
  public EasyCrudWireTapMode max(EasyCrudWireTapMode other) {
    if (other.level > level) {
      return other;
    }
    return this;
  }

  EasyCrudWireTapMode fromLevel(int level) {
    return Arrays.stream(EasyCrudWireTapMode.values())
        .filter(x -> x.level == level)
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "EasyCrudWireTapMode with level " + level + " is not defined"));
  }

  public boolean isNeeded() {
    return level > NOT_APPLICABLE.level;
  }

  public boolean isDtoNeeded() {
    return level >= FULL_DTO_NEEDED.level;
  }
}
