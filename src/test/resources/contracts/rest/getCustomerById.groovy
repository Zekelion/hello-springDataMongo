package contracts.rest

import org.springframework.cloud.contract.spec.Contract

Contract.make {
  description "should return stats result"

  request {
    urlPath("/v1.0/customer") {
      queryParameters {
        parameter 'id': "5b951c7a958ff683fc496ce8"
      } 
    }
    
    method GET()
  }

  response {
    status 200
    headers {
      contentType applicationJson()
    }
    body '''
          {"id":"5b951c7a958ff683fc496ce8","name":"3"}
         '''
  }
}