package me.nickpaul;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MyRouteUpdated extends MyDefaultRouteBase {


  @Override
  public void configure() throws Exception {
    setTargetQueue("newQueue");
    super.configure();
  }
}

