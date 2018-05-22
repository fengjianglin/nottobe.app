package app.nottobe.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import app.nottobe.bean.Comment;
import app.nottobe.bean.Follow;
import app.nottobe.bean.Image;
import app.nottobe.bean.Moment;
import app.nottobe.bean.Up;
import app.nottobe.bean.User;
import app.nottobe.component.OssUploader;
import app.nottobe.entity.Result;
import app.nottobe.repository.CommentRepository;
import app.nottobe.repository.FollowRepository;
import app.nottobe.repository.MomentRepository;
import app.nottobe.repository.UpRepository;
import app.nottobe.repository.UserRepository;

@RestController
@RequestMapping("moment")
public class MomentController extends BaseController {

	private static final int PAGE_SIZE = 20;

	@Autowired
	private OssUploader ossUploader;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MomentRepository momentRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UpRepository upRepository;

	@Autowired
	private FollowRepository followRepository;

	@GetMapping("list")
	public Result<Page<Moment>> list(@RequestParam(required = false, defaultValue = "1") int page) {
		page = (--page) < 0 ? 0 : page;
		PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id");
		Page<Moment> moments = momentRepository.findAll(pageRequest);
		return Result.getResult(moments);
	}

	@GetMapping("followingslist")
	public Result<Page<Moment>> followingslist(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "1") int page) {
		User user = authorized(request);
		List<User> users = followRepository.findByFollower(user).stream().map(Follow::getFollowing)
				.collect(Collectors.toList());
		users.add(user);
		page = (--page) < 0 ? 0 : page;
		PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id");
		Page<Moment> moments = momentRepository.findByAuthorIn(users, pageRequest);
		return Result.getResult(moments);
	}

	@GetMapping("mylist")
	public Result<Page<Moment>> mylist(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "1") int page) {
		User user = authorized(request);
		page = (--page) < 0 ? 0 : page;
		PageRequest pageRequest = new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id");
		Page<Moment> moments = momentRepository.findByAuthor(user, pageRequest);
		return Result.getResult(moments);
	}

	@PostMapping("post_text")
	public Result<Moment> post_text(HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "") String text) {
		User user = authorized(request);
		Moment moment = new Moment();
		moment.setAuthor(user);
		moment.setText(text);
		moment = momentRepository.save(moment);
		return Result.getResult(moment);
	}

	@PostMapping("upload_image")
	public Result<Moment> upload_image(HttpServletRequest request, long moment_id) {
		User user = authorized(request);
		MultipartRequest fileRequest = (MultipartRequest) request;
		List<MultipartFile> files = fileRequest.getFiles("image");
		if (files == null || files.size() == 0) {
			return Result.getErrorResult("请上传图片");
		}
		Moment moment = momentRepository.findOne(moment_id);
		if (moment == null) {
			return Result.getErrorResult("上传失败");
		}

		List<Image> images = moment.getImages();
		if (images == null) {
			images = new ArrayList<Image>();
			moment.setImages(images);
		}
		MultipartFile multipartFile = files.get(0);
		if (!multipartFile.isEmpty() && multipartFile.getContentType().startsWith("image/")) {
			String originFilename = multipartFile.getOriginalFilename();
			String filename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + originFilename;
			String filepath = "ntb/" + filename;
			try {
				String url = ossUploader.uploadFile(filepath, multipartFile.getInputStream());
				Image image = new Image();
				image.setAuthor(user);
				image.setUrl(url);
				images.add(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		moment = momentRepository.save(moment);
		return Result.getResult(moment);
	}

	@PostMapping("delete")
	public Result<Boolean> delete(HttpServletRequest request, @RequestParam(required = true) long id) {
		User user = authorized(request);
		Moment moment = momentRepository.findOne(id);
		if (moment == null) {
			return Result.getErrorResult("删除失败");
		}
		if (moment.getAuthor() == null) {
			return Result.getErrorResult("删除失败!");
		}
		if (user.getId() != moment.getAuthor().getId()) {
			return Result.getUnauthorizedErrorResult("没有权限");
		}
		momentRepository.delete(id);
		return Result.getResult(true);
	}

	@GetMapping("up")
	public Result<Boolean> up(HttpServletRequest request, @RequestParam(required = true) long id) {
		User user = authorized(request);
		Moment moment = momentRepository.findOne(id);
		if (moment == null) {
			return Result.getErrorResult("赞失败");
		}
		if (upRepository.existsByAuthorAndMoment(user, moment)) {
			return Result.getErrorResult("已经赞过");
		}
		Up up = new Up();
		up.setAuthor(user);
		up.setMoment(moment);
		upRepository.save(up);
		return Result.getResult(true);
	}

	@GetMapping("cancel_up")
	public Result<Boolean> cancel_up(HttpServletRequest request, @RequestParam(required = true) long id) {
		User user = authorized(request);
		Moment moment = momentRepository.findOne(id);
		if (moment == null) {
			return Result.getErrorResult("取消失败");
		}
		if (!upRepository.existsByAuthorAndMoment(user, moment)) {
			return Result.getErrorResult("没有赞过");
		}
		upRepository.deleteByAuthorAndMoment(user, moment);
		return Result.getResult(true);
	}

	@GetMapping("isupped")
	public Result<Boolean> isUpped(HttpServletRequest request, long id) {
		User user = authorized(request);
		Moment moment = momentRepository.findOne(id);
		if (moment == null) {
			return Result.getErrorResult("参数错误");
		}
		boolean b = upRepository.existsByAuthorAndMoment(user, moment);
		return Result.getResult(b);
	}

	@PostMapping("post_comment")
	public Result<Comment> post_comment(HttpServletRequest request, long mid,
			@RequestParam(required = false, defaultValue = "0") long to_user_id, String text) {
		User user = authorized(request);
		if (user.getId() == to_user_id) {
			return Result.getErrorResult("不能对自己评论");
		}
		Moment moment = momentRepository.findOne(mid);
		if (moment == null) {
			return Result.getErrorResult("参数错误");
		}
		User toUser = userRepository.findOne(to_user_id);
		Comment comment = new Comment();
		comment.setAuthor(user);
		comment.setMoment(moment);
		comment.setText(text);
		comment.setToUser(toUser);
		comment = commentRepository.save(comment);
		return Result.getResult(comment);
	}

	@PostMapping("delete_comment")
	public Result<Boolean> deleteComment(HttpServletRequest request, @RequestParam(required = true) long id) {
		User user = authorized(request);
		Comment comment = commentRepository.findOne(id);
		if (comment == null) {
			return Result.getErrorResult("删除失败");
		}
		if (comment.getAuthor() == null) {
			return Result.getErrorResult("删除失败!");
		}
		if (user.getId() != comment.getAuthor().getId()) {
			return Result.getUnauthorizedErrorResult("没有权限");
		}
		commentRepository.delete(id);
		return Result.getResult(true);
	}
}
