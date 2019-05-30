package five;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import game.Game;
import game.GameDao;
import game.Manual;
import game.ManualDao;
import user.User;
import user.UserDao;

public class FiveServer extends Frame implements ActionListener {// 服务器端界面
	Label lStatus = new Label("当前连接数:0", Label.LEFT);// 客户端连接数
	TextArea taMessage = new TextArea("", 22, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);// 显示客户端行为
	Button btServerClose = new Button("关闭服务器");

	ServerSocket ss = null;// 服务器套接字
	public static final int TCP_PORT = 4801; // 服务器端口号常量
	static int clientNum = 0; // 记录当前连接到服务器上的客户端数量
	ArrayList<Client> clients = new ArrayList<Client>(); // 保存客户端信息的链表

	public static void main(String[] args) {
		FiveServer fs = new FiveServer();
		fs.startServser();
	}

	public FiveServer() {
		super("JAVA 五子棋服务器");
		btServerClose.addActionListener(this);
		add(lStatus, BorderLayout.NORTH);
		add(taMessage, BorderLayout.CENTER);
		add(btServerClose, BorderLayout.SOUTH);
		setLocation(500, 200);
		pack();
		setVisible(true);
		setResizable(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btServerClose) {
			System.exit(0);
		}
	}

	class Client { // 内部类：记录连接到服务器的客户端信息
		String name;
		Socket s;
		String state; // 1、ready 2、playing
		Client opponent;
		String chessColor;// 该客户端执棋的颜色
		int step;// 下棋的手数
		int[][] coordinates;// 每一步棋子的坐标

		public Client(String name, Socket s) {
			this.name = name;
			this.s = s;
			this.state = "ready";
			this.opponent = null;
			step = 0;
			coordinates = new int[19 * 19][2];
		}
	}

	class ClientThread extends Thread {// 内部线程类:服务器接收客户端消息
		private Client c;
		private DataInputStream dis;
		private DataOutputStream dos;

		ClientThread(Client c) {
			this.c = c;
		}

