package jikgong.global.slack;

import static com.slack.api.webhook.WebhookPayloads.payload;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SlackService {

    @Value("${webhook.slack.url}")
    private String SLACK_WEBHOOK_URL;

    private final Slack slackClient = Slack.getInstance();

    /**
     * 슬랙 메시지 전송
     **/
    public void sendMessage(String title, HashMap<String, String> data) {
        try {
            slackClient.send(SLACK_WEBHOOK_URL, payload(p -> p
                .text("*" + title + "*") // 볼드 처리
                .attachments(List.of(
                    Attachment.builder().color(Color.GREEN.toString()) // 메시지 색상
                        .fields( // 메시지 본문 내용
                            data.keySet().stream().map(key -> generateSlackField(key, data.get(key)))
                                .collect(Collectors.toList())
                        ).build())))
            );
            log.info("slack 알림 전송");
        } catch (IOException e) {
            log.error("slack 알림 전송 실패");
            e.printStackTrace();
        }
    }

    /**
     * Slack Field 생성
     **/
    private Field generateSlackField(String title, String value) {
        return Field.builder()
            .title(title)
            .value(value)
            .valueShortEnough(false)
            .build();
    }
}
