package app.nottobe.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.nottobe.bean.Moment;

@Repository
public interface MomentRepository extends PagingAndSortingRepository<Moment, Long> {

}
