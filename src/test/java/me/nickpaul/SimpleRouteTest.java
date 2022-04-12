package me.nickpaul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(MyArtemisTestResource.class)
public class SimpleRouteTest {

  @Inject
  ProducerTemplate producerTemplate;

  @Inject
  ConsumerTemplate consumerTemplate;

  @Test
  void testDestination() {
    producerTemplate.sendBody("my-sjms2:inbound", "TESTING");

    String body = consumerTemplate.receiveBody("my-sjms2:target", 5000, String.class);
    assertEquals(body, "TESTING");
  }

  @Test
  void testFail() {
    producerTemplate.sendBody("my-sjms2:inbound", "fail");

    String body = consumerTemplate.receiveBody("my-sjms2:fail", 5000, String.class);
    assertEquals(body, "fail");
  }

  @Test
  void testError() {
    producerTemplate.sendBody("my-sjms2:inbound", "error");

    String body = consumerTemplate.receiveBody("my-sjms2:error", 5000, String.class);
    assertEquals(body, "error");
  }

}
