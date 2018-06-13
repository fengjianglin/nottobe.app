package app.nottobe.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.nottobe.bean.Follow;
import app.nottobe.bean.User;

@Repository
public interface FollowRepository extends PagingAndSortingRepository<Follow, Long> {

	// 某人的关注数
	int countByFollower(User follower);

	// 某人的粉丝数
	int countByFollowing(User following);

	boolean existsByFollowerAndFollowing(User follower, User following);

	@Transactional
	void deleteByFollowerAndFollowing(User follower, User following);

	// 某人的关注列表
	List<Follow> findByFollower(User follower);

	// 某人的关注列表 ： 分页
	Page<Follow> findByFollower(User follower, Pageable page);

	// 某人的粉丝列表 ： 分页
	Page<Follow> findByFollowing(User following, Pageable page);
}
