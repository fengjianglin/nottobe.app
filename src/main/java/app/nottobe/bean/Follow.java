package app.nottobe.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "follow")
public class Follow extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -6074689963865339728L;

	@ManyToOne
	@JoinColumn(name = "follower_id", foreignKey = @ForeignKey(name = "FK_follower_id_in_follow"))
	private User follower;

	@ManyToOne
	@JoinColumn(name = "following_id", foreignKey = @ForeignKey(name = "FK_following_id_in_follow"))
	private User following;

	public User getFollower() {
		return follower;
	}

	public void setFollower(User follower) {
		this.follower = follower;
	}

	public User getFollowing() {
		return following;
	}

	public void setFollowing(User following) {
		this.following = following;
	}

}
