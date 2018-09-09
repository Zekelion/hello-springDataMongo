package github.com.eriksen.proto.repository.monitor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import github.com.eriksen.proto.model.Visitor;
import github.com.eriksen.proto.repository.monitor.ManagedVisitorRepo;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * ManagedVisitorRepoImpl
 */
public class ManagedVisitorRepoImpl implements ManagedVisitorRepo{

  @Autowired
  @Qualifier("monitordbTemp")
  private MongoTemplate mongoTemp;

  @Override
  public List<Object> statsMerchantVisitor() {
    Aggregation pipeline = newAggregation(
      group("customer").push("$$ROOT").as("visitors").count().as("sum"),
      sort(Direction.DESC, "sum")
    );
    
    List<Object> result = mongoTemp.aggregate(pipeline, Visitor.class,Object.class).getMappedResults();
    return result;
  }
}