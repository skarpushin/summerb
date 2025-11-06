package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.PostRow;
import org.summerb.easycrud.dao.EasyCrudDao;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;

public class PostRowServiceImpl
    extends EasyCrudServiceImpl<Long, PostRow, EasyCrudDao<Long, PostRow>>
    implements PostRowService {

  public PostRowServiceImpl() {
    super(PostRow.class);
  }

  @Override
  public PostRow getForEnv(String env) {
    return query().eq(PostRow::getTitle, env).findFirst(orderBy(PostRow::getTitle).asc());
  }
}
