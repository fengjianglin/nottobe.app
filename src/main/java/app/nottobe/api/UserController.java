package app.nottobe.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.nottobe.bean.User;
import app.nottobe.component.MiniAppProvider;
import app.nottobe.component.SessionService;
import app.nottobe.entity.MiniAppAuthorize;
import app.nottobe.entity.MiniAppLogin;
import app.nottobe.entity.Result;
import app.nottobe.repository.UserRepository;
import app.nottobe.utils.DefaultUniqueIdGenerator;
import app.nottobe.utils.SecurityUtil;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

	private static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	private DefaultUniqueIdGenerator uniqueIdGenerator;

	@Autowired
	private MiniAppProvider MiniAppProvider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SessionService sessionService;

	@RequestMapping("test")
	public Result<User> test() {
		logger.debug("test");
		return Result.getResult(new User());
	}

	@GetMapping("login")
	public Result<User> wx_login(String code) {

		MiniAppLogin login = MiniAppProvider.login(code);
		User user = userRepository.findByUnionidOrOpenid(login.getUnionid(), login.getOpenid());
		if (user == null) {
			user = new User();
			user.setOpenid(login.getOpenid());
			user.setUnionid(login.getUnionid());
			user = userRepository.save(user);
		}
		user.setSessionKey(login.getSession_key());

		String uniqueId = uniqueIdGenerator.getNewId("ntb");
		String sessionId = "SESSIONID-" + uniqueId;
		sessionService.save(sessionId, user);

		return Result.getResult(sessionId, user);
	}

	@PostMapping("authorize")
	public Result<User> wx_authorize(HttpServletRequest req, String encryptedData, String iv) {
		User user = authorized(req);
		String sessionKey = user.getSessionKey();
		String userStr = SecurityUtil.decryptWxData(encryptedData, sessionKey, iv);
		if (StringUtils.isEmpty(userStr)) {
			return Result.getErrorResult("授权失败");
		}
		MiniAppAuthorize auth = new Gson().fromJson(userStr, MiniAppAuthorize.class);
		user.setAvatar(auth.getAvatarUrl());
		user.setNickname(auth.getNickName());
		user.setStatus(1);
		if (StringUtils.hasText(auth.getOpenId())) {
			user.setOpenid(auth.getOpenId());
		}
		if (StringUtils.hasText(auth.getUnionId())) {
			user.setUnionid(auth.getUnionId());
		}
		user = userRepository.save(user);
		return Result.getResult(user);
	}
}
