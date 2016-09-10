package integr.org.summerb.jdbccrud;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ProfileValueSourceConfiguration;
import org.springframework.test.annotation.SystemProfileValueSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:summerb-integr-test-context.xml")
@ProfileValueSourceConfiguration(SystemProfileValueSource.class)
@Transactional
public class EasyCrudServiceTableAuthTest extends GenericCrudServiceTestTemplate {
	@Autowired
	@Resource(name = "testDto1Service")
	private EasyCrudService<String, TestDto1> testDto1Service;

	@Autowired
	@Resource(name = "testDto2Service")
	private EasyCrudService<Long, TestDto2> testDto2Service;

	@Autowired
	@Resource(name = "testDto1ServiceEb")
	private EasyCrudService<String, TestDto1> testDto1ServiceEb;

	@Override
	public EasyCrudService<String, TestDto1> getTestDto1Service() {
		return testDto1Service;
	}

	@Override
	public EasyCrudService<Long, TestDto2> getTestDto2Service() {
		return testDto2Service;
	}

	@Override
	public EasyCrudService<String, TestDto1> getTestDto1ServiceEb() {
		return testDto1ServiceEb;
	}

}
