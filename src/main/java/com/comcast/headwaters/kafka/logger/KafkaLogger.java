/**
 * Copyright 2015, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.comcast.headwaters.kafka.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.comcast.headwaters.serde.AvroSerDeException;

/**
 * Main class and entry point for the kafka-logger utility
 */
public final class KafkaLogger {
  private final KafkaLoggerConfiguration config;
  private final List<KafkaMessageConsumer> consumers = new ArrayList<>();
  private ExecutorService executor;
  private final AtomicBoolean terminate;

  public KafkaLogger(final KafkaLoggerConfiguration configuration) {
    config = configuration;
    terminate = new AtomicBoolean(false);
  }

  /**
   * @throws AvroSerDeException
   *           If building the Avro deserializer failed
   */
  public void start() throws AvroSerDeException {
    terminate.set(false);

    executor = Executors.newFixedThreadPool(config.getNumberOfThreads());

    // Submitting the consumers
    for (int i = 0; i < config.getNumberOfThreads(); ++i) {
    	final KafkaMessageConsumer consumer = new KafkaMessageConsumer(terminate, config);
    	consumers.add(consumer);
      executor.submit(consumer);
    }
  }

  public void stop() {
    terminate.set(true);
    for (KafkaMessageConsumer consumer : consumers) {
    	consumer.stop();
    }
    if (executor != null) {
      executor.shutdown();
    }
  }

  /**
   * @param args
   *          Command line arguments
   * @throws Exception
   *           If starting the Kafka-logger failed
   */
  public static void main(final String[] args) throws Exception {
    // kafka-logger configuration
    final KafkaLoggerConfiguration configuration = new KafkaLoggerConfiguration();
    if (!configuration.build(args)) {
      configuration.printHelp();
      return;
    }

    final KafkaLogger kafkaLogger = new KafkaLogger(configuration);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        kafkaLogger.stop();
      }
    });
   kafkaLogger.start();
  }
}
