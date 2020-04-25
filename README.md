# zday-kds-replay-demo
Java Zday KDS Replay Demo

A simple Java application that replays Json events that are stored in objects in Amazon S3 into a Amazon Kinesis stream. The application reads the timestamp attribute of the stored events and replays them as if they occurred in real time.

By default, the application will replay a historic data set of news events that made in Global Database of Society is derived from the public dataset [available in Open Data on Google GigQuery Table](https://console.cloud.google.com/bigquery?GK=gdelt-bq&redirect_from_classic=true&project=kda-demo&folder=&organizationId=&p=gdelt-bq&d=covid19&t=onlinenewsgeo&page=table/).

```
$ java -jar amazon-kinesis-replay-1.0.jar -streamName «Kinesis stream name» -streamRegion «AWS region»
```

To increase the number of events sent per second, you can accelerate the replay using the `-speedup` parameter.

The following command replays one hour of data within one second.

```
$ java -jar amazon-kinesis-replay-1.0.jar -streamName «Kinesis stream name» -streamRegion «AWS region» -speedup 3600
```

To aggregate multiple events in a Kinesis Data Streams record, you can use the `-aggregate` option. Aggregation allows you to increase the number of records sent per API call, which effectively increases producer throughput

```
$ java -jar amazon-kinesis-replay-1.0.jar -streamName «Kinesis stream name» -streamRegion «AWS region» -speedup 3600 -aggregate
```

To specify an alternative dataset you can use the `-bucket` and `-prefix` options as long as the events in the objects are stored in minified Json format, have a `timestamp` attribute and are ordered by this timestamp. The name of the timestamp attribute can be customized with the `timestampAttributeName` parameter.

```
$ java -jar amazon-kinesis-replay-1.0.jar -streamName «Kinesis stream name» -streamRegion «AWS region» -bucketName «S3 bucket name» -bucketRegion «S3 bucket region» -objectPrefix «S3 prefix of objects to read»
```

More options can be obtained through the `-help` parameter.