package com.bfa.kafka.connector.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

public class FileStreamSourceTask extends SourceTask {
  private String id = UUID.randomUUID().toString();
  private int index = 0;
  private String topic;
  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {
    topic = props.get(FileStreamSourceConnector.TOPIC_CONFIG);
  }

  @Override
  public void stop() {

  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    String key,value;
    synchronized (this) {
      key = id + (index++);
      value = "value: " + index;
      wait(1000);
    }
    return Collections.singletonList(new SourceRecord(
        Collections.singletonMap("name","Timing"),
        Collections.singletonMap("index",key),
        topic,
        null,
        Schema.STRING_SCHEMA, key,
        Schema.STRING_SCHEMA, value,
        System.currentTimeMillis()
    ));
  }

}
