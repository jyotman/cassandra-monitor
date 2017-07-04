/**
 * Created by jyot on 3/7/17.
 */

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
            if (!liveHosts.contains(node.getPrivateIp() + "1"))
                slack.sendMessage("Node Down", node.getHost() + " is down", config.getUserToNotify());
        }
    }
}