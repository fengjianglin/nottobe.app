package app.nottobe.entity;

import java.io.Serializable;

@SuppressWarnings("unchecked")
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int CODE_SUCCESS = 200;
	public static final int CODE_CREATED = 201;
	public static final int CODE_ACCEPTED = 202;

	public static final int CODE_BAD_REQUEST = 400;
	public static final int CODE_UNAUTHORIZED = 401;
	public static final int CODE_SIGN_ERROR = 402;

	private int code;
	private String message;
	private T data;
	private String sessionId;

	private Result() {
	}

	public static <T> Result<T> getResult(T data) {
		return getResult("", data);
	}

	public static <T> Result<T> getResult(String sessionId, T data) {
		return getResult(sessionId, data, "");
	}

	public static <T> Result<T> getResult(T data, String msg) {
		return getResult("", data, msg);
	}

	public static <T> Result<T> getResult(String sessionId, T data, String msg) {
		return getResult(sessionId, CODE_SUCCESS, data, msg);
	}

	public static <T> Result<T> getResult(String sessionId, int code, T data, String message) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		result.setData(data == null ? (T) new Empty() : data);
		result.setMessage(message);
		result.setSessionId(sessionId == null ? "" : sessionId);
		return result;
	}

	public static <T> Result<T> getResult(String sessionId, int code, String message) {
		Result<T> result = new Result<T>();
		result.setCode(code);
		result.setMessage(message);
		result.setSessionId(sessionId == null ? "" : sessionId);
		return result;
	}

	public static <T> Result<T> getResult(String sessionId, int code, T data) {
		return getResult(sessionId, code, data, "");
	}

	public static <T> Result<T> getErrorResult(String sessionId, T data, String message) {
		return getResult(sessionId, CODE_BAD_REQUEST, data, message);
	}

	public static <T> Result<T> getErrorResult(String sessionId, String message) {
		return getResult(sessionId, CODE_BAD_REQUEST, (T) new Empty(), message);
	}

	public static <T> Result<T> getErrorResult(String message) {
		return getResult(null, CODE_BAD_REQUEST, (T) new Empty(), message);
	}

	public static <T> Result<T> getUnauthorizedErrorResult(String message) {
		return getResult(null, CODE_UNAUTHORIZED, (T) new Empty(), message);
	}

	public static <T> Result<T> getUnauthorizedErrorResult() {
		return getUnauthorizedErrorResult("没有登录");
	}

	public static <T> Result<T> getSignErrorResult() {
		return getResult(null, CODE_SIGN_ERROR, (T) new Empty(), "签名错误");
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@SuppressWarnings("unused")
	private static class Empty implements Serializable {
		private static final long serialVersionUID = 2158455533716052185L;
		private boolean empty = true;

		public boolean getEmpty() {
			return empty;
		}

		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
	}
}
