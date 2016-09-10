package integr.org.summerb.jdbccrud;

import org.summerb.approaches.jdbccrud.api.EasyCrudPerRowAuthStrategy;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;

public class TestDto2PerRowAuthImpl implements EasyCrudPerRowAuthStrategy<TestDto2> {
	@Override
	public void assertAuthorizedToCreate(TestDto2 dto) throws NotAuthorizedException {
		if ("throwNaeOnCreate".equals(dto.getEnv())) {
			throw new NotAuthorizedException("test", "create");
		}
	}

	@Override
	public void assertAuthorizedToUpdate(TestDto2 existingVersion, TestDto2 newVersion) throws NotAuthorizedException {
		if ("throwNaeOnUpdate".equals(existingVersion.getEnv())) {
			throw new NotAuthorizedException("test", "update", "" + existingVersion.getId());
		}
		if ("throwNaeForUpdate".equals(newVersion.getEnv())) {
			throw new NotAuthorizedException("test", "update", "" + existingVersion.getId());
		}
	}

	@Override
	public void assertAuthorizedToRead(TestDto2 dto) throws NotAuthorizedException {
		if ("throwNaeOnRead".equals(dto.getEnv())) {
			throw new NotAuthorizedException("test", "read", "" + dto.getId());
		}
	}

	@Override
	public void assertAuthorizedToDelete(TestDto2 dto) throws NotAuthorizedException {
		if ("throwNaeOnDelete".equals(dto.getEnv())) {
			throw new NotAuthorizedException("test", "delete", "" + dto.getId());
		}
	}
}
