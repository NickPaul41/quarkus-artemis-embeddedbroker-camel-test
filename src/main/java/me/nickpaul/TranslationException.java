package me.nickpaul;

/**
 * Generic exception which can be thrown by translations to indicate a translation error.
 */
public class TranslationException extends Exception {
  public TranslationException(String message) {
    super(message);
  }

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }
}