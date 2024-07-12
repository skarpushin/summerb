package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.TestDto2;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;

public class TestDto2ServiceImpl
    extends EasyCrudServiceImpl<Long, TestDto2, EasyCrudDao<Long, TestDto2>>
    implements TestDto2Service {

  public TestDto2ServiceImpl() {
    super(TestDto2.class);
  }

  @Override
  public TestDto2 getForEnv(String env) {
    return query().eq(TestDto2::getEnv, env).findFirst(orderBy(TestDto2::getEnv).asc());
  }
}
