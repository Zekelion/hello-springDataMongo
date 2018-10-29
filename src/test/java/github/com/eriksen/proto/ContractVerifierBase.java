package github.com.eriksen.proto;

import java.util.Optional;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import github.com.eriksen.proto.controller.CustomerC;
import github.com.eriksen.proto.model.Customer;
import github.com.eriksen.proto.repository.proto.CustomerRepo;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

/**
 * ContractVerifierBase
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class ContractVerifierBase {

  @InjectMocks
  CustomerC customerC;

  @Mock
  CustomerRepo customerRepo;

  @Before
  public void setup() throws Exception {
    RestAssuredMockMvc.standaloneSetup(customerC);
    Customer customer = new Customer();
    customer.setId("5b951c7a958ff683fc496ce8");
    customer.setName("3");

    Mockito.when(customerRepo.findById("5b951c7a958ff683fc496ce8")).thenReturn(Optional.of(customer));
  }
}