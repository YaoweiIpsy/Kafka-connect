./gradlew builZip

#jaws s3 cp build/distributions/kafka-1.0-SNAPSHOT.zip s3://ipsy-crm-staging/kafka/connectors/libs/bfa-connectors.zip
cd tmp/java
rm -rf lib
unzip ../../build/distributions/kafka-1.0-SNAPSHOT.zip
cd ..
~/confluent/bin/connect-standalone ./etc/connect-avro-standalone.properties ./etc/console.sink.properties