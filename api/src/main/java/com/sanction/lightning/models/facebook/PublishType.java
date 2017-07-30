package com.sanction.lightning.models.facebook;

public enum PublishType {
  TEXT("text"),
  PHOTO("photo"),
  VIDEO("video");

  private String text;

  PublishType(String text) {
    this.text = text;
  }

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
