//用户管理类
//在user表中：添加用户(User)、查找用户(userName,passWord)
package user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class UserDao {
	Connection conn = null;
	Statement st = null;
	PreparedStatement pstmt;
	ResultSet rs = null;

	public UserDao() {
		conn = new DBConnection().getConn();
	}

	public boolean addUser(User user) {
		String userName = user.getUserName();
		String passWord = user.getPassWord();
		String email = user.getEmail();
		int level = user.getLevel();
		Date regDate = user.getRegDate();

		try {
			st = conn.createStatement();
			rs = st.executeQuery("select * from user where name='" + userName + "'");
			if (rs.next()) {
				return false;
			} else {
				String sql = "insert into user(name,password,email,level,regDate)" + "values(?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, userName);
				pstmt.setString(2, passWord);
				pstmt.setString(3, email);
				pstmt.setInt(4, level);
				pstmt.setDate(5, new java.sql.Date(regDate.getTime()));// 转化为java.sql.Date()形式
				pstmt.executeUpdate();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public User getUser(String userName, String passWord) {
		try {
			st = conn.createStatement();
			String sql = "select * from user where name='" + userName + "'and passWord='" + passWord + "'";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				String email = rs.getString("email");
				int level = rs.getInt("level");
				Date regDate = rs.getDate("regDate");
				return new User(userName, passWord, email, level, regDate);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
