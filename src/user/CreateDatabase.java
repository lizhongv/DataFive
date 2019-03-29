//创建数据库类
//创建fiveChess数据库、创建表user、game
package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {
	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");// 注册JDBC驱动
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "12345678");// 打开链接
			stmt = conn.createStatement();// 实例Statement对象将SQL语句发送到数据库
		} catch (ClassNotFoundException e) {// 处理Class.forName错误
			e.printStackTrace();
			System.exit(-1);// 系统停止运行
		} catch (SQLException e) { // 处理JDBC错误
			e.printStackTrace();
			try {
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.exit(-1);
			}
		}
		createDatabase(stmt, "fiveChess");
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createDatabase(Statement stmt, String dbName) {
		try {
			stmt.executeUpdate("create database " + dbName);// 执行SQL语句
			stmt.executeUpdate("use " + dbName);
			String sql;

			sql = "create table user(id int auto_increment not null primary key," + "name VARCHAR(10) not null, "
					+ "password VARCHAR(10) not null," + "email VARCHAR(30) not null," + "level int not null,"
					+ "regDate date not null)";
			stmt.executeUpdate(sql);
			sql = "insert into user(name,password,email,level,regDate)"
					+ "values('test1','test1','test1@five.com',1,'2016-01-22')";
			stmt.executeUpdate(sql);
			sql = "insert into user(name,password,email,level,regDate)"
					+ "values('test2','test2','test2@five.com',1,'2016-01-22')";
			stmt.executeUpdate(sql);

			sql = "create table game(id int auto_increment not null primary key," + "gameDate date not null, "
					+ "playerBlack VARCHAR(10) not null," + "playerWhite VARCHAR(10) not null,"
					+ "winner VARCHAR(10) not null," + "manualFileName VARCHAR(100) not null)";
			stmt.executeUpdate(sql);
			sql = "insert into game(gameDate,playerBlack,playerWhite,winner,manualFileName)"
					+ "values('2016-01-22','test1','test2','test2','game1201601221205.fiv')";
			stmt.executeUpdate(sql);
			sql = "insert into game(gameDate,playerBlack,playerWhite,winner,manualFileName)"
					+ "values('2016-01-22','test1','test2','test1','game1201601221848.fiv')";
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
