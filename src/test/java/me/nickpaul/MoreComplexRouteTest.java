package me.nickpaul;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(MyArtemisTestResource.class)
public class MoreComplexRouteTest extends MyTestSupport{

  @Inject
  ProducerTemplate producerTemplate;

  @Inject
  MoreComplexRoute moreComplexRoute;


  @BeforeEach
  void beforeEach() throws Exception {
    setRoute(moreComplexRoute);
    initializeMockEndpoints();
  }

  @Test
  void testDestination() throws InterruptedException {
    String message = "TESTING";

    target.setExpectedCount(1);
    target.expectedBodiesReceived(message);

    producerTemplate.sendBody(moreComplexRoute.getInboundUri() ,message);

    assertMockEndpoints();
  }

  @Test
  void testFail() throws InterruptedException {

    String message = "fail";

    failure.setExpectedCount(1);
    failure.expectedBodiesReceived(message);
    producerTemplate.sendBody(moreComplexRoute.getInboundUri(), message);

    assertMockEndpoints();
  }

  @Test
  void testError() throws InterruptedException {
    String message = "error";

    error.setExpectedCount(1);
    error.expectedBodiesReceived(message);
    producerTemplate.sendBody(moreComplexRoute.getInboundUri(), message);

    assertMockEndpoints();
  }
}

