package com.school.api.common.mail;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MailProperties {

    @Value("${app.mail.from}")
    private String from;
}
