package me.nickpaul.config;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "my.jms.out")
public interface JmsOut extends AmqBase{

}
