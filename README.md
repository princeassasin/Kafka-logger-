Kafka logger is an application that outputs the content of a Kafka topic in a log4j logger.


# Overview

The Kafka logger is a generic consumer for Headwaters that reads messages published in a Kafka cluster and outputs it using Log4j. Since the data in Headwaters are serialized using Avro schemas, the Kafka logger deserializes the messages using the schema specified in its configuration, and outputs the JSON representation of the deserialized object.

If no schema is specified, it inspects the data and if the message is ASCII printable, it outputs it as a string - which provides the same functionality as the kafka-console-consumer. If the message isn't ASCII printable, it outputs the bytes of the message in a human-readable fashion.

In most cases, an Avro schema should be specified. The Kafka logger takes an ordered comma-separated list of Avro schemas in order to manage the schema dependencies (see examples below). However, for complex schemas (e.g. union schema which contains several record types), the Kafka logger needs to be provided an event class, which is the fully qualified class name of the record to use to deserialize the messages.

If an error occurs while trying to deserialize the data, the Kafka logger outputs an error and provides the message bytes encoded in base-64 (see [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt)).


# Usage

    $java -cp target/kafka-logger-1.6.0jar com.comcast.headwaters.kafka.logger.KafkaLogger
	ERROR StatusLogger No log4j2 configuration file found. Using default configuration: logging only errors to the console.
	11:07:47.481 [main] ERROR com.comcast.headwaters.kafka.logger.KafkaLoggerConfiguration - Topic configuration is missing!
	usage: kafka-logger
    --config <configuration-file>   The path to the configuration file for
                                    the kafka-logger. Command line values
    --consumer-threads <number>     The number of threads that should read
                                    from the topic. Default value: 1
    --display-key <true/false>      Defines whether or not to display the
                                    Kafka key (default: false)
    --event-class <arg>             The fully qualified class name (i.e.
                                    record schema) to use to deserialize
                                    the events
    --kafka <property=value>        Properties passed verbatim to the
                                    Kafka consumer properties
    --pretty-print <true/false>     Defines whether or not to display the
                                    Kafka message on multiple line
    --schema-registry <arg>         The URI of the schema registry
    --schemas <arg>                 A comma delimited set of paths to
                                    files defining the Avro schema(s) used
                                    to encode the messages in the Kafka
                                    topic. If there is only one file, it
                                    does not need a comma after it.
    --topic <topic-name>            The name of the Kafka topic to read
                                    the messages from.

# Configuration

The configuration of the Kafka logger can be done through command line arguments and/or using a properties-based configuration file (which is then specified using a command line argument). Configuration attributes can be specified in both places in which case the command line value overrides the value specified in the file.

Example of configuration file for the sandbox environment:
	
	#Defines whether or not to display the Kafka key (default: false)
	#default: false
	#display-key=false
                                    
    # The name of the Kafka topic to read the messages from
    topic=enter_your_topic

    # Schema registry URI
    schema-registry=http://headwaters.sandbox.video.comcast.net/schema_registry/

    # Kafka consumer configuration. 
    bootstrap.servers=headwaters.sandbox.video.comcast.net:9092
    group.id=myGroupId
    dual.commit.enabled=false
    auto.commit.enable=false
    offsets.storage=kafka
    value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
	key.deserializer=org.apache.kafka.common.serialization.StringDeserializer

Command line equivalent of the previous configuration file example:

    --display-key true --topic viper-ip-session-test --schema-registry=http://headwaters.sandbox.video.comcast.net/schema_registry/ -kafka zookeeper.connect=headwaters.sandbox.video.comcast.net:9092 -kafka group.id=myGroupId -kafka auto.commit.enable=false -kafka offsets.storage=kafka -kafka dual.commit.enabled=false -kafka auto.commit.enable=false

# Updated Log4j Configuration

The Log4j configuration can be specified using either an XML file or a properties file. See Log4j manual for more info "http://logging.apache.org/log4j/2.x/".

If the Log4j configuration is not specified, Kafka logger initializes Log4j using the BasicConfigurator, which outputs the log messages in the console (stdout), and sets the root log level to INFO.

Important: Kafka logger outputs the Kafka messages with logger "com.comcast.headwaters.kafka.logger.KafkaMessageConsumer" as INFO messages. You might want to check that your log4j configuration will output it, otherwise, it may be a little boring.

Example of log4j configuration file:

	<?xml version="1.0" encoding="UTF-8"?> 
	<Configuration>
	<Appenders>
    <Console name="CONSOLE" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level [%t] %logger{36} - %m%n"/>
    </Console>
 	 </Appenders>
 	 <Loggers>
  	 <Logger name="com.comcast" level="trace">
      <AppenderRef ref="CONSOLE"/>
     </Logger>
       <Root level="ERROR">
       	<AppenderRef ref="CONSOLE"/>
    	</Root>
  	</Loggers>
	</Configuration>

