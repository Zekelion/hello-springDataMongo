# hello-springDataMongo
spring data mongo demo

## Dependencies

MongoDB ^v3.4.10

RocketMQ v4.3.0

## Usage

```
gradle clean build

//================ 
// start rocketMQ
//================

// NameServer
<rocketmq-dir>/distribution/target/apache-rocketmq/bin/mqnamesrv

// Broker
<rocketmq-dir>/distribution/target/apache-rocketmq/bin/mqbroker

java -jar build/libs/proto-0.0.1-SNAPSHOT.jar --spring.profiles.active=master

java -jar build/libs/proto-0.0.1-SNAPSHOT.jar --spring.profiles.active=slave
```