package com.bfa.kafka.connector.file;

import java.util.Collection;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

@Slf4j
public class ConsoleSinkTask extends SinkTask {

  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {

  }

  @Override
  public void put(Collection<SinkRecord> records) {
//    System.out.println(records.size());
    log.info("Got #{} records", records.size());
    val iterator = records.iterator();
    if (iterator.hasNext()) {
      val record = iterator.next();
      log.info("{} - {}: {}", record.topic(), record.key(), record.value());
    }
//    records.forEach((sinkRecord -> {
//      log.info(sinkRecord.topic()
//    }));
//    records.forEach(sinkRecord -> {
//      log.info(sinkRecord.value().toString());
//    });
  }

  @Override
  public void stop() {

  }
}
