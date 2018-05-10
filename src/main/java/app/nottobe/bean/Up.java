package app.nottobe.bean;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "up")
public class Up extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -7788674364016561527L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "moment_id", foreignKey = @ForeignKey(name = "FK_moment_id_in_up"))
	private Moment moment;

	@ManyToOne
	@JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "FK_author_id_in_up"))
	private User author;

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
}
