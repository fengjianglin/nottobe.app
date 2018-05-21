package app.nottobe.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "message")
public class Message extends BaseEntity implements Serializable, Comparable<Message> {

	private static final long serialVersionUID = -7788674364016561527L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "moment_id", foreignKey = @ForeignKey(name = "FK_moment_id_in_message"))
	private Moment moment;

	@ManyToOne
	@JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "FK_author_id_in_message"))
	private User author;

	@ManyToOne
	@JoinColumn(name = "to_user_id", foreignKey = @ForeignKey(name = "FK_to_user_id_in_message"))
	private User toUser;

	@Lob
	@Column(name = "text", columnDefinition = "TEXT")
	private String text;

	public Moment getMoment() {
		return moment;
	}

	public void setMoment(Moment moment) {
		this.moment = moment;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public User getToUser() {
		return toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int compareTo(Message o) {
		return (int) (this.getId() - o.getId());
	}
}
