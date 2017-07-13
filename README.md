# Cassandra Monitor

#### A java application to monitor your Cassandra cluster using the JMX interface exposed by the Cassandra nodes and report it via Slack messages.


## About

It takes a JSON config file as input which contains information regarding your Cassandra cluster and the metrics which need to be tracked.

It then evaluates each of the metrics provided in the config file along with some pre defined metrics to check if there's any problem with the cluster. If a problem is found in any node then it is reported through [Slack webhooks](https://api.slack.com/incoming-webhooks).

## Configuration

Sample config.json file - 

```json
{
  "nodes": [
    {
      "privateIp": "172.1.1.1",
      "host": "node1@example.com",
      "port": 7199,
      "label": "My production node 1"
    },
    {
      "privateIp": "172.1.1.1",
      "host": "node2@example.com",
      "port": 7199,
      "label": "My production node 2"
    }
  ],
  "userToNotify": "jyotman",
  "slackWebhook": "https://hooks.slack.com/services/.../.../...",
  "metrics": [
    {
      "objectName": "org.apache.cassandra.metrics:type=ClientRequest,scope=Read,name=Latency",
      "attribute": "FiveMinuteRate",
      "value": 10,
      "type": "max"
    },
    {
      "objectName": "org.apache.cassandra.metrics:type=ClientRequest,scope=Write,name=Latency",
      "attribute": "FiveMinuteRate",
      "value": 5,
      "type": "max"
    },
    {
      "objectName": "org.apache.cassandra.metrics:type=Client,name=connectedNativeClients",
      "attribute": "Value",
      "value": 3,
      "type": "max"
    },
    {
      "objectName": "org.apache.cassandra.metrics:type=ColumnFamily,name=PercentRepaired",
      "attribute": "Value",
      "value": 50,
      "type": "min"
    },
    {
      "objectName": "org.apache.cassandra.metrics:type=ColumnFamily,name=TotalDiskSpaceUsed",
      "attribute": "Value",
      "value": 100000000,
      "type": "max"
    },
    {
      "objectName": "org.apache.cassandra.metrics:type=CommitLog,name=TotalCommitLogSize",
      "attribute": "Value",
      "value": 500000000,
      "type": "max"
    },
    {
      "objectName": "java.lang:type=OperatingSystem",
      "attribute": "FreePhysicalMemorySize",
      "value": 50000000,
      "type": "min"
    }
  ]
}
```

1. nodes - Array containing all of your Cassandra nodes in the cluster. `privateIp` is basically the IP address to which the `host` field resolves to. If all your nodes are in a single Amazon Region then they'll probably be connected by their privte IPs hence the `privateIp` field has been set to the private IP of each node. If your nodes are connected through their public IPs then set the `privateIp` field respectively.
2. userToNotify - Slack username who will be tagged while sending metric alerts in the Slack channel.
3. slackWebhook - Slack channel's webhook URL which would receive the alerts.
4. metrics - Array containg all the metrics which would be evaluated for each node in the cluster. `type` represents whether the result of the metric should be below the `max` limit or above the `min` limit.

## Metric
Lets consider the following metric - 
```json
{
    "objectName": "org.apache.cassandra.metrics:type=Client,name=connectedNativeClients",
    "attribute": "Value",
    "value": 3,
    "type": "max"
}
```
This metric gives us the number of clients that are connected to a specific node. Setting `value` as *3* and `type` as *max* means that if the number of connected clients connected to that node are found to be greater than 3, then an alert would be fired.

You may include as many metrics as you like.
**Note** - Currently only those metrics are supported which return an `int`, `long` or `double`.

Along with the metrics specified in the `metrics` Array, some default metrics are also tracked. For example cassandra-monitor automatically makes sure that all the nodes in the cluster are live. If any Node is found to be down then it will also be reported.

## Usage
1. Make sure all nodes have the JMX port (specified in the config) open to this app.
2. Download the .jar file from the [latest release](https://github.com/jyotman/cassandra-monitor/releases).
3. Make sure you have JRE.
4. Run `CONFIG=path_to_config_file java -jar cassandra-monitor.jar`.

You can set a cron for the above command to ensure continuous monitoring.