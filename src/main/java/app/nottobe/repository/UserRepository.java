package app.nottobe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.nottobe.bean.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	public Page<User> findByStatus(int status, Pageable pageable);

	public User findByUnionid(String unionid);

	public User findByOpenid(String openid);

}
