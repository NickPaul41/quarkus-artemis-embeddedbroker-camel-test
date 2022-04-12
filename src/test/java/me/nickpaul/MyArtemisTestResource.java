package me.nickpaul;

import static org.awaitility.Awaitility.await;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.commons.io.FileUtils;

public class MyArtemisTestResource implements QuarkusTestResourceLifecycleManager {

  private EmbeddedActiveMQ embedded;
  private static final String DATA_DIRECTORY = "./target/ucla-artemis";

  @Override
  public Map<String, String> start() {
    try {
      AddressSettings addressSettings = new AddressSettings()
          .setAutoCreateAddresses(true)
          .setAutoCreateQueues(true)
          .setAutoCreateDeadLetterResources(true)
          .setDeadLetterAddress(new SimpleString("ucla-dlq"))
          .setAutoCreateExpiryResources(true)
          .setExpiryAddress(new SimpleString("ucla-expiry"));

      Map<String, AddressSettings> addressSettingsMap = new HashMap<>();
      addressSettingsMap.put("#", addressSettings);


      FileUtils.deleteDirectory(Paths.get(DATA_DIRECTORY).toFile());
      embedded = new EmbeddedActiveMQ();
      Configuration configuration = new ConfigurationImpl()
          //.setPersistenceEnabled(false)
          .setJournalDirectory(DATA_DIRECTORY + "/journal")
          .setBindingsDirectory(DATA_DIRECTORY + "/bindings")
          .setLargeMessagesDirectory(DATA_DIRECTORY + "/large-message")
          .setPagingDirectory(DATA_DIRECTORY + "/paging")
          .setAddressesSettings(addressSettingsMap)
          .setSecurityEnabled(false)
          .addAcceptorConfiguration("vm", "vm://0")
          .addAcceptorConfiguration("amq", "tcp://localhost:61616")
          .addConnectorConfiguration("amq", new TransportConfiguration(NettyConnectorFactory.class.getName()));



      embedded.setConfiguration(configuration);

      embedded.start();

      await()
          .pollInterval(50, TimeUnit.MILLISECONDS)
          .atMost(30, TimeUnit.SECONDS)
          .until(() -> embedded.getActiveMQServer().isActive()  && embedded.getActiveMQServer().isStarted());


      System.out.println("Artemis server started");
    } catch (Exception exception) {
      throw new RuntimeException("Could not start embedded ActiveMQ server", exception);
    }
    return Collections.emptyMap();
  }

  @Override
  public void stop() {
    if (embedded == null) {
      return;
    }
    try {
      embedded.stop();
    } catch (Exception exception) {
      throw new RuntimeException("Could not stop embedded ActiveMQ server", exception);
    }
  }
}

