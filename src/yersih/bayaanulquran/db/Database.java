package yersih.bayaanulquran.db;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

class Database {

    private static final Database instance = new Database();
    private static final SQLiteDataSource dataSource = new SQLiteDataSource();


    static Connection connect() {
        dataSource.setUrl("jdbc:sqlite:data/database.db");
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
