package integr.org.summerb.easycrud;

import java.util.Arrays;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.summerb.security.api.CurrentUserResolver;
import org.summerb.security.api.Roles;

public class CurrentUserResolverTestImpl implements CurrentUserResolver {
	public User user;
	public User user1;
	public User user2;

	public CurrentUserResolverTestImpl() {
		user1 = new User("user1", "1", Arrays.asList(new SimpleGrantedAuthority(Roles.ROLE_USER)));
		user2 = new User("user2", "2", Arrays.asList(new SimpleGrantedAuthority(Roles.ROLE_USER)));
		user = user1;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public String getUserUuid() {
		return user.getUsername();
	}

}
