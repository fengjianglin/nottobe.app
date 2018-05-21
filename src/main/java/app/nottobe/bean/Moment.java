package app.nottobe.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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

	@Lob
	@Column(name = "text", columnDefinition = "TEXT")
	private String text;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "moment_id")
	private List<Image> images;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "moment_id")
	private List<Up> ups;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "moment_id")
	private List<Message> messages;

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

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public List<Up> getUps() {
		return ups;
	}

	public void setUps(List<Up> ups) {
		this.ups = ups;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
