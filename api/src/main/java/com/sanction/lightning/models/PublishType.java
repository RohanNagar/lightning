package com.sanction.lightning.models;

public enum PublishType {
  TEXT("text"),
  PHOTO("photo"),
  VIDEO("video");

  private final String text;

  PublishType(String text) {
    this.text = text;
  }

  /**
   * Provides a PublishType representation of a given string.
   *
   * @param text The String to parse into a PublishType.
   * @return The corresponding PublishType representation.
   */
  public static PublishType fromString(String text) {
    for (PublishType b : PublishType.values()) {
      if (b.text.equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return this.text;
  }
}
