package me.nickpaul.exception;

import me.nickpaul.exception.TranslationException;
import org.apache.camel.Handler;

public class FailureExceptionBean {

  @Handler
  void throwTranslationException() throws TranslationException {
    throw new RuntimeException();
  }
}
