/**
 * Created by jyot on 4/7/17.
 */

import java.util.List;

class Config {
    private List<Node> nodes;
    private String userToNotify;
    private String slackWebhook;

    public List<Node> getNodes() {
        return nodes;
    }

    public String getUserToNotify() {
        return userToNotify;
    }

    public String getSlackWebhook() {
        return slackWebhook;
    }
}
