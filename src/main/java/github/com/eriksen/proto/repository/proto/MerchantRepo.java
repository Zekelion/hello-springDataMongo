package github.com.eriksen.proto.repository.proto;

import org.springframework.data.mongodb.repository.MongoRepository;

import github.com.eriksen.proto.model.Merchant;

/**
 * MerchantRepo
 */
public interface MerchantRepo extends MongoRepository<Merchant, String>{

  
}