package com.school.api.common.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SlackNotificationService {

  @Value("${app.notifications.slack-webhook}")
  private String webhookUrl;

  private final RestTemplate restTemplate = new RestTemplate();

  public void notifyNewContact(String name, String email) {

    String payload = """
      {
        "text": "📩 *Nouveau message contact*\n*Nom:* %s\n*Email:* %s"
      }
    """.formatted(name, email);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    restTemplate.postForEntity(
      webhookUrl,
      new HttpEntity<>(payload, headers),
      String.class
    );
  }
}
