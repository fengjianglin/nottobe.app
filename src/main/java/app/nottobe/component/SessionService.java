package app.nottobe.component;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import app.nottobe.bean.User;

@Service
public class SessionService {

	@Value("${session.expire.days}")
	private Long sessionExpireDays;

	private Map<String, User> redisTemplate = new HashMap<String, User>();

	public void save(String sessionId, User user) {
		if (StringUtils.hasText(sessionId)) {
			redisTemplate.put(sessionId, user);
		}
	}

	public User find(String sessionId) {
		try {
			return (User) redisTemplate.get(sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
