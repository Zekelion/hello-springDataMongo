# hello-springDataMongo
spring data mongo demo

## Dependencies

MongoDB ^v3.4.10

RocketMQ v4.3.0

## Usage

```
//================ 
// start rocketMQ
//================

// NameServer
<rocketmq-dir>/distribution/target/apache-rocketmq/bin/mqnamesrv

// Broker
<rocketmq-dir>/distribution/target/apache-rocketmq/bin/mqbroker -n localhost:9876 autoCreateTopicEnable=true

//===================================
// Start SpringBoot Rest Application
//===================================
gradle clean build

// bootRun
gradle master bootRun

gradle slave bootRun

// jar
java -jar build/libs/proto-0.0.1-SNAPSHOT.jar --spring.profiles.active=master

java -jar build/libs/proto-0.0.1-SNAPSHOT.jar --spring.profiles.active=slave

//==================
// Request Put API
//==================
curl -v -X PUT -H "Content-Type: application/json" -d '{"id": "5b951c7a958ff683fc496ce8","name":3}' http://localhost:9000/v1.0/customers // slave

curl -v -X PUT -H "Content-Type: application/json" -d '{"id": "5b951c7a958ff683fc496ce8","name":3}' http://localhost:8080/v1.0/customers // master
```