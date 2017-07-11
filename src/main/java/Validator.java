/**
 * Created by jyot on 3/7/17.
 */

import models.Config;
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

    void checkThreshold(double current, double threshold, String objectName, String type) {
        if (type.equals("max")) {
            if (current > threshold)
                slack.sendMessage("Threshold Crossed", node.getHost() + " is above threshold for metric " + objectName, config.getUserToNotify(), current, threshold);
        } else if (type.equals("min")) {
            if (current < threshold)
                slack.sendMessage("Threshold Crossed", node.getHost() + " is below threshold for metric " + objectName, config.getUserToNotify(), current, threshold);
        }

    }

    void checkThreshold(int current, double threshold, String objectName, String type) {
        if (type.equals("max")) {
            if (current > threshold)
                slack.sendMessage("Threshold Crossed", node.getHost() + " is above threshold for metric " + objectName, config.getUserToNotify(), current, threshold);
        } else if (type.equals("min")) {
            if (current < threshold)
                slack.sendMessage("Threshold Crossed", node.getHost() + " is below threshold for metric " + objectName, config.getUserToNotify(), current, threshold);
        }
    }
}