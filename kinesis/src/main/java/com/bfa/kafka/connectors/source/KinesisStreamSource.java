package com.bfa.kafka.connectors.source;

import com.bfa.kafka.utils.ConfigDefUtils;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.AppInfoParser;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile.Type;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.utils.ImmutableMap;

@Slf4j
public class KinesisStreamSource extends SourceConnector {
  public static AwsCredentialsProvider getCredentialsProvider(Map<String, String> props) {
    String roleArn = props.getOrDefault("roleArn", null);
    log.info("=== {}", System.getenv("AWS_PROFILE"));
    if (roleArn != null) {
      log.info("----------------- {}", roleArn);
      return StsAssumeRoleCredentialsProvider.builder()
          .refreshRequest(
              AssumeRoleRequest.builder()
                  .roleArn(roleArn)
                  .roleSessionName("test")
                  .build())
          .stsClient(
              StsClient.builder()
                  .credentialsProvider(
                      ProfileCredentialsProvider.builder()
                          .profileFile(builder -> builder
                              .content(Paths.get("/Users/yaowei/.ipsy/jaws/creds"))
                              .type(Type.CREDENTIALS))
                          .build()
                  )
                  .build()
          ).build();
    }
    return DefaultCredentialsProvider.create();
  }
  public final static ConfigDef configDef = ConfigDefUtils.generate(KinesisStreamSourceConfig.class);

  private KinesisStreamSourceConfig sourceConfig;
  private KinesisClient client;
  private Map<String, String> props;
  @Override
  public void start(Map<String, String> props) {
    sourceConfig = ConfigDefUtils.assignProps(props, KinesisStreamSourceConfig.class);
    client = KinesisClient.builder().region(Region.of(sourceConfig.getRegion()))
        .credentialsProvider(getCredentialsProvider(props))
        .build();
    this.props = props;
  }

  @Override
  public Class<? extends Task> taskClass() {
    log.info("000000000000000000000");
    return KinesisStreamSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    val configs = new ArrayList<Map<String, String>>();
    val request = DescribeStreamRequest.builder()
        .streamName(sourceConfig.getStreamName())
        .build();
    log.info("=============== {}", sourceConfig.getStreamName());
    val response = client.describeStream(request);
    System.out.printf("===== %s", response.toString());
    for (val shard : response.streamDescription().shards()) {
      val config = new HashMap<>(props);
      config.put("shard_id", shard.shardId());
      configs.add(config);
    }
    return configs;
  }

  @Override
  public void stop() {
    client.close();
  }

  @Override
  public ConfigDef config() {
    return configDef;
  }

  @Override
  public String version() {
    return AppInfoParser.getVersion();
  }

  public static void main(String[] argv) throws Exception {
    System.out.println(configDef);
    val source = new KinesisStreamSource();
    source.start(ImmutableMap.of("streamName", "ipsy-priority-events", "role_arn", "arn:aws:iam::450096215204:role/PowerUserAccess"));
//    source.sourceConfig = KinesisStreamSourceConfig.builder()
//            .streamName("ipsy-priority-events")
//                .build();
    source.taskConfigs(1);
  }
}
