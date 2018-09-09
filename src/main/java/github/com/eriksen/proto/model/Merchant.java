package github.com.eriksen.proto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection="merchant")
public class Merchant {
  @Id
  private String id;

  @Indexed
  private String name;
}