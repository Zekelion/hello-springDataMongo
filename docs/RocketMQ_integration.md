## RocketMQ Integration

### Preface

> Why RocketMQ

借助rocketMQ提供的分布式事务的特性，可以帮助我们轻松实现在非传统场景(RDBMS)下的事务问题/一致性问题，e.g., 持久层更新后的缓存与持久层的一致性问题

![IMAGE](./images/A55CCBDAC2BD0AA824BF4D29CE979E25.jpg)

___

### RockerMQ

**Core Concept**

* A typical message system model
  ![IMAGE](./images/67B9DA9298BDC504777FE97907C86560.jpg)
  we can dig deeper into some topics about messaging system design:
  * Consumer Concurrency
  * Consumer Hot Issues
  * Consumer Load Balance
  * Message Router
  * Connection Multiplex
  * Canary Deployments // 金丝雀部署，部分发布
  
* Producer
  send messages to brokers
  sending paradigms
    * synchronous
    * asynchronous
    * one-way // 单方面的

* Producer Group
  Producers of the same role are grouped together. 当原有的生产者在transaction后crash掉，在同生产者组内的另一个生产者实例将会与中介者进行联系(contact)并继续提交(commit)或回滚(roll back)事务
  > Warning
  鉴于现有的producer足以应对发送消息，每个producer group只允许一个实例存在以避免冗余

* Consumer
  pulls messages from brokers and feeds them into application. two types of consumer are provided:
  * PullConsumer
  actively pulls messages from brokers. Once batches of msgs are pulled, user application initiates consuming process.
  * PushConsumer
  includes message pulling, consuming progress and maintaining other work inside,leaving a callback interface to end user to implement which will be executed on msg arrival.

* Consumer Group
  consumers of exactly same role are grouped together. It is a great concept with which achieving goals of load-balance (负载均衡) and fault-tolerance (容错), in terms of msg consuming, is super easy.
  > Warning
  consumer instances of a consumer group must have exactly the same topic subscription(s).

* Broker
  It receives messages sent from producers, store them and prepare to handle pull requests from consumers. It also stores message related meta data, including consumer groups, consuming progress offsets and topic/queue info.

* Name server
  serves as the routing information provider. Producer/Consumer clients look up topics to find the corresponding broker list.

* Topic
  A category in which producers deliver messages and consumers pull messages. Topics have very loose relationship with producers and consumers.
  ![IMAGE](./images/36E8018681C524C4A77BC8CF1F1A8FD2.jpg)

* Message
  information to be delivered.
  * topic
  * optional tag and extra k-v pairs, e.g., msg ref for lookup op
    * Tag
      i.e., sub-topic, provides extra filexibility to users. Msg with different purposes from the same business module may have the same topic and different tag. Tags would be helpful to keep your code clean and coherent, and tags also can facilitate the query system RocketMQ provides.
  
* Message Queue
  Topic is partitioned into one or more sub-topics

* Message Model
  * Clustering
  * Broadcasting
  
* Message Order
  the order for consume message
  * Orderly
    messages are consumed the same order they are sent by producers for each msg queue. If you are dealing with scenario that global order is mandatory (强制的), make sure the topic you use has only one msg queue.

    > Warn: If consuming orderly is specified, the maximum concurrency of msg consuming is the number of msg queues subscribed by the consumer group
    
  * Concurrently
    maximum concurrency of msg consuming is only limited by thread pool specified for eaxh consumer client.

    > Warn: Msg order is no longer guaranteed in this mode
___

**Architecture**

![IMAGE](./images/D8EB7459560DF068140B0FB6B70D570D.jpg)

* NameServer Cluster
  Name Servers provide lightweight service discovery and routing. Each Name Server records full routing information, provides corresponding reading and weiting service, and supports fast storage expansion.

* NameServer
  * Broker Management
    accepts the register from Broker cluster and provides heartbeat mechanism to check whether a broker is alive
  * Routing Management
    each NameServer will hold whole routing info about the broker cluster and the queue info for clients query

  > How do clients find NameServer address?
  
  * Programmtic Way, like `producer.setNamesrvAddr("ip:port")`
  * Java Options, use `rocketmq.namesrv.addr`
  * Environment Variable, use `NAMESRV_ADDR`
  * HTTP Endpoint
  
