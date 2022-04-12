package me.nickpaul;

import org.apache.camel.Handler;

public class ThrowTranslationExceptionBean {
  @Handler
  void throwTranslationException() throws TranslationException {
    throw new TranslationException("Error");
  }
}
