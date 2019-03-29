//测试用户类和用户管理类
package user;

import java.util.Date;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UserDao dao = new UserDao();
		User user = new User("test6", "1234", "tetsts", 1, new Date());
		System.out.println(new Date());
		if (dao.addUser(user))
			System.out.println("添加成功！");
		else
			System.out.println("添加失败！");

		User user2 = dao.getUser("test6", "1234");
		if (user2 != null) {
			System.out.println("找到该用户");
			System.out.println(user2);

		} else {
			System.out.println("未找到该用户");
		}

		User user3 = dao.getUser("test6", "ddd");
		if (user3 != null) {
			System.out.println("找到该用户");
			System.out.println(user3);
		} else {
			System.out.println("未找到该用户");
		}
	}

}
