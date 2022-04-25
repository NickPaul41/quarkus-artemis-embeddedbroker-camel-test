package me.nickpaul;

import javax.enterprise.context.ApplicationScoped;
import me.nickpaul.exception.FailureExceptionBean;
import me.nickpaul.exception.ThrowTranslationExceptionBean;
import me.nickpaul.exception.TranslationException;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.sjms2.Sjms2Component;
import org.apache.camel.model.OnCompletionDefinition;
import org.apache.camel.model.OnExceptionDefinition;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;

@ApplicationScoped
public class MoreComplexRoute extends MyBaseRoute  {

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
    this.getCamelContext().addComponent(sjms2ComponentName, component);



    // Setup the exchange failure completion clause
    OnCompletionDefinition onFailure =
        onCompletion()
          .onFailureOnly()
          .toF("%s?transacted=false", failureUri)
          .log(LoggingLevel.TRACE,"Sending Message to Failure")
          .log(LoggingLevel.DEBUG, "${body}");

    // Set up the failure exception clause
    OnExceptionDefinition onTranslationExceptionDefinition =
        onException(TranslationException.class)
          .handled(true)
          .toF("%s?transacted=false", errorUri)
          .log(LoggingLevel.DEBUG, "Sending message to Error")
          .log(LoggingLevel.DEBUG, "${body}");


    // Based on the message send to Error, Audit or Destination Endpoints
    from(inboundUri)
        .routeId("Complex Route")
        .log("ReceivedMessage: ${body}")
        .choice()
          .when(simple("${body} == 'error'"))
            .bean(ThrowTranslationExceptionBean.class)
          .when(simple("${body} == 'fail'"))
            .bean(FailureExceptionBean.class)
          .otherwise()
            .to(targetUri);
  }
}
