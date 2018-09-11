package github.com.eriksen.proto.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document(collection = "visitor")
@CompoundIndexes({ @CompoundIndex(name = "mId_createdTime", def = "{'mId':1,'createdTime':-1}") })
public class Visitor {
  @Id
  private String id;

  // save as objectId
  private String customer;

  @Field("createdTime")
  private Date createdTime = new Date();

  private Date lastModTime = new Date();
}