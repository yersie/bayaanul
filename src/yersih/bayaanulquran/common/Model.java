package yersih.bayaanulquran.common;

import java.io.IOException;
import java.sql.SQLException;

public interface Model {
    void add() throws SQLException, IOException;
    void set() throws SQLException, IOException;
    void delete() throws SQLException, IOException;

}
