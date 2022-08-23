package com.bfa.kafka.connector.sink;

import java.util.Collection;
import java.util.Map;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

public class LambdaConnectorTask extends SinkTask {

  @Override
  public String version() {
    return null;
  }

  @Override
  public void start(Map<String, String> props) {

  }

  @Override
  public void put(Collection<SinkRecord> records) {

  }

  @Override
  public void stop() {

  }
}
