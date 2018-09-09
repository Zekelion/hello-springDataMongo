package github.com.eriksen.proto.repository.proto;

import org.springframework.data.mongodb.repository.MongoRepository;

import github.com.eriksen.proto.model.Customer;

/**
 * CustomerRepo
 */
public interface CustomerRepo extends MongoRepository<Customer, String>{


}