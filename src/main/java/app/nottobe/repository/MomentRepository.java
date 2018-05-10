package app.nottobe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.nottobe.bean.Moment;
import app.nottobe.bean.User;

@Repository
public interface MomentRepository extends PagingAndSortingRepository<Moment, Long> {

	Page<Moment> findByAuthor(User author, Pageable page);
	
//	Page<Moment> find( Pageable page);
}
