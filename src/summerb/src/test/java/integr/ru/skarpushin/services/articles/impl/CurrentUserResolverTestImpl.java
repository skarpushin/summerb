package integr.ru.skarpushin.services.articles.impl;

import java.util.Date;
import java.util.UUID;

import org.summerb.approaches.security.api.CurrentUserResolver;
import org.summerb.microservices.users.api.dto.User;

public class CurrentUserResolverTestImpl implements CurrentUserResolver {
	public User user;
	public User user1;
	public User user2;

	public CurrentUserResolverTestImpl() {
		user1 = new User();
		user1.setUuid(UUID.randomUUID().toString());
		user1.setEmail("test" + new Date().getTime() + "@test.org");
		user1.setDisplayName("Display name");

		user2 = new User();
		user2.setUuid(UUID.randomUUID().toString());
		user2.setEmail("test" + (new Date().getTime() + 20) + "@test.org");
		user2.setDisplayName("Display name 2");

		user = user1;
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public String getUserUuid() {
		return user.getUuid();
	}

}
