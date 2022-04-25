package me.nickpaul;

import me.nickpaul.config.AmqBase;
import me.nickpaul.exception.TranslationException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.OnCompletionDefinition;
import org.apache.camel.model.OnExceptionDefinition;


/**
 * Construct a route where the inbound jms component receives a message from a queue/topic
 * and sends to the outbound jms component (These may be separate brokers). If there are
 * error or failure events route to the outbound component on their respective queues.
 */
public class MyBaseRoute extends RouteBuilder {
  String sjms2InComponentName = "jms-in";
  String inboundType = "queue";
  String inboundQueue;
  String sjms2OutComponentName = "jms-out";
  String targetType = "queue";
  String targetQueue;
  String errorQueue = "audit.error";
  String failureQueue = "audit.failure";

  public void setTargetType(String type) {
    if (type.equalsIgnoreCase("topic")) {
      this.targetType = "topic";
    } else {
      this.targetType = "queue";
    }
  }

  public void setInboundType(String type) {
    if (type.equalsIgnoreCase("topic")) {
      this.inboundType = "topic";
    } else {
      this.inboundType = "queue";
    }
  }

  public void setSjms2InComponentName(String sjms2InComponentName) {
    this.sjms2InComponentName = sjms2InComponentName;
  }

  public void setSjms2OutComponentName(String sjms2OutComponentName) {
    this.sjms2OutComponentName = sjms2OutComponentName;
  }

  public void setInboundQueue(String inboundUri) {
    this.inboundQueue = inboundUri;
  }

  public void setErrorQueue(String errorQueue) {
    this.errorQueue = errorQueue;
  }

  public void setFailureQueue(String failureQueue) {
    this.failureQueue = failureQueue;
  }

  public void setTargetQueue(String targetQueue) {
    this.targetQueue = targetQueue;
  }

  public String getInboundUri() {
    return sjms2InComponentName + ":" + inboundType + ":" + inboundQueue;
  }

  public String getErrorUri() {
    return sjms2OutComponentName + ":" + errorQueue;
  }

  public String getFailureUri() {
    return sjms2OutComponentName + ":" + failureQueue;
  }

  public String getTargetUri() {
    return sjms2OutComponentName + ":" + targetType + ":" + targetQueue;
  }

  public void setInboundConfig(AmqBase config) {
    setSjms2InComponentName(config.componentName());
    setInboundQueue(config.destinationName());
    setInboundType(config.destinationType());
  }

  public void setTargetConfig(AmqBase config) {
    setSjms2OutComponentName(config.componentName());
    setTargetQueue(config.destinationName());
    setTargetType(config.destinationType());
  }

  @Override
  public void configure() throws Exception {
    // Setup the exchange failure completion clause
    OnCompletionDefinition onFailure =
        onCompletion()
            .onFailureOnly()
            .toF("%s?transacted=false", getFailureUri())
            .log(LoggingLevel.TRACE,"Sending Message to Failure")
            .log(LoggingLevel.DEBUG, "${body}");

    // Set up the failure exception clause
    OnExceptionDefinition onTranslationExceptionDefinition =
        onException(TranslationException.class)
            .handled(true)
            .toF("%s?transacted=false", getErrorUri())
            .log(LoggingLevel.DEBUG, "Sending message to Error")
            .log(LoggingLevel.DEBUG, "${body}");

  }
}
