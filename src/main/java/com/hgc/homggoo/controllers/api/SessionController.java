package com.hgc.homggoo.controllers.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/session-info")
public class SessionController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> sessionInfo(HttpSession session) {
        long now = System.currentTimeMillis();
        long lastAccessed = session.getLastAccessedTime();
        int maxInactive = session.getMaxInactiveInterval(); // 초 단위
        long remaining = (lastAccessed + (maxInactive * 1000L)) - now;

        Map<String, Object> map = new HashMap<>();
        map.put("sessionId", session.getId());
        map.put("maxInactiveInterval", maxInactive); // 전체 만료 시간 (초)
        map.put("remaining", remaining / 1000); // 남은 시간 (초)

        return map;
    }
}
