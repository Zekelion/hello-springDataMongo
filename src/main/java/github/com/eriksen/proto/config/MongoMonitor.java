package github.com.eriksen.proto.config;

import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoProto configuration
 */
@Configuration
@EnableMongoRepositories(basePackages = "github.com.eriksen.proto.repository.monitor", mongoTemplateRef = "monitordbTemp")
public class MongoMonitor {

  @Bean("monitordbConf")
  @ConfigurationProperties(prefix = "spring.data.mongodb.monitor")
  public MongoProperties monitor_config() {
    return new MongoProperties();
  }

  @Bean
  public MongoDbFactory monitor_factory(@Qualifier("monitordbConf") MongoProperties mongoProperties) {
    String uri = "mongodb://";

    if (monitor_config().getUsername() != null && monitor_config().getPassword() != null) {
      uri += monitor_config().getUsername() + ':' + monitor_config().getPassword().toString() + '@';
    }

    uri += monitor_config().getHost() + ':' + monitor_config().getPort() + '/'
        + monitor_config().getDatabase();

    return new SimpleMongoDbFactory(new MongoClientURI(uri));
  }

  @Bean("monitordbTemp")
  public MongoTemplate monitor_template() {
    return new MongoTemplate(monitor_factory(monitor_config()));
  }
}