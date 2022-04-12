package me.nickpaul;

import javax.enterprise.context.ApplicationScoped;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sjms2.Sjms2Component;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

@ApplicationScoped
public class SimpleRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    // Create the JMS Connection Factory
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");

    // Create the pooled connection
    JmsPoolConnectionFactory pooledConnectionFactory = new JmsPoolConnectionFactory ();
    pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);

    // Create a unique component with the pooled connection.
    Sjms2Component component = new Sjms2Component();
    component.setConnectionFactory(pooledConnectionFactory);
    this.getCamelContext().addComponent("my-sjms2", component);


    // Based on the message send to Error, Audit or Destination Endpoints
    from("my-sjms2:inbound")
        .log("ReceivedMessage: ${body}")
        .choice()
          .when(simple("${body} == 'error'"))
            .to("my-sjms2:error")
          .when(simple("${body} == 'fail'"))
            .to("my-sjms2:fail")
          .otherwise()
             .to("my-sjms2:destination");
    }

}