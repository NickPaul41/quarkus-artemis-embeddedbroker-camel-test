package me.nickpaul;

import javax.jms.JMSException;
import me.nickpaul.config.AmqBase;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.sjms2.Sjms2Component;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

public class JmsUtils {

  static void addSjms2NamedComponent(CamelContext context, AmqBase config) throws JMSException {
    // Create the JMS Connection Factory
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL(config.brokeUrl());

    // Create the pooled connection
    JmsPoolConnectionFactory pooledConnectionFactory = new JmsPoolConnectionFactory();
    pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);

    // Create a unique component with the pooled connection.
    Sjms2Component component = new Sjms2Component();
    component.setConnectionFactory(pooledConnectionFactory);
    context.addComponent(config.componentName(), component);
  }
}
