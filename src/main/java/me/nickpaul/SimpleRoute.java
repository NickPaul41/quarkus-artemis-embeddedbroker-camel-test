package me.nickpaul;

import javax.enterprise.context.ApplicationScoped;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sjms2.Sjms2Component;

@ApplicationScoped
public class SimpleRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL("tcp://localhost:61616");

    Sjms2Component component = new Sjms2Component();
    component.setConnectionFactory(activeMQConnectionFactory);
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