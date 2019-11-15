package io.crdb.cdc.common;

import org.springframework.kafka.support.serializer.JsonDeserializer;

public class ChangeFeedDeserializer extends JsonDeserializer<ChangeFeed> {
}
