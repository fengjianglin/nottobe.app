package app.nottobe.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.nottobe.bean.Comment;

@Repository
public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {

}
