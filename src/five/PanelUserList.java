
package five;

import java.awt.BorderLayout;
import java.awt.List;
import java.awt.Panel;

public class PanelUserList extends Panel {// 创建用户列表类
	public List userList = new List(8);// 8行

	public PanelUserList() {
		setLayout(new BorderLayout());
		add(userList, BorderLayout.CENTER);
	}
}