		public void run() {
			while (true) {
				try {
					dis = new DataInputStream(c.s.getInputStream());
					String msg = dis.readUTF();
					String[] words = msg.split(":");

					if (words[0].equals(Command.JOIN)) {// join：+opponentName：+playingTime
						String opponentName = words[1];
						String playingTime = words[2];
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).name.equals(opponentName)) {
								dos = new DataOutputStream(clients.get(i).s.getOutputStream());
								dos.writeUTF(Command.JOIN + ":" + c.name + ":" + playingTime);
								break;
							}
						}
					} else if (words[0].equals(Command.REFUSE)) {
						// c.state = "palying";
						String opponentName = words[1];
						for (int i = 0; i < clients.size(); i++) {
							dos = new DataOutputStream(clients.get(i).s.getOutputStream());
							dos.writeUTF(Command.REFUSE + ":" + c.name);
							break;
						}
					} else if (words[0].equals(Command.AGREE)) {
						c.state = "playing";
						String opponentName = words[1];
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i).name.equals(opponentName)) {
								clients.get(i).state = "playng";
								clients.get(i).opponent = c;
								c.opponent = clients.get(i);
								break;
							}
						}
						for (int i = 0; i < clients.size(); i++) {// 改变所有客户端中这两个客户的状态为playing
							dos = new DataOutputStream(clients.get(i).s.getOutputStream());
							dos.writeUTF(Command.CHANGE + ":" + c.name + ":playing");
							dos.writeUTF(Command.CHANGE + ":" + opponentName + ":playing");
						}
						int r = (int) (Math.random() * 2);// 随机分配黑旗、白棋
						if (r == 0) {
							dos = new DataOutputStream(c.s.getOutputStream());
							dos.writeUTF(Command.GUESSCOLOR + ":black:" + opponentName);
							dos = new DataOutputStream(c.opponent.s.getOutputStream());
							dos.writeUTF(Command.GUESSCOLOR + ":white:" + c.name);
							c.chessColor = "black";
							c.opponent.chessColor = "white";
						} else {
							dos = new DataOutputStream(c.s.getOutputStream());
							dos.writeUTF(Command.GUESSCOLOR + ":white:" + opponentName);
							dos = new DataOutputStream(c.opponent.s.getOutputStream());
							dos.writeUTF(Command.GUESSCOLOR + ":black:" + c.name);
							c.chessColor = "white";
							c.opponent.chessColor = "black";
						}
						taMessage.append(c.name + "  playing\n");
						taMessage.append(opponentName + "  playing\n");
					} else if (words[0].equals(Command.GO)) {
						dos = new DataOutputStream(c.opponent.s.getOutputStream());
						dos.writeUTF(msg);
						taMessage.append(c.name + " " + msg + "\n");

						String x = words[1];
						String y = words[2];
						c.coordinates[c.step][0] = Integer.valueOf(x);
						c.coordinates[c.step][1] = Integer.valueOf(y);
						c.step++;
						c.opponent.coordinates[c.opponent.step][0] = Integer.valueOf(x);
						c.opponent.coordinates[c.opponent.step][1] = Integer.valueOf(y);
						c.opponent.step++;
					} else if (words[0].equals(Command.WIN)) {
						for (int i = 0; i < clients.size(); i++) {// 改变所有客户端客户列表中这两个客户的状态为”ready“
							dos = new DataOutputStream(clients.get(i).s.getOutputStream());
							dos.writeUTF(Command.CHANGE + ":" + c.name + ":ready");
							dos.writeUTF(Command.CHANGE + ":" + c.opponent.name + ":ready");
						}
						dos = new DataOutputStream(c.s.getOutputStream());
						dos.writeUTF(Command.TELLRESULT + ":win");// 向自己发挥胜利的命令
						dos = new DataOutputStream(c.opponent.s.getOutputStream());
						dos.writeUTF(Command.TELLRESULT + ":losses");// 向对方发送失败的命令
						c.state = "ready";
						c.opponent.state = "ready";
						taMessage.append(c.name + "  win\n");
						taMessage.append(c.opponent.name + "  losses\n");

						Date date = new Date(new java.util.Date().getTime());
						recordGame(date);
						recordManual(date);
					} else if (words[0].equals(Command.GIVEUP)) {
						for (int i = 0; i < clients.size(); i++) {
							dos = new DataOutputStream(clients.get(i).s.getOutputStream());
							dos.writeUTF(Command.CHANGE + ":" + c.name + ":ready");
							dos.writeUTF(Command.CHANGE + ":" + c.opponent.name + ":ready");
						}
						dos = new DataOutputStream(c.s.getOutputStream());
						dos.writeUTF(Command.TELLRESULT + ":losses");
						dos = new DataOutputStream(c.opponent.s.getOutputStream());
						dos.writeUTF(Command.TELLRESULT + ":win");
						c.state = "ready";
						c.opponent.state = "ready";
						taMessage.append(c.name + "  loss\n");
						taMessage.append(c.opponent.name + "  win\n");
					} else if (words[0].equals(Command.QUIT)) {
						for (int i = 0; i < clients.size(); i++) {
							if (clients.get(i) != c) {
								dos = new DataOutputStream(clients.get(i).s.getOutputStream());
								dos.writeUTF(Command.DELETE + ":" + c.name);
							}
						}
						clients.remove(c);
						taMessage.append(c.name + "  quit\n");
						clientNum--;
						lStatus.setText("连接数:" + clientNum);
						return;
					} else if (words[0].equals(Command.GAME)) {
						ArrayList<Game> games;
						GameDao gd = new GameDao();
						String userName = words[1];
						games = gd.getGame(userName);
						DataOutputStream dos = new DataOutputStream(c.s.getOutputStream());
						dos.writeUTF(Command.GAME);
						ObjectOutputStream oos = new ObjectOutputStream(c.s.getOutputStream());
						oos.writeObject(games);
					} else if (words[0].equals(Command.MANUAL)) {
						Manual manual;
						ManualDao md = new ManualDao();
						String fileName = words[1];
						manual = md.getManual(fileName);
						if (manual != null) {
							DataOutputStream dos = new DataOutputStream(c.s.getOutputStream());
							dos.writeUTF(Command.MANUAL);
							ObjectOutputStream oos = new ObjectOutputStream(c.s.getOutputStream());
							oos.writeObject(manual);
						} else {
							// 向客户端发送未找到文件的错误
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}

		public void recordGame(Date date) {// 记录棋局
			String blackUser;
			String whiteUser;
			String winner;
			winner = c.name;
			if (c.chessColor.equals("black")) {
				blackUser = c.name;
				whiteUser = c.opponent.name;
			} else {
				blackUser = c.opponent.name;
				whiteUser = c.name;
			}
			Game game = new Game(blackUser, whiteUser, date, winner);
			GameDao gd = new GameDao();
			gd.addGame(game);
		}

		public void recordManual(Date date) {// 记录棋谱
			String blackUser;
			String whiteUser;
			String winner;
			winner = c.name;
			if (c.chessColor.equals("black")) {
				blackUser = c.name;
				whiteUser = c.opponent.name;
			} else {
				blackUser = c.opponent.name;
				whiteUser = c.name;
			}
			Manual manual = new Manual(blackUser, whiteUser, date, winner, c.step, c.coordinates);
			ManualDao md = new ManualDao();
			md.addManual(manual);
		}
	}

	public void startServser() { // 启动服务器
		try {
			ss = new ServerSocket(TCP_PORT);
			while (true) {
				Socket s = ss.accept();
				InputStream is = s.getInputStream();
				OutputStream os = s.getOutputStream();
				DataInputStream dis = new DataInputStream(is);
				DataOutputStream dos = new DataOutputStream(os);
				String msg = dis.readUTF();
				String[] words = msg.split(":");
				// 服务器接收客户端：登录、注册消息
				if (words[0].equals(Command.LOGIN)) {
					String userName = words[1];
					String passWord = words[2];

					boolean hasLogin = false;
					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						if (c.name.equalsIgnoreCase(userName)) {
							dos.writeUTF(Command.LOGIN + ":hasLogin");
							hasLogin = true;
							break;
						}
					}
					if (hasLogin) {
						s.close();
						continue;
					}

					UserDao ud = new UserDao();
					if (ud.getUser(userName, passWord) != null) {
						dos.writeUTF(Command.LOGIN + ":true");
						clientNum++;
						Client c = new Client(userName, s);
						clients.add(c);
						lStatus.setText("连接数:" + clientNum);
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
						taMessage.append(userName + "  login  " + s.getInetAddress().getHostAddress() + "  <"
								+ df.format(new java.util.Date()) + ">\n");
						tellName(c);
						addAllUserToMe(c);
						addMeToAllUser(c);
						new ClientThread(c).start();
					} else {
						dos.writeUTF(Command.LOGIN + ":false");
						s.close();
					}
				}
				if (words[0].equals(Command.REGISTER)) {// register：+ 用户
					ObjectInputStream ois = null;
					try {
						ois = new ObjectInputStream(is);
						User u = (User) ois.readObject();
						UserDao ud = new UserDao();
						if (ud.addUser(u)) {
							dos.writeUTF(Command.REGISTER + ":true");
						} else {
							dos.writeUTF(Command.REGISTER + ":false");
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						dis.close();
						ois.close();
						dos.close();
						s.close();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void tellName(Client c) {
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(c.s.getOutputStream());
			dos.writeUTF(Command.TELLNAME + ":" + c.name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addAllUserToMe(Client c) {
		DataOutputStream dos = null;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i) != c) {
				try {
					dos = new DataOutputStream(c.s.getOutputStream());
					dos.writeUTF(Command.ADD + ":" + clients.get(i).name + ": " + clients.get(i).state);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addMeToAllUser(Client c) {
		DataOutputStream dos = null;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i) != c) {
				try {
					dos = new DataOutputStream(clients.get(i).s.getOutputStream());
					dos.writeUTF(Command.ADD + ":" + c.name + ": ready");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
