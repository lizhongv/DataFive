package game;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GameDao dao = new GameDao();
		Game game = new Game("test5", "test6", new java.sql.Date(new java.util.Date().getTime()), "test5");
		// java.sql.Date()与java.util.Date()区别
		if (dao.addGame(game))
			System.out.println("添加成功！");
		else
			System.out.println("添加失败！");

		ArrayList<Game> game2 = dao.getGame("test5");
		if (game2 != null) {
			System.out.println("找到该用户");
			System.out.println(game2);

		} else {
			System.out.println("未找到该用户");
		}

		ArrayList<Game> game3 = dao.getGame("test8");
		if (game3 != null) {
			System.out.println("找到该用户");
			System.out.println(game3);
		} else {
			System.out.println("未找到该用户");
		}
	}

}
