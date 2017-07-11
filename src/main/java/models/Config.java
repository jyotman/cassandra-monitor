package models; /**
 * Created by jyot on 4/7/17.
 */

import java.util.List;

public class Config {
    private List<Node> nodes;
    private String userToNotify;
    private String slackWebhook;
    private Metric[] metrics;

    public List<Node> getNodes() {
        return nodes;
    }

    public String getUserToNotify() {
        return userToNotify;
    }

    public String getSlackWebhook() {
        return slackWebhook;
    }

    public Metric[] getMetrics() {
        return metrics;
    }
}