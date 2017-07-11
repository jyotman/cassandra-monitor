/**
 * Created by jyot on 3/7/17.
 */

import net.gpedro.integrations.slack.SlackApi;
import net.gpedro.integrations.slack.SlackAttachment;
import net.gpedro.integrations.slack.SlackField;
import net.gpedro.integrations.slack.SlackMessage;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

class Slack {

    private SlackApi slack;

    Slack(String webhookUrl) {
        slack = new SlackApi(webhookUrl);
    }

    void sendMessage(String title, String text, String userToNotify) {
        SlackMessage message = new SlackMessage("");
        SlackAttachment attachment = new SlackAttachment();
        attachment
                .setTitle(title)
                .setText(text)
                .setColor("danger")
                .setFallback(text);
        if (userToNotify != null)
            attachment.setPretext("<@" + userToNotify + ">");
        message.addAttachments(attachment);
        slack.call(message);
    }

    void sendMessage(String title, String text, String userToNotify, double currentValue, double threshold) {
        DecimalFormat df = new DecimalFormat("#.#####");
        SlackMessage message = new SlackMessage("");
        SlackAttachment attachment = new SlackAttachment();
        List<SlackField> fields = new ArrayList<SlackField>(2);
        SlackField current = new SlackField();
        current.setTitle("CurrentValue");
        current.setValue(df.format(currentValue));
        fields.add(current);
        SlackField expected = new SlackField();
        expected.setTitle("Threshold");
        expected.setValue(df.format(threshold));
        fields.add(expected);
        attachment
                .setTitle(title)
                .setText(text)
                .setColor("danger")
                .setFallback(text)
                .setFields(fields);
        if (userToNotify != null)
            attachment.setPretext("<@" + userToNotify + ">");
        message.addAttachments(attachment);
        slack.call(message);
    }
}