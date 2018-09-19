package github.com.eriksen.proto.messages;

import java.lang.reflect.Method;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * TransactionListenerImpl
 */
@Component
public class TransactionListenerImpl implements TransactionListener {
  @Autowired
  private RedisTemplate<String, Integer> redisTemplate;

  @Override
  public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
    if (arg instanceof ProceedingJoinPoint) {
      try {
        redisTemplate.opsForValue().set(msg.getTransactionId(), 0);
        Method method = arg.getClass().getMethod("proceed", new Class[] {});
        method.invoke(arg, new Object[] {});
        redisTemplate.opsForValue().set(msg.getTransactionId(), 1);
        return LocalTransactionState.UNKNOW;
      } catch (Exception e) {
        System.out.println("Failed to process jointPoint" + e);
        redisTemplate.opsForValue().set(msg.getTransactionId(), 2);
        return LocalTransactionState.ROLLBACK_MESSAGE;
      }
    }

    return LocalTransactionState.UNKNOW;
  }

  @Override
  public LocalTransactionState checkLocalTransaction(MessageExt msg) {
    Integer status = redisTemplate.opsForValue().get(msg.getTransactionId());
    if (null != status) {
      switch (status) {
      case 0:
        return LocalTransactionState.UNKNOW;
      case 1:
        return LocalTransactionState.COMMIT_MESSAGE;
      case 2:
        return LocalTransactionState.ROLLBACK_MESSAGE;
      }
    }
    System.out.println("MQ check msg, status:" + status);

    return LocalTransactionState.ROLLBACK_MESSAGE;
  }
}