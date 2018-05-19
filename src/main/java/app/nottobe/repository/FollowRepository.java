package app.nottobe.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.nottobe.bean.Follow;
import app.nottobe.bean.User;

@Repository
public interface FollowRepository extends PagingAndSortingRepository<Follow, Long> {

	// 某人的关注数
	int countByFollower(User follower);

	// 某人的粉丝数
	int countByFollowing(User following);

	boolean existsByFollowerAndFollowing(User follower, User following);
}
