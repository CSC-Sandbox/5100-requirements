package edu.calpoly.lambdalab.paulmotter;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class RobotDistanceMessagingAdapter implements RobotDistanceLambda.MessagingAdapter {

    private final SnsClient sns = SnsClient.create();
    private final SqsClient sqs = SqsClient.create();
    private final String topicArn;
    private final String queueUrl;

    RobotDistanceMessagingAdapter(String SnsTopicARN, String SqsURL){
        topicArn = SnsTopicARN;
        queueUrl = SqsURL;
    }

    @Override
    public void publish(String json) {
        sns.publish(PublishRequest.builder().topicArn(topicArn).message(json).build());
        sqs.sendMessage(SendMessageRequest.builder().queueUrl(queueUrl)
            .messageBody(json).build());
    }
    
}
