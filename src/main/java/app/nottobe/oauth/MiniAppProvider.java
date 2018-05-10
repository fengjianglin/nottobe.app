package app.nottobe.oauth;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import app.nottobe.entity.MiniAppLogin;

@Component
public class MiniAppProvider {

	private static Logger logger = Logger.getLogger(MiniAppProvider.class);

	private final String appId = "wx698f91201bfe4536";

	private final String appSecret = "396990b7a12ed178ca0d050eb0bd8dae";

	private String jscode2session = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

	@Autowired
	private RestTemplate restTemplate;

	public MiniAppLogin login(String code) {
		String url = String.format(jscode2session, appId, appSecret, code);
		String info = restTemplate.getForObject(url, String.class);
		logger.error(info);
		MiniAppLogin login = new Gson().fromJson(info, MiniAppLogin.class);
		return login;
	}

}
