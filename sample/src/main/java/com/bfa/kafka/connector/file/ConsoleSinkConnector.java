package com.bfa.kafka.connector.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

public class ConsoleSinkConnector extends SinkConnector {

  @Override
  public void start(Map<String, String> props) {

  }

  @Override
  public Class<? extends Task> taskClass() {
    return ConsoleSinkTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    return Collections.singletonList(new HashMap<>());
  }

  @Override
  public void stop() {

  }

  @Override
  public ConfigDef config() {
    return new ConfigDef();
  }

  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }
}
