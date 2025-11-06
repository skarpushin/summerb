package integr.org.summerb.easycrud.testbeans;

import integr.org.summerb.easycrud.dtos.UserRow;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowCustomMapper implements RowMapper<UserRow> {

  public static final String CUSTOM_MAPPER = "custom mapper";

  @Override
  public UserRow mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserRow ret = new UserRow();
    ret.setName(rs.getString("name"));
    ret.setAbout(CUSTOM_MAPPER);
    return ret;
  }
}
