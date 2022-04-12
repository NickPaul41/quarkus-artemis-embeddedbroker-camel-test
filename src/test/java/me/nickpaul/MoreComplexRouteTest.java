package me.nickpaul;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(MyArtemisTestResource.class)
public class MoreComplexRouteTest {

  @Inject
  ProducerTemplate producerTemplate;

  @Inject
  CamelContext camelContext;

  @Inject
  MoreComplexRoute moreComplexRoute;

  MockEndpoint target;
  MockEndpoint error;
  MockEndpoint failure;

  @BeforeEach
  void beforeEach() throws Exception {
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


  // For a given endpoint add a mock address to listen to.
  void addListeningEndpointsToMockEndpoints() throws Exception {
    RouteBuilder mockEndpoints = new RouteBuilder() {
      @Override
      public void configure() {
        from(moreComplexRoute.getTargetUri()).routeId("Mock Target Route")
            .to("mock:" + moreComplexRoute.getTargetUri());

        from(moreComplexRoute.getErrorUri()).routeId("Mock Error Route")
            .to("mock:" + moreComplexRoute.getErrorUri());

        from(moreComplexRoute.getFailureUri()).routeId("Mock Failure Route")
            .to("mock:" + moreComplexRoute.getFailureUri());
      }
    };
    camelContext.addRoutes(mockEndpoints);
  }

  /**
   * Initialize the common MockEndpoints.
   */
  protected void initializeMockEndpoints() throws Exception {
    // Add the custom routes to the unitTest.
    addListeningEndpointsToMockEndpoints();

    target = getMockEndpoint(moreComplexRoute.getTargetUri());
    error = getMockEndpoint(moreComplexRoute.getErrorUri());
    failure = getMockEndpoint(moreComplexRoute.getFailureUri());

    target.setExpectedCount(0);
    error.expectedMessageCount(0);
    failure.expectedMessageCount(0);
  }

  protected MockEndpoint getMockEndpoint(String uri) {
    return camelContext.getEndpoint(String.format("mock:%s", uri) ,MockEndpoint.class);
  }


  /**
   * Assert the mock endpoints.
   * @throws InterruptedException on error.
   */
  protected void assertMockEndpoints() throws InterruptedException {
    awaitMockEndpointReceived(error);
    awaitMockEndpointReceived(failure);
    awaitMockEndpointReceived(target);
  }

  /**
   * Check the mock endpoint for the expected message count. Timeout after 30 seconds.
   * @param mock the mock endpoint to be checked.
   * @throws InterruptedException on error.
   */
  protected void awaitMockEndpointReceived(MockEndpoint mock) throws InterruptedException {

    Awaitility.await()
        .pollInterval(50, TimeUnit.MILLISECONDS)
        .atMost(30, TimeUnit.SECONDS)
        .ignoreExceptions()
        .until(() -> mock.getReceivedCounter() >= mock.getExpectedCount());
    // Validate everything in the mock endpoint asserts correctly
    mock.assertIsSatisfied();
  }

}

