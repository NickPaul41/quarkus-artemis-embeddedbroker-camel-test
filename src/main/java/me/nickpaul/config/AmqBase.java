package me.nickpaul.config;

import io.smallrye.config.WithDefault;

public interface AmqBase {
  String brokeUrl();

  String componentName();

  String destinationName();

  @WithDefault("queue")
  String destinationType();
}
