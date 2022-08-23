package com.bfa.kafka.connectors.sink;

import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

public class IterableSinkConnector extends SinkConnector {

  @Override
  public void start(Map<String, String> props) {

  }

  @Override
  public Class<? extends Task> taskClass() {
    return IterableSinkTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    return null;
  }

  @Override
  public void stop() {

  }

  @Override
  public ConfigDef config() {
    return null;
  }

  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }
}