* Broker Cluster
  Brokers take care of msg storage by providing lightweight TOPIC and QUEUE mechanisms. 
  
  They support the Push and Pull model, contains fault tolerance mechanism (2 copies or 3 copies)

  Provides strong padding of peaks and capacity of accumulating hundreds of billion messages in their original time order.
  
  In addition, Brokers provides disaster recovery, rich metrics statistics, and alert mechanisms (all lacks in traditional messaging systems)

* Broker Server
  responsible for message store and delivery, message query, HA guarantee..., serval important sub modules:

  * Remoting Module
    the entry of broker, handles the request from clients
  * Client Manager
    manages the clients (Producer/Consumer) and maintains topic subscription of consumer
  * Store Service
    provides simple APIs to store or query msg in physical disk
  * HA Service
    provides data sync feature between master broker and slave broker
  * Index Service
    builds index for messages by specified key and provides quick message query

  ![IMAGE](./images/10B42C029B8E943B757F3473AF0A1232.jpg)
  
* Producer Cluster
  Distributed Producers send messages to the Broker cluster through multiple load balancing modes. The sending processes support fast failure and have low latency

* Consumer Cluster
  Consumers support distributed deployment in the Push and Pull model as well. It also supports cluster consumption and message broadcasting. It provides real-time message subscription mechanism and can meet most consumer requirements.
___

**Transcation**

* transactional message
  can be tought of as a two-phase commit message implementation to ensure eventual consistency in distributed system. It ensures that the execution of local transcation and the sending of message can be performed atomically

* Half/Prepare Message
  msg is successfully sent to the MQ server, but the server did not receive the second ack of msg from the producer, then the msg is marked as 'temporarily undeliverable'

* Message Status Check
  When MQ server finds that a message remains a half message for a long time, it will send a request to the message producer, checking the final status of the message (Commit or Rollback)

* Usage Constraint
  * have no schedule and batch support
  * default check maximum for a single msg: 15, if not sent success, the broker will discard this msg and print an error log.
  * check period `transactionMsgTimeout`
  * a transactional msg maybe checked or consumed more than once
  * producer IDs of msg cannot be shared with producer IDs of other types of msg. Allow backward queries, MQ Server query clients by their producer IDs
  
* Application

* Transactional status
  * TransactionStatus.CommitTransaction // 1
  * TransactionStatus.RollbackTransaction // 2
  * TransactionStatus.Unknown // 0
    MQ is needed to check back to determine the status

* Send transactional producer
  * Create the transactional producer
    After exexutiing the local transaction, you need to reply to MQ according to the execution result, and the reply status is above
  * Implement the TranscationListener interface
    * `executeLocalTransaction`
      execute local transaction when send half msg succeed, it returns one of three transaction status as above
    * `checkLocalTransaction`
      check the local transaction status and repond to MQ check requests. Also return transaction status

![IMAGE](./images/F34FE5AF2EEA67EE6CC7DCE27A5A12CE.jpg)
___

**Load Balance**

* Broker
  Broker以group为单位提供服务，在一个group里区分master和slave，slave从master同步数据；经由nameServer暴露给client后，可以抽象为一个个topic的路由信息，细化为msg queue进行发送。对于client来说，分布在不同broker group的msg queue构成了一个服务集群，这样一来，压力分摊到了不同的queue上，自然也分摊到了不同的broker group上

* Producer
  每个实例在发送消息的时候，默认会轮询所有的msg queue进行发送，达到消息平均落在不同的queue上，进而发送到不同的broker上

  ![IMAGE](./images/D41A50A259E5D025610E0B0A545F7606.jpg)
  
* Consumer
  * cluster
    集群消费模式，每条消息会投递到订阅topic下的consumer group的一个实例上。这时使用pull的主动拉取方式，指定哪一条msg queue进行拉取
  
    每次实例的数量有变更，都会触发一次所有实例的负载均衡，按照queue的数量和实例的数量平均分配queue给每个实例 (AllocateMessageQueueAveragely)
    
    > Warn: 集群模式下queue只能分配给一个实例，由于拉取消息是由consumer主动控制的，有可能导致同一个实例重复消费

___
### Appendix

![IMAGE](./images/3136E0288D2A36AF5F57C4A6BBAF361E.jpg)

