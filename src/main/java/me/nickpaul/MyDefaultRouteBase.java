package me.nickpaul;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import me.nickpaul.config.JmsIn;
import me.nickpaul.config.JmsOut;
import me.nickpaul.exception.FailureExceptionBean;
import me.nickpaul.exception.ThrowTranslationExceptionBean;
import org.apache.camel.CamelContext;

@ApplicationScoped
public class MyDefaultRouteBase extends MyBaseRoute  {

  @Inject
  CamelContext camelContext;

  @Inject
  JmsIn jmsIn;

  @Inject
  JmsOut jmsOut;

  @Override
  public void configure() throws Exception {
    JmsUtils.addSjms2NamedComponent(camelContext, jmsIn);
    JmsUtils.addSjms2NamedComponent(camelContext, jmsOut);

    // Set up the inbound
    setInboundConfig(jmsIn);

    // Set up the outbound
    setTargetConfig(jmsOut);

    super.configure();

    // Based on the message send to Error, Audit or Destination Endpoints
    from(getInboundUri())
        .routeId("Complex Route")
        .log("ReceivedMessage: ${body}")
        .choice()
          .when(simple("${body} == 'error'"))
            .bean(ThrowTranslationExceptionBean.class)
          .when(simple("${body} == 'fail'"))
            .bean(FailureExceptionBean.class)
          .otherwise()
            .to(getTargetUri());


  }
}
