package five;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import game.DialogPlayback;
import game.Game;
import game.Manual;
import user.DialogLogin;

public class FiveClient extends Frame {// 创建客户端窗口
	PanelBoard board; // 棋盘
	PanelUserList userList; // 用户列表
	PanelMessage message; // 信息面板
	PanelTiming timing; // 计时面板
	PanelControl control; // 控制面板

	String myname; // 自己的用户名
	String opname; // 对手的用户名
	public boolean isConnected = false; // 当前是否连接到服务器
	Communication c = null;

	public static void main(String[] args) {
		FiveClient fc = new FiveClient();
	}

	public FiveClient() { // 窗口分成三个区域：中、南、东
		super("五子棋客户端");
		board = new PanelBoard(this);
		timing = new PanelTiming();
		userList = new PanelUserList();
		message = new PanelMessage();
		control = new PanelControl();

		Panel east = new Panel();// 东：计时面板+用户列表+信息面板
		east.setLayout(new BorderLayout());
		east.add(userList, BorderLayout.CENTER);
		east.add(message, BorderLayout.SOUTH);
		east.add(timing, BorderLayout.NORTH);

		this.add(east, BorderLayout.EAST);
		this.add(control, BorderLayout.SOUTH);
		this.add(board, BorderLayout.CENTER);

		this.setLocation(300, 100);
		pack();
		this.setResizable(false);
		this.setVisible(true);

		ActionMonitor monitor = new ActionMonitor();
		control.exitGameButton.addActionListener(monitor);
		control.loginButton.addActionListener(monitor);
		control.joinGameButton.addActionListener(monitor);
		control.chessManualButton.addActionListener(monitor);
		control.cancelGameButton.addActionListener(monitor);

		control.loginButton.setEnabled(true);
		control.joinGameButton.setEnabled(false);
		control.cancelGameButton.setEnabled(false);
		control.chessManualButton.setEnabled(false);
		control.exitGameButton.setEnabled(true);
	}

	class ActionMonitor implements ActionListener { // 内部监听类
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == control.exitGameButton) {
				if (isConnected) {
					c.disConnect();
				}
				System.exit(0);
			} else if (e.getSource() == control.loginButton) {
				c = new Communication(FiveClient.this);
				DialogLogin dr = new DialogLogin(FiveClient.this, FiveClient.this.control.inputIP.getText());
			} else if (e.getSource() == control.joinGameButton) {
				String select = userList.userList.getSelectedItem();
				if (select == null) {
					message.messageArea.append("请选择一个对手" + "\n");
					return;
				}
				if (!select.endsWith("ready")) {
					message.messageArea.append("请选择ready状态的对手" + "\n");
					return;
				}
				if (select.startsWith(myname)) {
					message.messageArea.append("不能选自己作为对手" + "\n");
					return;
				}
				int index = select.lastIndexOf(":");
				String name = select.substring(0, index);
				join(name);
			} else if (e.getSource() == control.cancelGameButton) {
				c.giveup();
			} else if (e.getSource() == control.chessManualButton) {
				c.getGames(myname);
			}
		}
	}

	public void join(String opponentName) {
		c.join(opponentName);
	}

	public Communication getC() {
		return c;
	}

	public void showGames(ArrayList<Game> games) {
		DialogGames dg = new DialogGames(this, games);
	}

	public void playBack(Manual manual) {
		DialogPlayback dp = new DialogPlayback(this, manual);
	}
}
