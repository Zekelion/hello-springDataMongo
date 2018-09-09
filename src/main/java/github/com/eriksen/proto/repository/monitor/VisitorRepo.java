package github.com.eriksen.proto.repository.monitor;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import github.com.eriksen.proto.model.Visitor;

/**
 * VisitorRepo
 */
public interface VisitorRepo extends MongoRepository<Visitor, String>, ManagedVisitorRepo{

  public List<Visitor> findAllByCustomer(ObjectId customer);

  @Query("{'createdTime': {'$gte': ?0, '$lte': ?1} }")
  public List<Visitor> findAllBetweenCreatedTime(Date startTime, Date endTime);
}