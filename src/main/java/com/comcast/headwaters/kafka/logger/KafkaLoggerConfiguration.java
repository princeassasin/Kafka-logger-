/**
 * Copyright 2015, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */

package com.comcast.headwaters.kafka.logger;

import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the configuration for the Kafka-logger. It gets its data from a configuration file, command line arguments or
 * both. Command line arguments take precedence over the configuration file.
 */
final class KafkaLoggerConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(KafkaLoggerConfiguration.class);
  public static final String CONFIG_FILE_OPTION = "config";
  public static final String CONFIG_FILE_DESCRIPTION = "The path to the configuration file for the kafka-logger. Command line values ";

  // TODO topic could be a list, and even use regex
  public static final String TOPIC_OPTION = "topic";
  public static final String TOPIC_DESCRIPTION = "The name of the Kafka topic to read the messages from.";

  public static final String SCHEMA_REGISTRY_OPTION = "schema-registry";
  public static final String SCHEMA_REGISTRY_DESCRIPTION = "The URI of the schema registry";

  public static final String SCHEMAS_OPTION = "schemas";
  public static final String SCHEMAS_DESCRIPTION = "A comma delimited set of paths to files defining the Avro schema(s) used to encode the messages in the Kafka topic. If there is only one file, it does not need a comma after it.";

  public static final String EVENT_CLASS_OPTION = "event-class";
  public static final String EVENT_CLASS_DESCRIPTION = "The fully qualified class name (i.e. record schema) to use to deserialize the events";

  public static final String KAFKA_OPTIONS = "kafka";
  public static final String KAFKA_DESCRIPTION = "Properties passed verbatim to the Kafka consumer properties";

  public static final String THREADS_OPTION = "consumer-threads";
  public static final int THREADS_DEFAULT = 1;
  public static final String THREADS_DESCRIPTION = "The number of threads that should read from the topic. Default value: " + THREADS_DEFAULT;

  public static final String DISPLAY_KEY_OPTION = "display-key";
  public static final boolean DISPLAY_KEY_DEFAULT = false;
  public static final String DISPLAY_KEY_DESCRIPTION = "Defines whether or not to display the Kafka key (default: " + DISPLAY_KEY_DEFAULT + ")";

  public static final String PRETTY_PRINT_OPTION = "pretty-print";
  public static final boolean PRETTY_PRINT_DEFAULT = false;
  public static final String PRETTY_PRINT_DESCRIPTION = "Defines whether or not to display the Kafka message on multiple line";

  // TODO More config: behavior on error (log or drop), filters, ...

  private final Options options;
  private final Configuration configuration;

  public KafkaLoggerConfiguration() {
    configuration = new MapConfiguration(new HashMap<String, Object>());

    // Building command line options
    options = new Options();
    options.addOption(Option.builder().longOpt(CONFIG_FILE_OPTION).hasArg().argName("configuration-file").desc(CONFIG_FILE_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(TOPIC_OPTION).hasArg().argName("topic-name").desc(TOPIC_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(SCHEMA_REGISTRY_OPTION).hasArg().desc(SCHEMA_REGISTRY_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(SCHEMAS_OPTION).hasArg().desc(SCHEMAS_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(EVENT_CLASS_OPTION).hasArg().desc(EVENT_CLASS_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(THREADS_OPTION).hasArg().argName("number").desc(THREADS_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(DISPLAY_KEY_OPTION).hasArg().argName("true/false").desc(DISPLAY_KEY_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(PRETTY_PRINT_OPTION).hasArg().argName("true/false").desc(PRETTY_PRINT_DESCRIPTION).build());
    options.addOption(Option.builder().longOpt(KAFKA_OPTIONS).argName("property=value").hasArgs().valueSeparator().desc(KAFKA_DESCRIPTION).build());
  }

  public void printHelp() {
    new HelpFormatter().printHelp("kafka-logger", options);
  }

  /**
   * @param args
   *          Command line arguments
   * @return The newly built configuration for the Kafka-logger based on the command line argument provided
   */
  public boolean build(final String[] args) {
    // Parsing command line
    final CommandLineParser parser = new DefaultParser();
    CommandLine line = null;
    try {
      line = parser.parse(options, args);
    } catch (final ParseException e) {
      LOG.error("Invalid argument: " + args, e);
      return false;
    }

    // Look at configuration file first
    final String configFileName = line.getOptionValue(CONFIG_FILE_OPTION);
    if (configFileName != null) {
      try {
        ConfigurationUtils.append(new PropertiesConfiguration(configFileName), configuration);
      } catch (final Exception e) {
        return false;
      }
    }

    // Then look at command line options
    final CombinedConfiguration cmdLineConfig = new CombinedConfiguration();
    cmdLineConfig.addConfiguration(new MapConfiguration(line.getOptionProperties(KAFKA_OPTIONS)), "", KAFKA_OPTIONS);
    ConfigurationUtils.append(cmdLineConfig, configuration);
    for (final String option : new String[] { TOPIC_OPTION, SCHEMA_REGISTRY_OPTION, SCHEMAS_OPTION, EVENT_CLASS_OPTION, THREADS_OPTION, DISPLAY_KEY_OPTION, PRETTY_PRINT_OPTION }) {
      final String s = line.getOptionValue(option);
      if (s != null) {
        configuration.setProperty(option, s);
      }
    }

    // Finally, validating configuration
    if (getKafkaTopic() == null) {
      LOG.error("Topic configuration is missing!");
      return false;
    }
    if (getNumberOfThreads() <= 0) {
      LOG.error("Number of consumer threads is invalid!");
      return false;
    }
    return true;
  }

  /**
   * @return See {@link #TOPIC_DESCRIPTION}
   */
  public String getKafkaTopic() {
    return configuration.getString(TOPIC_OPTION);
  }

  /**
   * @return See {@link #SCHEMA_REGISTRY_DESCRIPTION}
   */
  public String getSchemaRegistry() {
    return configuration.getString(SCHEMA_REGISTRY_OPTION);
  }

  /**
   * @return See {@link #THREADS_DESCRIPTION}
   */
  public int getNumberOfThreads() {
    try {
      return configuration.getInt(THREADS_OPTION, THREADS_DEFAULT);
    } catch (final Exception e) {
      return -1;
    }
  }

  /**
   * @return See {@link #DISPLAY_KEY_DESCRIPTION}
   */
  public boolean displayKey() {
    try {
      return configuration.getBoolean(DISPLAY_KEY_OPTION, DISPLAY_KEY_DEFAULT);
    } catch (final Exception e) {
      return DISPLAY_KEY_DEFAULT;
    }
  }

  /**
   * @return See {@link #PRETTY_PRINT_DESCRIPTION}
   */
  public boolean prettyPrint() {
    try {
      return configuration.getBoolean(PRETTY_PRINT_OPTION, PRETTY_PRINT_DEFAULT);
    } catch (final Exception e) {
      return PRETTY_PRINT_DEFAULT;
    }
  }

  /**
   * @return The path (including the file name) to the file describing the Avro schema used to encode the messages in
   *         the Kafka topic.
   */
  public String[] getSchemaFiles() {
    return configuration.getStringArray(SCHEMAS_OPTION);
  }

  /**
   * @return See {@value #EVENT_CLASS_DESCRIPTION}
   */
  public String getEventClass() {
    return configuration.getString(EVENT_CLASS_OPTION);
  }

  /**
   * @return The configuration for the Kafka consumer
   */
  public Properties getConsumerConfig() {
    return ConfigurationConverter.getProperties(configuration);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "KafkaLoggerConfiguration:\n" + ConfigurationUtils.toString(configuration);
  }
}
