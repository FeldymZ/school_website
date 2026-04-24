package com.school.api.common.time;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {

    private static final ZoneId ZONE = ZoneId.of("Europe/Paris");

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }
}