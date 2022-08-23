package com.bfa.kafka.test;

import java.nio.charset.Charset;
import java.nio.file.Paths;
import lombok.val;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.profiles.ProfileFile.Type;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;


public class KinesisTest {

  private final static StsAssumeRoleCredentialsProvider credentialsProvider = StsAssumeRoleCredentialsProvider.builder()
      .refreshRequest(
          AssumeRoleRequest.builder()
              .roleArn("arn:aws:iam::450096215204:role/PowerUserAccess")
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
      )
      .build();

  public static void main(String[] argv) throws Exception {
    val client = KinesisClient.builder().region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build();
    val describeRequest = DescribeStreamRequest.builder()
        .streamName("ipsy-priority-events")
        .build();
    val streamRes = client.describeStream(describeRequest);
    System.out.println(streamRes.streamDescription().shards());
    val iteratorRequest = GetShardIteratorRequest.builder()
        .shardId(streamRes.streamDescription().shards().get(0).shardId())
        .streamName("ipsy-priority-events")
        .shardIteratorType(ShardIteratorType.TRIM_HORIZON)
        .build();
    val iteratorResp = client.getShardIterator(iteratorRequest);
    val recordsRequest = GetRecordsRequest.builder()
        .limit(5)
        .shardIterator(iteratorResp.shardIterator())
        .build();
    val result = client.getRecords(recordsRequest);

    System.out.println(result.records().get(0).data().asString(Charset.defaultCharset()));

  }
}
