package app.nottobe.component;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import app.nottobe.bean.User;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Service
public class SessionService {

	@Value("${session.expire.days}")
	private Long sessionExpireDays;

	@Autowired
	private RedisTemplate redisTemplate;

	public void save(String sessionId, User user) {
		if (StringUtils.hasText(sessionId)) {
			redisTemplate.opsForValue().set(sessionId, user, sessionExpireDays, TimeUnit.DAYS);
		}
	}

	public User find(String sessionId) {
		try {
			return (User) redisTemplate.opsForValue().get(sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
