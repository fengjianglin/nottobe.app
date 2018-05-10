package app.nottobe.api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.nottobe.bean.User;
import app.nottobe.entity.MiniAppLogin;
import app.nottobe.entity.Result;
import app.nottobe.oauth.MiniAppProvider;
import app.nottobe.repository.UserRepository;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

	private static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	private MiniAppProvider MiniAppProvider;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("test")
	public Result<User> test() {
		return Result.getResult(new User());
	}

	@RequestMapping("login")
	public Result<User> login(String code) {
		logger.debug("login-------------------");
		MiniAppLogin login = MiniAppProvider.login(code);
		User user = userRepository.findByUnionidOrOpenid(login.getUnionid(), login.getOpenid());
		if (user == null) {
			user = new User();
			user.setOpenid(login.getOpenid());
			user.setUnionid(login.getUnionid());
			user = userRepository.save(user);
		}
		return Result.getResult(user);
	}

}
