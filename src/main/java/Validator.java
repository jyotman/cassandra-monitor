/**
 * Created by jyot on 3/7/17.
 */

import models.Config;
import models.Metric;
import models.Node;

import java.util.List;

class Validator {

    private Config config;
    private Slack slack;
    private Node node;

    Validator(Config config, Node node) {
        this.config = config;
        this.node = node;
        slack = new Slack(config.getSlackWebhook());
    }

    void checkHosts(List liveHosts) {
        for (Node node : config.getNodes()) {
            if (!liveHosts.contains(node.getPrivateIp()))
                slack.sendMessage("Node Down", node.getHost() + " is down", config.getUserToNotify());
        }
    }

    void checkThreshold(double current, Metric metric) {
        double threshold = (double) metric.getValue();
        if (metric.getType().equals("max")) {
            if (current > threshold)
                slack.sendMessage("Threshold Crossed", node.getHost() + " is above threshold for metric " + metric.getObjectName(), config.getUserToNotify(), current, threshold);
        } else if (metric.getType().equals("min")) {
            if (current < threshold)
                slack.sendMessage("Threshold Crossed", node.getHost() + " is below threshold for metric " + metric.getObjectName(), config.getUserToNotify(), current, threshold);
        }

    }
}