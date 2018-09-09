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
@EnableMongoRepositories(basePackages = "github.com.eriksen.proto.repository.proto", mongoTemplateRef = "protodbTemp")
public class MongoProto {

  @Bean("protodbConf")
  @ConfigurationProperties(prefix = "spring.data.mongodb.proto")
  public MongoProperties proto_config() {
    return new MongoProperties();
  }

  @Bean
  public MongoDbFactory proto_factory(@Qualifier("protodbConf") MongoProperties mongoProperties) {
    String uri = "mongodb://";

    if (proto_config().getUsername() != null && proto_config().getPassword() != null) {
      uri += proto_config().getUsername() + ':' + proto_config().getPassword().toString() + '@';
    }

    uri += proto_config().getHost() + ':' + proto_config().getPort() + '/'
        + proto_config().getDatabase();

    return new SimpleMongoDbFactory(new MongoClientURI(uri));
  }

  @Bean("protodbTemp")
  public MongoTemplate proto_template() {
    return new MongoTemplate(proto_factory(proto_config()));
  }
}