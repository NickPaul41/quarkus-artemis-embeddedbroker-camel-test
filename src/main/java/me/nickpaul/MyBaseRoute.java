package me.nickpaul;

import org.apache.camel.builder.RouteBuilder;

public class MyBaseRoute extends RouteBuilder {

  String sjms2ComponentName = "complex-sjms2";
  String inboundUri = sjms2ComponentName + ":complex-inbound";
  String targetUri = sjms2ComponentName + ":complex-error";
  String errorUri = sjms2ComponentName + ":complex-error";
  String failureUri = sjms2ComponentName + ":complex-error";

  public String getErrorUri() {
    return errorUri;
  }

  public String getFailureUri() {
    return failureUri;
  }

  public String getInboundUri() {
    return inboundUri;
  }

  public String getTargetUri() {
    return targetUri;
  }

  public String getSjms2ComponentName() {
    return sjms2ComponentName;
  }

  @Override
  public void configure() throws Exception {

  }
}
