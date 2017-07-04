/**
 * Created by jyot on 3/7/17.
 */

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackMessage;

class Slack {

    private SlackApi slack;

    Slack(String webhookUrl) {
        slack = new SlackApi(webhookUrl);
    }

    void sendMessage(String title, String text, String userToNotify) {
        SlackMessage message = new SlackMessage("");
        SlackAttachment attachment = new SlackAttachment();
        attachment.setTitle(title).setText(text).setColor("danger").setFallback(text);
        if (userToNotify != null)
            attachment.setPretext("<@" + userToNotify + ">");
        message.addAttachments(attachment);
        slack.call(message);
    }
}