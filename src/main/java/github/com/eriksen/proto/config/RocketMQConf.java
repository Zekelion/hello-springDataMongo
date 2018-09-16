package github.com.eriksen.proto.config;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import github.com.eriksen.proto.messages.TransactionListenerImpl;

/**
 * RocketMQ configuration
 */
@Configuration
public class RocketMQConf {

  @Bean
  public TransactionMQProducer transactionMQProducer() throws MQClientException {
    TransactionMQProducer producer = new TransactionMQProducer("proto_svc_trans_producer");
    TransactionListener transactionListener = new TransactionListenerImpl();
    ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), new ThreadFactory(){
    
      @Override
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("client-transaction-msg-check-thread");
        return thread;
      }
    });

    producer.setNamesrvAddr("localhost:9876");
    producer.setExecutorService(executorService);
    producer.setTransactionListener(transactionListener);

    producer.start();
    return producer;
  }

  @Bean
  public DefaultMQProducer defaultMQProducer() throws MQClientException {
    DefaultMQProducer producer = new DefaultMQProducer("proto_svc_producer");
    producer.setNamesrvAddr("localhost:9876");
    producer.start();
    return producer;
  }

  @Bean
  public DefaultMQPushConsumer defaultMQPushConsumer() throws MQClientException {
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("proto_svc_consumer");
    consumer.setNamesrvAddr("localhost:9876");
    consumer.subscribe("Proto_db", "*");

    consumer.registerMessageListener(new MessageListenerConcurrently(){
    
      @Override
      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      }
    });

    consumer.start();
    return consumer;
  }
}