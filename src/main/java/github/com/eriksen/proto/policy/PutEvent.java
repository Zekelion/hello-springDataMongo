package github.com.eriksen.proto.policy;

import java.lang.reflect.Method;

import org.apache.rocketmq.client.producer.SendResult;
// import org.apache.rocketmq.client.producer.DefaultMQProducer;
// import org.apache.rocketmq.client.producer.SendCallback;
// import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PutEvent
 */
@Component
@Aspect
public class PutEvent {

  @Pointcut("execution(public * github.com.eriksen.proto.repository..*.save(..))")
  public void save() {

  }

  // @Autowired
  // private DefaultMQProducer defaultMQProducer;

  @Autowired
  private TransactionMQProducer transactionMQProducer;

  @Around("save()")
  public void around(ProceedingJoinPoint pjp) throws Throwable {
    try {
      // Before
      Method method = pjp.getClass().getMethod("getId", new Class[] {});
      String id = method.invoke(pjp, new Object[] {}).toString();
      Message msg = new Message("Proto_db", "modify", id, pjp.toString().getBytes());

      // Transactional msg
      SendResult sendResult = transactionMQProducer.sendMessageInTransaction(msg, pjp);

      // Invoke by local transaction
      // pjp.proceed();

      // After
      System.out.println(sendResult);

      // Synchronously
      // SendResult sendResult = defaultMQProducer.send(msg);
      // System.out.println("Save intercepter" + sendResult);

      // Asynchronously
      // defaultMQProducer.send(msg, new SendCallback() {

      // @Override
      // public void onSuccess(SendResult sendResult) {
      // System.out.println(sendResult);
      // }

      // @Override
      // public void onException(Throwable e) {
      // e.printStackTrace();
      // }
      // });

      return;
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
  }
}