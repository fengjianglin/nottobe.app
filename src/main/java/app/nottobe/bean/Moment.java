package app.nottobe.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "moment")
public class Moment extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -7788674364016561527L;

	@ManyToOne
	@JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "FK_author_id_in_moment"))
	private User author;

	@Column(name = "text")
	private String text;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "moment_id")
	private Set<Image> images;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "moment_id")
	private Set<Up> ups;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "moment_id")
	private Set<Message> messages;

	@Column(name = "status", columnDefinition = "INT default 0")
	private int status = 0;

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Set<Image> getImages() {
		return images;
	}

	public void setImages(Set<Image> images) {
		this.images = images;
	}

	public Set<Up> getUps() {
		return ups;
	}

	public void setUps(Set<Up> ups) {
		this.ups = ups;
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
