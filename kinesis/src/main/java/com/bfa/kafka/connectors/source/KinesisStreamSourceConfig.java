package com.bfa.kafka.connectors.source;

import com.bfa.kafka.utils.Config;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import lombok.Builder.Default;
@Jacksonized
@Getter
@Builder
public class KinesisStreamSourceConfig {
  @Config(required = true)
  private final String streamName;
  @Config(required = true)
  private final String topic;
  @Default
  private final String region = Region.US_EAST_1.id();
  @Default
  private final String shardIteratorType = ShardIteratorType.LATEST.toString();
  @Default
  private final int recordLimit = 1024;
  @Default
  private final long throughputExceededBackoffMs = 10_000;
  @Default
  private final long emptyRecordsBackoffMs = 1_000;
}
