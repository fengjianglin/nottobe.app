package app.nottobe.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.nottobe.bean.Moment;
import app.nottobe.bean.Up;
import app.nottobe.bean.User;

@Repository
public interface UpRepository extends PagingAndSortingRepository<Up, Long> {

	boolean existsByAuthorAndMoment(User user, Moment moment);

	@Transactional
	void deleteByAuthorAndMoment(User user, Moment moment);
}
