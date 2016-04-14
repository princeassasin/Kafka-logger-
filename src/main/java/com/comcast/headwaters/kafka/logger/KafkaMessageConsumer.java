/**
 * Copyright 2015, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.comcast.headwaters.kafka.logger;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class consumes one stream from Kafka
 */
public class KafkaMessageConsumer implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(KafkaMessageConsumer.class);
	private final KafkaConsumer<String, String> consumer;
	private final AtomicBoolean terminate;
	private final KafkaLoggerConfiguration config;

	public KafkaMessageConsumer(final AtomicBoolean shouldTerminate, final KafkaLoggerConfiguration config) {
		consumer = new KafkaConsumer<>(config.getConsumerConfig());
		this.terminate = shouldTerminate;
		this.config = config;
	}

	public void stop() {
		consumer.close();
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		consumer.subscribe(Arrays.asList(config.getKafkaTopic()));
		do {
			try {
				// Awaiting next message availability
				final ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
				for (ConsumerRecord<String, String> record : records) {
					final StringBuilder sb = new StringBuilder();
					if (config.displayKey()) {
						sb.append("Key: ");
						sb.append(record.key());
						sb.append("; Message: ");
					}
					sb.append(record.value());
					if (LOG.isInfoEnabled()) {
						LOG.info(sb.toString());
					}
				}
			} catch (final Exception e) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Error while consuming messages: ", e);
				}
			}
		} while (!terminate.get());
	}

}