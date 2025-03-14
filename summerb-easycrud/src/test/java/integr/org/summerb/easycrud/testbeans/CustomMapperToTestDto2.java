package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.TestDto2;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CustomMapperToTestDto2 implements RowMapper<TestDto2> {

  public static final String CUSTOM_MAPPER = "custom mapper";

  @Override
  public TestDto2 mapRow(ResultSet rs, int rowNum) throws SQLException {
    TestDto2 ret = new TestDto2();
    ret.setEnv(rs.getString("env"));
    ret.setLinkToFullDownload(CUSTOM_MAPPER);
    return ret;
  }
}
