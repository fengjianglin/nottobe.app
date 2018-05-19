package app.nottobe.repository;

import java.util.List;

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
	
	List<Follow> findByFollower(User follower);
}
