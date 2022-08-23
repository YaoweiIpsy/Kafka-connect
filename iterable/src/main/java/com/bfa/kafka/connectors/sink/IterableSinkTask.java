package com.bfa.kafka.connectors.sink;

import java.util.Collection;
import java.util.Map;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

public class IterableSinkTask extends SinkTask {

  @Override
  public String version() {
    return AppInfoParser.getVersion();
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
