package github.com.eriksen.proto.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="customer")
public class Customer {
  @Id
  private String id;

  @Indexed
  private String name;

  @Version
  private Number version;
}