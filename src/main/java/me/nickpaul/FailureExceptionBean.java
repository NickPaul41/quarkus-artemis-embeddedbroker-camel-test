package me.nickpaul;

import java.util.concurrent.ExecutionException;
import org.apache.camel.Handler;

public class FailureExceptionBean {

  @Handler
  void throwTranslationException() throws TranslationException {
    throw new RuntimeException();
  }
}
