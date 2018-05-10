package app.nottobe.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import app.nottobe.bean.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	public User findByUnionid(String unionid);

	public User findByOpenid(String openid);

	public User findByUnionidOrOpenid(String unionid, String openid);
}