# Full Example
    
    $java -cp target/kafka-logger-1.6.0-SNAPSHOT.jar -Dlog4j.configuration=file:src/test/resources/log4j2.xml com.comcast.headwaters.kafka.logger.KafkaLogger -config src/test/resources/viper-test.config

    2015-07-29 22:48:37,548 INFO  [pool-1-thread-1]: com.comcast.headwaters.kafka.logger.KafkaMessageConsumer - {"header": {"timestamp": 1438231717696, "uuid": [3, 20, -124, 74, 121, -61, 78, 103, -81, 80, -64, 35, 47, 11, -29, 103], "money": null, "partner": "comcast", "hostname": null}, "device": {"deviceId": "2D2B1854-A107-4111-9C5B-B8370E2475ED", "xcalDeviceId": "e7320362f59022af2f0f3fb947912dec", "appName": "cDVR-X2-IOS", "appVersion": "1.8.6.373", "deviceType": "IOS", "isp": "Comcast Cable", "ipAddress": "172.30.131.210", "networkLocation": null, "userAgent": "cDVR-X2-IOS / Apple; iPad 1536 / iOS 8.0.2 / AT&T / e7320362f59022af2f0f3fb947912dec"}, "eventType": "START", "startTime": 1438231696198, "sessionId": [-95, 89, 64, -55, -48, 53, 64, -57, -122, 89, -2, 31, -74, 56, -115, 125], "assetId": {"providerId": "hustler.com", "assetId": "HUST0715553200404320"}, "assetMetadata": null, "geolocation": null, "sessionDuration": null, "drmLatency": null, "openLatency": null, "assetPosition": null, "trickplayEvents": null, "bufferingEvents": null, "fragmentWarningEvents": null, "errorEvents": null, "fatalError": null, "successfulPlaybackStart": null, "frameRateMin": null, "frameRateMax": null, "bitRateMin": null, "bitRateMax": null}
    2015-07-29 22:48:37,548 INFO  [pool-1-thread-1]: com.comcast.headwaters.kafka.logger.KafkaMessageConsumer - {"header": {"timestamp": 1438231717696, "uuid": [3, 20, -124, 74, 121, -61, 78, 103, -81, 80, -64, 35, 47, 11, -29, 103], "money": null, "partner": "comcast", "hostname": null}, "device": {"deviceId": "2D2B1854-A107-4111-9C5B-B8370E2475ED", "xcalDeviceId": "e7320362f59022af2f0f3fb947912dec", "appName": "cDVR-X2-IOS", "appVersion": "1.8.6.373", "deviceType": "IOS", "isp": "Comcast Cable", "ipAddress": "172.30.131.210", "networkLocation": null, "userAgent": "cDVR-X2-IOS / Apple; iPad 1536 / iOS 8.0.2 / AT&T / e7320362f59022af2f0f3fb947912dec"}, "eventType": "START", "startTime": 1438231696198, "sessionId": [-95, 89, 64, -55, -48, 53, 64, -57, -122, 89, -2, 31, -74, 56, -115, 125], "assetId": {"providerId": "hustler.com", "assetId": "HUST0715553200404320"}, "assetMetadata": null, "geolocation": null, "sessionDuration": null, "drmLatency": null, "openLatency": null, "assetPosition": null, "trickplayEvents": null, "bufferingEvents": null, "fragmentWarningEvents": null, "errorEvents": null, "fatalError": null, "successfulPlaybackStart": null, "frameRateMin": null, "frameRateMax": null, "bitRateMin": null, "bitRateMax": null}
    
# Tool
	The Groovy Script to convert the schema from hexadecimal value to ascii, place the kafka-logger jar file in $HOME/.groovy/lib folder.
	
	$cp kafka-logger.jar /$HOME/.groovy/lib    
	$echo '{"header":{"timestamp":1450971105001,"uuid":null,"money":null,"partner":null,"hostname":null},"facts":[{"timestamp":1450971105001,"key":"california","value":112}]}' | groovy ./avro-json2bin.groovy http://headwaters.sandbox.video.comcast.net/schema_registry/ clean.blink.comcast.facts | hexdump -C
	
	00000000  d2 9b f0 c9 ba 54 00 00  00 00 02 d2 9b f0 c9 ba  |.....T..........|
	00000010  54 14 63 61 6c 69 66 6f  72 6e 69 61 00 00 00 00  |T.california....|
	00000020  00 00 5c 40 00                                    |..\@.|
	00000025