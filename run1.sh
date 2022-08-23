./gradlew builZip

cd tmp
[[ -d "confluent" ]] || {
  curl -O "https://packages.confluent.io/archive/7.2/confluent-7.2.1.tar.gz"
  tar xvfz  confluent-7.2.1.tar.gz
  mv confluent-7.2.1 confluent
}

cd java
rm -rf lib
unzip ../../build/distributions/kafka-1.0-SNAPSHOT.zip
cd ..
./confluent/bin/connect-standalone ./etc/connect-avro-standalone.properties ./etc/console.sink.properties