package github.com.eriksen.proto.policy;

import java.util.Date;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * Logger
 */
@Component // 组件，该类纳入到bean中
@Aspect // 定义切面
public class Logger {
  @Pointcut("execution(public * github.com.eriksen.proto.repository..*.*(..))")
  public void repositoryLogger() {
  }

  @Before("repositoryLogger()")
  public void preRetrieve() {
    System.out.println("[Pre]" + new Date());
  }

  @After("repositoryLogger()")
  public void postRetrieve() {
    System.out.println("[Post]" + new Date());
  }
}