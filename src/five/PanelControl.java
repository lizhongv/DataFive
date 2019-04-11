
//创建控制面板类
package five;

import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class PanelControl extends Panel {
	public Label IPlabel = new Label("服务器IP：", Label.LEFT);
	public TextField inputIP = new TextField("127.0.0.1", 12);
	public Button loginButton = new Button("登录");
	public Button talkButton = new Button("聊天");

	public Button joinGameButton = new Button("加入游戏");
	public Button cancelGameButton = new Button("放弃游戏");
	public Button chessManualButton = new Button("棋谱欣赏");
	public Button exitGameButton = new Button("关闭程序");

	public PanelControl() {
		setLayout(new FlowLayout(FlowLayout.LEFT));// 流式布局
		setBackground(new Color(200, 200, 200));// 设置背景颜色
		add(IPlabel);
		add(inputIP);
		add(loginButton);
		// add(talkButton);
		add(joinGameButton);
		add(cancelGameButton);
		add(chessManualButton);
		add(exitGameButton);
		add(talkButton);
	}
}
