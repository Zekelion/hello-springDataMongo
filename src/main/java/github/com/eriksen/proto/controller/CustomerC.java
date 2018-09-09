package github.com.eriksen.proto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import github.com.eriksen.proto.model.Customer;
import github.com.eriksen.proto.repository.proto.CustomerRepo;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * CustomerC
 */
@RestController
public class CustomerC {

  @Autowired
  private CustomerRepo customerRepo;

  @GetMapping("/v1.0/customers")
  public Page<Customer> findCustomers() {
    Sort sort = new Sort(Direction.DESC, "name");
    Pageable page = PageRequest.of(0, 10, sort);
    Page<Customer> result = customerRepo.findAll(page);
    return result;
  }

  @PostMapping("/v1.0/customers")
  public Customer createCustomer(@RequestBody Customer customer) {
    Customer result = customerRepo.insert(customer);
    return result;
  }

  @PutMapping("/v1.0/customers")
  public Customer putCustomer(@RequestBody Customer customer) {
    Customer result = customerRepo.save(customer);
    return result;
  }

  @GetMapping(value = "/v1.0/customer")
  public Customer getMethodName(@RequestParam(value = "id", required = true) String id) {
    Customer result = customerRepo.findById(id).get();
    return result;
  }

}