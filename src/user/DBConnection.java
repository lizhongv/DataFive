//数据库连接类
//创建Connect连接对象，其他类可以调用getConn()方法获取该连接对象
package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	Connection conn = null;

	public DBConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/fiveChess", "root", "12345678");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}

	public Connection getConn() {
		return conn;
	}
}
