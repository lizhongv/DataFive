//添加棋局、查找棋局
package game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import user.DBConnection;

public class GameDao {
	Connection conn = null;
	PreparedStatement pst = null;
	Statement st = null;
	ResultSet rs;

	public GameDao() {
		conn = new DBConnection().getConn();
	}

	public boolean addGame(Game game) {
		try {
			String sql = "insert into game(gameDate,playerBlack,playerWhite,winner,manualFileName) values(?,?,?,?,?)";
			pst = conn.prepareStatement(sql);
			pst.setDate(1, new java.sql.Date(game.getDate().getTime()));
			pst.setString(2, game.getbUser());
			pst.setString(3, game.getwUer());
			pst.setString(4, game.getWinner());
			pst.setString(5, game.getFileName());
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<Game> getGame(String userName) {
		ArrayList<Game> games = new ArrayList<Game>();
		try {
			st = conn.createStatement();
			String sql = "select * from game where playerBlack='" + userName + "'" + "or playerWhite='" + userName
					+ "'";
			rs = st.executeQuery(sql);
			while (rs.next()) {
				String bUser = rs.getString("playerBlack");
				String wUser = rs.getString("playerWhite");
				java.sql.Date date = new java.sql.Date(rs.getDate("gameDate").getTime());
				String winner = rs.getString("winner");
				String fileName = rs.getString("manualFileName");
				Game g = new Game(bUser, wUser, date, winner, fileName);
				games.add(g);
			}
			return games;
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