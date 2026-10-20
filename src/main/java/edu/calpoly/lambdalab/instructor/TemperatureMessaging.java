package edu.calpoly.lambdalab.instructor;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/** Sends the same event independently to SNS and SQS; neither depends on the other. */
final class TemperatureMessaging implements TemperatureLambda.Messaging {
  private final SnsClient sns = SnsClient.create();
  private final SqsClient sqs = SqsClient.create();
  private final String topicArn;
  private final String queueUrl;

  TemperatureMessaging(String topicArn, String queueUrl) {
    this.topicArn = topicArn;
    this.queueUrl = queueUrl;
  }

  @Override
  public void publish(String json) {
    sns.publish(PublishRequest.builder().topicArn(topicArn).message(json).build());
    sqs.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl)
        .messageBody(json).build());
  }
}
