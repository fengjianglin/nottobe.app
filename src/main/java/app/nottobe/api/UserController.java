package app.nottobe.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import app.nottobe.bean.Follow;
import app.nottobe.bean.User;
import app.nottobe.component.MiniAppProvider;
import app.nottobe.component.SessionService;
import app.nottobe.entity.MiniAppAuthorize;
import app.nottobe.entity.MiniAppLogin;
import app.nottobe.entity.Result;
import app.nottobe.repository.FollowRepository;
import app.nottobe.repository.UserRepository;
import app.nottobe.utils.DefaultUniqueIdGenerator;
import app.nottobe.utils.SecurityUtil;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

	@Autowired
	private DefaultUniqueIdGenerator uniqueIdGenerator;

	@Autowired
	private MiniAppProvider MiniAppProvider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private SessionService sessionService;

	@GetMapping("login")
	public Result<User> wx_login(String code) {

		MiniAppLogin login = MiniAppProvider.login(code);
		User user = StringUtils.isEmpty(login.getUnionid()) ? //
				userRepository.findByOpenid(login.getOpenid()) : //
				userRepository.findByUnionid(login.getUnionid());

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

	@GetMapping("list")
	public Result<Page<User>> list(@RequestParam(required = false, defaultValue = "1") int page) {
		page = (--page) < 0 ? 0 : page;
		PageRequest pageRequest = new PageRequest(page, 20, Sort.Direction.DESC, "id");
		Page<User> users = userRepository.findByStatus(1, pageRequest);
		return Result.getResult(users);
	}

	@GetMapping("follow")
	public Result<Boolean> follow(HttpServletRequest request, long id) {
		User user = authorized(request);
		User user2 = userRepository.findOne(id);
		if (user2 == null) {
			return Result.getErrorResult("参数错误");
		}

		if (followRepository.existsByFollowerAndFollowing(user, user2)) {
			return Result.getErrorResult("已经关注");
		}

		Follow follow = new Follow();
		follow.setFollower(user);
		follow.setFollowing(user2);
		followRepository.save(follow);
		return Result.getResult(true);
	}

	@GetMapping("unfollow")
	public Result<Boolean> unfollow(HttpServletRequest request, long id) {
		User user = authorized(request);
		User user2 = userRepository.findOne(id);
		if (user2 == null) {
			return Result.getErrorResult("参数错误");
		}
		if (!followRepository.existsByFollowerAndFollowing(user, user2)) {
			return Result.getErrorResult("没有关注");
		}
		followRepository.deleteByFollowerAndFollowing(user, user2);
		return Result.getResult(true);
	}

	@GetMapping("isfollowing")
	public Result<Boolean> isFollowing(HttpServletRequest request, long id) {
		User user = authorized(request);
		User user2 = userRepository.findOne(id);
		if (user2 == null) {
			return Result.getErrorResult("参数错误");
		}
		boolean b = followRepository.existsByFollowerAndFollowing(user, user2);
		return Result.getResult(b);
	}

}
