# What does this connector do?

It reads from a kafka topic containing amplitude events in JSON format and sends them to Amplitude via the 
[Batch Upload API](https://developers.amplitude.com/#!Batch-Event-Upload).
The records in the kafka topic must have values in following format:

````json
{
      "user_id": "7abc9561-5d10-4c33-b2be-ddb0f2cfbe6e",
      "device_id": "3050fcd6-a572-414d-9d15-6c2751cb416e",
      ...
      "event_properties":  {...},
      "user_properties": {...},
      ...
      "event_type": "watch_tutorial"
}
````

# How do install / use? 

First build the distribution zip:
```
./gradle buildZip
cp ./distributions/kafka-amplitude-connector-*.zip <yourdestination>
```

Then you need a kafka worker to run the connector:

worker.properties
```
bootstrap.servers=xxxx.aivencloud.com:22638
format.class=io.confluent.connect.s3.format.bytearray.ByteArrayFormat
value.converter=org.apache.kafka.connect.converters.ByteArrayConverter
key.converter=org.apache.kafka.connect.converters.ByteArrayConverter
sasl.mechanism=PLAIN
security.protocol=SASL_SSL
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="xxxxxx" password="yyyyyy";
ssl.truststore.location=/Users/xxx/xxx/truststore.jks
ssl.truststore.password=yyyyyy
ssl.endpoint.identification.algorithm=
plugin.path=/Users/xxxx/confluent-5.3.1/share/kafka/plugins/
offset.storage.file.filename=/Users/xxxxx/confluent-5.3.1/share/kafka/plugins/kafka-connect-offsets.txt
group.id=zzzzzzzz
config.storage.topic=zzzzzz-config-storage
offset.storage.topic=zzzzzz-offset-storage
status.storage.topic=zzzzzz-status-storage


# Embedded consumer for sink connectors
consumer.security.protocol=SASL_SSL
consumer.ssl.truststore.location=/Users/..../truststore.jks
consumer.ssl.truststore.password=yyyyy
consumer.ssl.endpoint.identification.algorithm=
consumer.sasl.mechanism=PLAIN
consumer.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="xxxxx" password="yyyy";

```

connector2.properties
```
name=my-connector
connector.class=com.schibsted.AmplitudeSinkConnector
tasks.max=3
topics=amplitude
key.converter=org.apache.kafka.connect.storage.StringConverter
value.converter=org.apache.kafka.connect.json.JsonConverter
config.action.reload=none
errors.log.enable=true
errors.log.include.messages=true
amplitude_key=xxxxxxxx
errors.tolerance = all
schemas.enable=false
value.converter.schemas.enable=false
```

Then run a Kakfa Connect worker standalone with 

```
bin/connect-standalone worker.properties connector2.properties | tee log.txt
```


# TODO

* Implement a batching policy (like wait until there is 75 events or 1 minute has elapse whatever happens first.)