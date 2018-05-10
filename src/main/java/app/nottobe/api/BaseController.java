package app.nottobe.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import app.nottobe.bean.User;
import app.nottobe.entity.Result;
import app.nottobe.exception.UnauthorizedException;

@ControllerAdvice
public class BaseController {

	@ModelAttribute("user")
	public User getUser(HttpServletRequest request) {
		return (User) request.getAttribute("user");
	}

	public String getSessionId(HttpServletRequest request) {
		return (String) request.getAttribute("sessionId");
	}

	public User authorized(HttpServletRequest request) {
		User user = getUser(request);
		String sessionId = getSessionId(request);
		if (user == null || StringUtils.isEmpty(sessionId)) {
			throw new UnauthorizedException();
		}
		return user;
	}

	@ExceptionHandler(UnauthorizedException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public Result<?> processUnauthorizedException() {
		return Result.getUnauthorizedErrorResult("用户未登录");
	}

	@ExceptionHandler(value = { IllegalStateException.class, NumberFormatException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Result<?> processIllegalStateAndNumberFormatException(Exception e) {
		return Result.getErrorResult(e.getMessage());
	}

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Result<?> processIllegalArgumentException(Exception e) {
		e.printStackTrace();
		return Result.getErrorResult(e.getMessage());
	}

}
