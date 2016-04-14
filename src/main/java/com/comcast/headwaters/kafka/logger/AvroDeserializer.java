package com.comcast.headwaters.kafka.logger;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.comcast.headwaters.schema.registry.SchemaRegistryClient;
import com.comcast.headwaters.serde.AvroSerDe;
import com.comcast.headwaters.serde.AvroSerDeBuilder;
import com.google.common.base.Optional;

public class AvroDeserializer implements Deserializer<String> {
	private AvroSerDe deserializer = null;
	private boolean prettyPrint;
	private static final Logger LOG = LoggerFactory.getLogger(AvroDeserializer.class);

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> config, boolean isKey) {
		if (isKey) {
			return;
		}
		try {
			final String prettyPrintStr = (String) config.get(KafkaLoggerConfiguration.PRETTY_PRINT_OPTION);
			prettyPrint = prettyPrintStr == null ? KafkaLoggerConfiguration.PRETTY_PRINT_DEFAULT
					: Boolean.getBoolean(prettyPrintStr.trim());

			final AvroSerDeBuilder builder = new AvroSerDeBuilder();
			final String registry = (String) config.get(KafkaLoggerConfiguration.SCHEMA_REGISTRY_OPTION);
			if (registry != null) {
				final String topic = (String) config.get(KafkaLoggerConfiguration.TOPIC_OPTION);
				final Optional<Schema> schema = new SchemaRegistryClient(registry).getSchema(topic);
				if (schema.isPresent()) {
					builder.setSchema(schema.get());
					//deserializer = builder.build();
				} else {
					throw new IllegalArgumentException(
							"Schema not found for topic '" + topic + "' in registry: " + registry);
				}
			} else {
				builder.addSchemas(((String) config.get(KafkaLoggerConfiguration.SCHEMAS_OPTION)).split(","));
				builder.className((String) config.get(KafkaLoggerConfiguration.EVENT_CLASS_OPTION));
			}
			deserializer = builder.build();
		} catch (Exception e) {
			LOG.info("Failed to create avro serde for " + config.get(KafkaLoggerConfiguration.TOPIC_OPTION));
			deserializer = null;
		}
	}

	@Override
	public String deserialize(String topic, byte[] data) {
		if (deserializer == null) {
			return bytesAsStr(data);
		}
		try {
			return deserializer.serializeToJson( data, prettyPrint);
		} catch (IOException e) {
			if (LOG.isInfoEnabled()) {
				final StringBuilder sb = new StringBuilder();
				sb.append("Error while consuming messages: ");
				sb.append(e.getMessage());
				if (data != null) {
					sb.append(". Base64-encoded message: ");
					sb.append(Base64.encodeBase64String(data));
				}
				LOG.info(sb.toString());

			}
			return null;

		}
	}
	
	private static String bytesAsStr(final byte[] data) {
	    if (data == null) {
	      return "null";
	    }
	    final String msgStr = new String(data);
	    if (StringUtils.isAsciiPrintable(msgStr)) {
	      // Deserializing as string
	      return "\"" + msgStr + "\"";
	    }
	    // Dumping byte array
	    return Hex.encodeHexString(data);
	  }
}