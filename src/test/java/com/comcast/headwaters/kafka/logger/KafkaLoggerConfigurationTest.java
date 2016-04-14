/**
 * Copyright 2015, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.comcast.headwaters.kafka.logger;

public class KafkaLoggerConfigurationTest {
  //
  // private static final String TEST_GROUP = "group";
  // private static final String TEST_SCHEMA = "src/test/resources/viper-player-session.avsc";
  // private static final String TEST_THREADS = "123";
  // private static final int TEST_THREADS_INT = 123;
  // private static final int TEST_THREADS_DEFAULT = 1;
  // private static final String TEST_TOPIC = "topic";
  // private static final String TEST_ZK = "http://test.zookeeper.xcal.com:8888";
  //
  // private static final String SHORT_PREFIX = "-";
  // private static final String GROUP_SHORT = SHORT_PREFIX + KafkaLoggerOptions.GROUP_SHORT;
  // private static final String SCHEMA_SHORT = SHORT_PREFIX + KafkaLoggerOptions.SCHEMA_SHORT;
  // private static final String THREADS_SHORT = SHORT_PREFIX + KafkaLoggerOptions.THREADS_SHORT;
  // private static final String TOPIC_SHORT = SHORT_PREFIX + KafkaLoggerOptions.TOPIC_SHORT;
  // private static final String ZK_SHORT = SHORT_PREFIX + KafkaLoggerOptions.ZK_SHORT;
  //
  // private static final String LONG_PREFIX = "--";
  // private static final String GROUP_LONG = LONG_PREFIX + KafkaLoggerOptions.GROUP_LONG;
  // private static final String SCHEMA_LONG = LONG_PREFIX + KafkaLoggerOptions.SCHEMA_LONG;
  // private static final String THREADS_LONG = LONG_PREFIX + KafkaLoggerOptions.THREADS_LONG;
  // private static final String TOPIC_LONG = LONG_PREFIX + KafkaLoggerOptions.TOPIC_LONG;
  // private static final String ZK_LONG = LONG_PREFIX + KafkaLoggerOptions.ZK_LONG;
  //
  // private static final String[] ARGS_LONG = { GROUP_LONG, TEST_GROUP, SCHEMA_LONG, TEST_SCHEMA, THREADS_LONG,
  // TEST_THREADS, TOPIC_LONG, TEST_TOPIC, ZK_LONG, TEST_ZK };
  // private static final String[] ARGS_MISSING_MANDATORY = { SCHEMA_LONG, TOPIC_LONG, TEST_TOPIC, ZK_LONG, TEST_ZK };
  // private static final String[] ARGS_MISSING_OPTIONAL = { GROUP_LONG, TEST_GROUP, THREADS_SHORT, SCHEMA_LONG,
  // TEST_SCHEMA, TOPIC_LONG, TEST_TOPIC, ZK_LONG, TEST_ZK };
  // private static final String[] ARGS_NO_OPTIONAL = { GROUP_LONG, TEST_GROUP, SCHEMA_LONG, TEST_SCHEMA, TOPIC_LONG,
  // TEST_TOPIC, ZK_LONG, TEST_ZK };
  // private static final String[] ARGS_PARSE_FAIL = { GROUP_LONG, TEST_GROUP, THREADS_SHORT, TEST_THREADS +
  // GROUP_SHORT, SCHEMA_LONG, TEST_SCHEMA, TOPIC_LONG, TEST_TOPIC, ZK_LONG, TEST_ZK };
  // private static final String[] ARGS_SHORT = { GROUP_SHORT, TEST_GROUP, SCHEMA_SHORT, TEST_SCHEMA, THREADS_SHORT,
  // TEST_THREADS, TOPIC_SHORT, TEST_TOPIC, ZK_SHORT, TEST_ZK };
  //
  // @Test
  // public void shortTest() throws ParseException {
  // final KafkaLoggerOptions options = new KafkaLoggerOptions(ARGS_SHORT);
  // Assert.assertEquals("Failed to match group using short option keys.", TEST_GROUP, options.getKafkaGroup());
  // Assert.assertEquals("Failed to match topic using short option keys.", TEST_TOPIC, options.getKafkaTopic());
  // Assert.assertEquals("Failed to match number of threads using short option keys.", TEST_THREADS_INT,
  // options.getNumberOfThreads());
  // Assert.assertEquals("Failed to match schema using short option keys.", TEST_SCHEMA, options.getSchemaFiles());
  // Assert.assertEquals("Failed to match zookeeper URL using short option keys.", TEST_ZK,
  // options.getZookeeperConnectionString());
  // }
  //
  // @Test
  // public void longTest() throws ParseException {
  // final KafkaLoggerOptions options = new KafkaLoggerOptions(ARGS_LONG);
  // Assert.assertEquals("Failed to match group using long option keys.", TEST_GROUP, options.getKafkaGroup());
  // Assert.assertEquals("Failed to match topic using long option keys.", TEST_TOPIC, options.getKafkaTopic());
  // Assert.assertEquals("Failed to match number of threads using long option keys.", TEST_THREADS_INT,
  // options.getNumberOfThreads());
  // Assert.assertEquals("Failed to match schema using long option keys.", TEST_SCHEMA, options.getSchemaFiles());
  // Assert.assertEquals("Failed to match zookeeper URL using long option keys.", TEST_ZK,
  // options.getZookeeperConnectionString());
  // }
  //
  // @Test
  // public void optionalArgsTest() throws ParseException {
  // final KafkaLoggerOptions options = new KafkaLoggerOptions(ARGS_NO_OPTIONAL);
  // Assert.assertEquals("Failed to match group using long option keys.", TEST_GROUP, options.getKafkaGroup());
  // Assert.assertEquals("Failed to match topic using long option keys.", TEST_TOPIC, options.getKafkaTopic());
  // Assert.assertEquals("A value other than the default number of threads was returned buy getNumberOfThreads(), despite no threads argument having been specified.",
  // TEST_THREADS_DEFAULT, options.getNumberOfThreads());
  // Assert.assertEquals("Failed to match schema using long option keys.", TEST_SCHEMA, options.getSchemaFiles());
  // Assert.assertEquals("Failed to match zookeeper URL using long option keys.", TEST_ZK,
  // options.getZookeeperConnectionString());
  // }
  //
  // @Test
  // public void failParseThreadsTest() throws ParseException {
  // final KafkaLoggerOptions options = new KafkaLoggerOptions(ARGS_PARSE_FAIL);
  // Assert.assertEquals("Failed to match group using long option keys.", TEST_GROUP, options.getKafkaGroup());
  // Assert.assertEquals("Failed to match topic using long option keys.", TEST_TOPIC, options.getKafkaTopic());
  // Assert.assertEquals("A value other than the default number of threads was returned buy getNumberOfThreads(), despite the threads argument no being able to be parsed into an int.",
  // TEST_THREADS_DEFAULT,
  // options.getNumberOfThreads());
  // Assert.assertEquals("Failed to match schema using long option keys.", TEST_SCHEMA, options.getSchemaFiles());
  // Assert.assertEquals("Failed to match zookeeper URL using long option keys.", TEST_ZK,
  // options.getZookeeperConnectionString());
  // }
  //
  // @Test(expected = KakfaLoggerException.class)
  // public void missingOptionalTest() throws ParseException {
  // new KafkaLoggerOptions(ARGS_MISSING_OPTIONAL);
  // }
  //
  // @Test(expected = KakfaLoggerException.class)
  // public void missingMandatoryTest() throws ParseException {
  // new KafkaLoggerOptions(ARGS_MISSING_MANDATORY);
  // }
}
