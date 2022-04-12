package me.nickpaul;

import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.awaitility.Awaitility;

public class MyTestSupport {
  MockEndpoint target;
  MockEndpoint error;
  MockEndpoint failure;
  MyBaseRoute route;

  @Inject
  CamelContext camelContext;

  void setRoute(MoreComplexRoute route) {
    this.route = route;
  }

  // For a given endpoint add a mock address to listen to.
  void addListeningEndpointsToMockEndpoints() throws Exception {
    RouteBuilder mockEndpoints = new RouteBuilder() {
      @Override
      public void configure() {
        from(route.getTargetUri()).routeId("Mock Target Route")
            .to("mock:" + route.getTargetUri());

        from(route.getErrorUri()).routeId("Mock Error Route")
            .to("mock:" + route.getErrorUri());

        from(route.getFailureUri()).routeId("Mock Failure Route")
            .to("mock:" + route.getFailureUri());
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

    target = getMockEndpoint(route.getTargetUri());
    error = getMockEndpoint(route.getErrorUri());
    failure = getMockEndpoint(route.getFailureUri());

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
