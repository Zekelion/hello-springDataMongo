package github.com.eriksen.proto.controller;

import java.util.List;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.com.eriksen.proto.model.Visitor;
import github.com.eriksen.proto.repository.monitor.VisitorRepo;

/**
 * VisitorC
 */
@RestController
public class VisitorC {

  @Autowired
  private VisitorRepo visitorRepo;

  @PostMapping("/v1.0/visitors")
  public Visitor createVisitor(@RequestBody Visitor visitor) {
    Visitor result = visitorRepo.insert(visitor);
    return result;
  }

  @GetMapping("/v1.0/visitors/customer")
  public List<Visitor> getVisitorByCustomer(@RequestParam(value = "cId", required = true) String customer) {
    List<Visitor> result = visitorRepo.findAllByCustomer(new ObjectId(customer));
    return result;
  }

  @GetMapping("/v1.0/visitors/stats")
  public List<Object> statsVisitor() {
    List<Object> result = visitorRepo.statsMerchantVisitor();
    return result;
  }

  @GetMapping("/v1.0/visitors")
  public Page<Visitor> findVisitors(@RequestParam int page, @RequestParam int size) {
    Sort sort = new Sort(Direction.DESC, "createdTime");
    Pageable pages = PageRequest.of(page, size, sort);
    Page<Visitor> result = visitorRepo.findAll(pages);
    return result;
  }

  @GetMapping("/v1.0/visitors/createdTime")
  public List<Visitor> getVisitorByCreatedTime(
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") Date startTime,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") Date endTime) {
    List<Visitor> result = visitorRepo.findAllBetweenCreatedTime(startTime, endTime);
    return result;
  }
}