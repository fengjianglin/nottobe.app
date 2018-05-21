package app.nottobe.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "image")
public class Image extends BaseEntity implements Serializable, Comparable<Image> {

	private static final long serialVersionUID = -7788674364016561527L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "moment_id", foreignKey = @ForeignKey(name = "FK_moment_id_in_image"))
	private Moment moment;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "FK_author_id_in_image"))
	private User author;

	@Column(name = "url")
	private String url;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(Image o) {
		return (int) (this.getId() - o.getId());
	}

}
