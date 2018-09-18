package github.com.eriksen.proto.messages;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * TransactionListenerImpl
 */
public class TransactionListenerImpl implements TransactionListener {
  private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

  @Override
  public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
    if (arg instanceof ProceedingJoinPoint){
      try {
        Method method = arg.getClass().getMethod("proceed", new Class[] {});
        method.invoke(arg, new Object[]{});
        localTrans.put(msg.getTransactionId(), 1);
        return LocalTransactionState.COMMIT_MESSAGE;
      } catch (Exception e) {
        System.out.println("Failed to process jointPoint" + e);
        localTrans.put(msg.getTransactionId(), 2);
        return LocalTransactionState.ROLLBACK_MESSAGE;
      }
    }

    return LocalTransactionState.UNKNOW;
  }

  @Override
  public LocalTransactionState checkLocalTransaction(MessageExt msg) {
    Integer status = localTrans.get(msg.getTransactionId());
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