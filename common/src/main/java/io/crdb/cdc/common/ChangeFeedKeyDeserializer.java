package io.crdb.cdc.common;

import org.springframework.kafka.support.serializer.JsonDeserializer;

public class ChangeFeedKeyDeserializer extends JsonDeserializer<String[]> {
}
