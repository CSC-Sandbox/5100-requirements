package edu.calpoly.lambdalab.aidenrodriguez;

import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/**
 * DynamoDB storage for EncryptMessageLambda results. The UUID {@code id} is
 * the String partition key in {@code csc5100-results}; resultJson preserves
 * the exact Lambda response for subsequent lookup.
 */
final class EncryptMessageStorage implements EncryptMessageLambda.Storage {
    private static final String OWNER = "aidenrodriguez";
    private static final String FEATURE = "encrypt";

    private final DynamoDbClient dynamo;
    private final String table;

    EncryptMessageStorage(String table) {
        this(DynamoDbClient.create(), table);
    }

    EncryptMessageStorage(DynamoDbClient dynamo, String table) {
        if (table == null || table.isBlank()) {
            throw new IllegalArgumentException("DynamoDB table is required");
        }
        this.dynamo = dynamo;
        this.table = table;
    }

    @Override
    public void save(String id, String json) {
        dynamo.putItem(PutItemRequest.builder().tableName(table)
                .item(Map.of(
                        "id", AttributeValue.fromS(id),
                        "owner", AttributeValue.fromS(OWNER),
                        "feature", AttributeValue.fromS(FEATURE),
                        "resultJson", AttributeValue.fromS(json)))
                .build());
    }

    @Override
    public String get(String id) {
        Map<String, AttributeValue> item = dynamo.getItem(GetItemRequest.builder()
                .tableName(table)
                .key(Map.of("id", AttributeValue.fromS(id)))
                .consistentRead(true)
                .build())
                .item();
        if (item == null || !item.containsKey("resultJson")) {
            return null;
        }
        if (!OWNER.equals(item.getOrDefault("owner", AttributeValue.fromS("")).s())) {
            return null;
        }
        return item.get("resultJson").s();
    }
}
