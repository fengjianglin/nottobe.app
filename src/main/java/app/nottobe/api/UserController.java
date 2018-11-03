package app.nottobe.api;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import app.nottobe.bean.Follow;
import app.nottobe.bean.User;
import app.nottobe.component.MiniAppProvider;
import app.nottobe.component.FileUploader;
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

	private static final int PAGE_SIZE = 20;

	@Autowired
	private DefaultUniqueIdGenerator uniqueIdGenerator;

	@Autowired
	private FileUploader fileUploader;

	@Autowired
	private MiniAppProvider MiniAppProvider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private RestTemplate restTemplate;

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

	@GetMapping("get")
	public Result<User> get(long id) {
		User user = userRepository.findOne(id);
		if (user != null) {
			// 关注数
			int followings = followRepository.countByFollower(user);
			// 粉丝数
			int followers = followRepository.countByFollowing(user);
			user.setFollowings(followings);
			user.setFollowers(followers);
			return Result.getResult(user);
		}
		return Result.getErrorResult("请求错误");
	}

	@GetMapping("follows_num")
	public Result<Map<String, Integer>> follows_num(long id) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		User user = userRepository.findOne(id);
		if (user != null) {
			// 关注数
			int followings = followRepository.countByFollower(user);
			// 粉丝数
			int followers = followRepository.countByFollowing(user);
			map.put("followings", followings);
			map.put("followers", followers);
			return Result.getResult(map);
		}
		map.put("followings", 0);
		map.put("followers", 0);
		return Result.getResult(map);
	}

	@GetMapping("followings")
	public Result<Page<Follow>> followings(long id, @RequestParam(required = false, defaultValue = "1") int page) {
		User user = userRepository.findOne(id);
		if (user != null) {
			page = (--page) < 0 ? 0 : page;
			PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id");
			Page<Follow> followings = followRepository.findByFollower(user, pageRequest);
			return Result.getResult(followings);
		}
		return Result.getErrorResult("请求失败");
	}

	@GetMapping("followers")
	public Result<Page<Follow>> followers(long id, @RequestParam(required = false, defaultValue = "1") int page) {
		User user = userRepository.findOne(id);
		if (user != null) {
			page = (--page) < 0 ? 0 : page;
			PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id");
			Page<Follow> followers = followRepository.findByFollowing(user, pageRequest);
			return Result.getResult(followers);
		}
		return Result.getErrorResult("请求失败");
	}

	@GetMapping(value = "/hb/{id}.png")
	public void haibao(HttpServletResponse resp, @PathVariable long id) throws IOException {
		BufferedImage hb = fileUploader.randomUserHB();
		byte[] bytes = restTemplate.getForObject("http://www.manlanvideo.com/ntb/ntbqr.jpg", byte[].class);
		BufferedImage qr = ImageIO.read(new ByteArrayInputStream(bytes));
		drawQr(hb, qr);
		User user = userRepository.findOne(id);
		if (user != null) {
			String avatar = user.getAvatar();
			String nickname = user.getNickname();
			BufferedImage originAvatar = null;
			if (isHttpProtocol(avatar)) {
				byte[] bytes2 = restTemplate.getForObject(user.getAvatar(), byte[].class);
				originAvatar = ImageIO.read(new ByteArrayInputStream(bytes2));// 获取用户头像图片
			} else {
				originAvatar = ImageIO.read(this.getClass().getResourceAsStream("/avatar_default.png"));
			}
			drawAvatarAndNickname(hb, originAvatar, nickname);
		}
		resp.setContentType("image/png");
		OutputStream os = resp.getOutputStream();
		ImageIO.write(hb, "png", os);
	}

	// 绘制用户二维码
	private void drawQr(BufferedImage hb, BufferedImage qr) {
		int hb_width = hb.getWidth();
		int hb_height = hb.getHeight();
		int qr_size = 256;
		if (hb_width < 1024 || hb_height < 1024) {
			qr_size = 128;
			qr = scale(qr, qr_size, qr_size);
		}
		int qr_x = (int) (hb_width - qr_size * 1.1);
		int qr_y = (int) (hb_height - qr_size * 1.1);
		Graphics2D g2d = hb.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.drawImage(circle(qr), qr_x, qr_y, null);
		g2d.dispose();
	}

	// 绘制用户头像和昵称
	private void drawAvatarAndNickname(BufferedImage hb, BufferedImage avatar, String nickname) {
		int a_size = 256;
		if (hb.getWidth() < 1024 || hb.getHeight() < 1024) {
			a_size = 128;
		}
		int a_x = (int) (a_size * 0.1);
		int a_y = (int) (a_size * 0.1);

		Graphics2D g2d = hb.createGraphics();

		avatar = scale(avatar, a_size, a_size);
		g2d.drawImage(circle(avatar), a_x, a_y, null);

		int name_size = a_size / 3;
		int name_x = (int) (a_size * 1.2);
		int name_y = (int) (a_size * 2.2 / 3);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Font font = new Font(null, Font.BOLD, name_size);
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);
		g2d.drawString(nickname, name_x - 1, name_y - 1);

		font = new Font(null, Font.BOLD, name_size);
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		g2d.drawString(nickname, name_x + 1, name_y + 1);
		g2d.dispose();
	}

	private boolean isHttpProtocol(String url) {
		if (url == null || url.length() == 0 || url.equalsIgnoreCase("null")) {
			return false;
		}
		if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
			return false;
		}
		return true;
	}

	private BufferedImage scale(BufferedImage srcImage, int width, int height) {
		Image image = srcImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = scaledImage.getGraphics();
		g.drawImage(image, 0, 0, null); // 绘制缩小后的图
		g.dispose();
		return scaledImage;
	}

	/**
	 * 生成圆角图标
	 * 
	 * @param image
	 * @param cornerRadius 圆角半径
	 * @return
	 */
	private BufferedImage circle(BufferedImage image) {
		int oW = image.getWidth();
		int oH = image.getHeight();
		BufferedImage output = new BufferedImage((int) oW, (int) oH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = output.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, oW, oH, oW, oH));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return output;
	}

}
