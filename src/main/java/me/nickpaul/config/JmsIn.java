package me.nickpaul.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "my.jms.in")
public interface JmsIn extends AmqBase{

}
