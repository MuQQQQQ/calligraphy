package extract.util;

import java.sql.SQLException;

public interface JdbcTask {
	void exec() throws SQLException;
}
