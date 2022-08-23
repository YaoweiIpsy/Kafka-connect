package com.bfa.kafka.connectors.source;

import com.bfa.kafka.utils.ConfigDefUtils;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.utils.ImmutableMap;

@Slf4j
public class KinesisStreamSourceTask extends SourceTask {
  public static final String SHARD_ID = "shard_id";
  public static final String SEQUENCE_NUMBER = "sequence_number";

  private KinesisClient client;
  private String shardId;
  private KinesisStreamSourceConfig config;

  private Map<String, Object> sourcePartition;
  private String nextShardIterator;

  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }

  @Override
  public void start(Map<String, String> props) {
    log.info("++++++++++++++++++++++++ {}",this.context.configs());
    log.info("task configuration =========================={}", props);
    shardId = props.get(SHARD_ID);
    config = ConfigDefUtils.assignProps(props, KinesisStreamSourceConfig.class);
    client = KinesisClient.builder().region(Region.US_EAST_1)
        .credentialsProvider(KinesisStreamSource.getCredentialsProvider(props))
        .build();
    sourcePartition = ImmutableMap.of(SHARD_ID, shardId);

    val lastOffset = context.offsetStorageReader().offset(sourcePartition);
    val requestBuilder = GetShardIteratorRequest.builder()
        .shardId(shardId)
        .streamName(config.getStreamName());
    if (lastOffset == null || lastOffset.isEmpty()) {
      requestBuilder.shardIteratorType(ShardIteratorType.fromValue(config.getShardIteratorType()));
    } else {
      val starting = (String) lastOffset.get(SEQUENCE_NUMBER);
      log.info("Start from shard({}) : {}", shardId, starting);
      requestBuilder
          .shardIteratorType(ShardIteratorType.AFTER_SEQUENCE_NUMBER)
          .startingSequenceNumber(starting);
    }
    val result = client.getShardIterator(requestBuilder.build());
    log.info("Shard Iterator: {}", result.shardIterator());
    nextShardIterator = result.shardIterator();
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    try{
//      log.info("--------------------- {}", nextShardIterator);
      val result = client.getRecords(GetRecordsRequest.builder()
          .limit(config.getRecordLimit())
          .shardIterator(nextShardIterator)
          .build());
      nextShardIterator = result.nextShardIterator();
//      log.info("----------------------------{}", nextShardIterator);
      val sourceRecords = result.records().stream().map(this::convert).collect(Collectors.toList());
      if (sourceRecords.isEmpty()) {
        log.info("empty records, sleeping {} ms", config.getEmptyRecordsBackoffMs());
        Thread.sleep(config.getThroughputExceededBackoffMs());
//        wait(config.getThroughputExceededBackoffMs());
      } else {
        log.info("Records size: {}", sourceRecords.size());
      }
      return sourceRecords;
    } catch (ProvisionedThroughputExceededException ex) {
      log.warn("Throughput exceeded sleeping {} ms", config.getThroughputExceededBackoffMs());
      Thread.sleep(config.getThroughputExceededBackoffMs());
      return Collections.emptyList();
    }

  }

  private SourceRecord convert(Record record) {
    val value = record.data().asString(StandardCharsets.UTF_8);
    val sourceOffset = ImmutableMap.of(SEQUENCE_NUMBER, record.sequenceNumber());
    return new SourceRecord(
        sourcePartition,
        sourceOffset,
        config.getTopic(),
        null,
        Schema.STRING_SCHEMA, "======",Schema.STRING_SCHEMA, value,
        record.approximateArrivalTimestamp().getEpochSecond());
  }
  @Override
  public void stop() {

  }
}
