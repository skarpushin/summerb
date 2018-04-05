package integr.org.summerb.jdbccrud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.EasyCrudWireTap;
import org.summerb.approaches.jdbccrud.scaffold.api.EasyCrudScaffold;

import com.google.common.eventbus.EventBus;

@Configuration
public class ScaffoldBeans {
	@Autowired
	private EasyCrudScaffold easyCrudScaffold;

	@Autowired
	private EasyCrudWireTap<?, ?> easyCrudWireTapEventBus;

	// table1:
	// table2: forms_test_2

	@Bean
	public TestDto1Service testDto1Service() {
		return easyCrudScaffold.fromService(TestDto1Service.class, TestDto1.class.getCanonicalName(), "forms_test_1");
	}

	@Bean
	public EasyCrudService<String, TestDto1> testDto1ServiceEb(EventBus eventBus) {
		return easyCrudScaffold.fromDto(TestDto1.class, TestDto1.class.getCanonicalName(), "forms_test_1",
				easyCrudWireTapEventBus);
	}

	@Bean
	public EasyCrudService<Long, TestDto2> testDto2Service() {
		return easyCrudScaffold.fromDto(TestDto2.class, TestDto2.class.getCanonicalName(), "forms_test_2");
	}
}
