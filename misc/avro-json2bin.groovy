#!/opt/local/bin/groovy
// Date  : 2016-01
// Author: Gabriel C.
// Descr : Takes a json object on input and output the bin serialized equivalent
// Requires: Kafka-logger version 1.3 or newer
// Usage : groovy scriptName schema-registry-url topic_name

import org.apache.commons.io.IOUtils;
import com.comcast.headwaters.schema.registry.*;
import com.comcast.headwaters.serde.*;

if(args.length != 2){
    System.err << "Usage: groovy scriptName schema-registry-url topic_name\n"
    return
}

// Getting input data
String input = IOUtils.toString(System.in, "UTF-8");

// Getting the schema
schemaRegistryClient = new SchemaRegistryClient(args[0])
optionalSchema = schemaRegistryClient.getSchema(args[1])
if(!optionalSchema.isPresent()){
    System.err << "Schema " + args[1]+ " not found\n"
    return
}

// Converting to bin
avroSerde = new AvroSerDe(optionalSchema.get())
System.out << avroSerde.serialize(avroSerde.deserializeSpecificFromJson(input))
