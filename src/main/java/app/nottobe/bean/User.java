package app.nottobe.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class User extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -7788674364016561527L;

	@Column(name = "openid")
	private String openid;

	@Column(name = "unionid")
	private String unionid;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "avatar")
	private String avatar;

	// 0: 未授权；1：已授权
	@Column(name = "status", columnDefinition = "INT default 0")
	private int status = 0;

	@JsonIgnore
	@Transient
	private String sessionKey;

	// 关注数
	@Transient
	private int followings = 0;

	// 粉丝数
	@Transient
	private int followers = 0;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	public int getFollowings() {
		return followings;
	}

	public void setFollowings(int followings) {
		this.followings = followings;
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

}
