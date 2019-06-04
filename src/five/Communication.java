// 创建客户端接收和发送的信息类haha
package five;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import game.Game;
import game.Manual;

public class Communication {
	FiveClient fc;
	Socket s;
	TimerThread tt;
	private DataInputStream dis;
	private DataOutputStream dos;

	public Communication(FiveClient fc) {
		this.fc = fc;
	}

	class ReceaveThread extends Thread {// 内部线程类：用于客户端不断的接收服务器消息
		Socket s;
		private DataInputStream dis;
		private DataOutputStream dos;
		String msg;

		public ReceaveThread(Socket s) {
			this.s = s;
		}

		public void run() {
			while (true) {
				try {
					dis = new DataInputStream(s.getInputStream());
					dos = new DataOutputStream(s.getOutputStream());
					msg = dis.readUTF();
					String[] words = msg.split(":");
					// 客户端接收来自服务器的消息
					if (words[0].equals(Command.TELLNAME)) {// 用户列表、计时面板、信息面板
						fc.myname = words[1];
						fc.userList.userList.add(fc.myname + ": ready");
						fc.timing.setMyName(fc.myname);
						fc.message.messageArea.append("My name: " + fc.myname + " ready" + "\n");
					} else if (words[0].equals(Command.ADD)) {// 用户列表、信息面板
						fc.userList.userList.add(words[1] + ": " + words[2]);
						fc.message.messageArea.append(words[1] + ": " + words[2] + "\n");
					} else if (words[0].equals(Command.JOIN)) {
						String name = words[1];
						String strTime = words[2];
						TimeDialog d = new TimeDialog();
						fc.playingTime = Integer.parseInt(strTime);
						int select = d.showDialog(fc, name + " 邀请您下棋(用时" + strTime + "秒)是否接受 ？", 100);
						if (select == 0) {
							dos.writeUTF(Command.AGREE + ":" + name);
						} else {
							dos.writeUTF(Command.REFUSE + ":" + name);
						}
					} else if (words[0].equals(Command.REFUSE)) {
						String name = words[1];
						JOptionPane.showMessageDialog(fc, name + "拒绝了您的邀请!");
					} else if (words[0].equals(Command.CHANGE)) {
						String name = words[1];
						String state = words[2];
						for (int i = 0; i < fc.userList.userList.getItemCount(); i++) {
							if (fc.userList.userList.getItem(i).startsWith(name)) {
								fc.userList.userList.replaceItem(name + ":" + state, i);
							}
						}
						fc.message.messageArea.append(name + " " + state + "\n");
					} else if (words[0].equals(Command.GUESSCOLOR)) {
						String color = words[1];
						String oppName = words[2];
						fc.board.isGamming = true;
						fc.opname = oppName;
						fc.timing.setOpName(oppName);
						if (color.equals("black")) {
							fc.timing.setMyIcon("black");
							fc.timing.setOpIcon("white");
							fc.board.isBlack = true;
							fc.board.isGoing = true;
						} else if (color.equals("white")) {
							fc.timing.setMyIcon("white");
							fc.timing.setOpIcon("black");
							fc.board.isBlack = false;
							fc.board.isGoing = false;
						}
						fc.control.joinGameButton.setEnabled(false);
						fc.control.cancelGameButton.setEnabled(true);
						fc.control.exitGameButton.setEnabled(false);
						fc.message.messageArea.append("My color is " + color + "\n");

						tt = new TimerThread(fc, fc.playingTime);
						tt.start();
					} else if (words[0].equals(Command.GO)) {
						int col = Integer.parseInt(words[1]);
						int row = Integer.parseInt(words[2]);
						fc.board.addOpponentChess(col, row);
					} else if (words[0].equals(Command.TELLRESULT)) {
						if (words[1].equals("win"))
							fc.board.winsGame();
						else
							fc.board.lossesGame();
						fc.control.joinGameButton.setEnabled(true);
						fc.control.cancelGameButton.setEnabled(false);
						fc.control.exitGameButton.setEnabled(true);
					} else if (words[0].equals(Command.DELETE)) {
						for (int i = 0; i < fc.userList.userList.getItemCount(); i++) {
							String name = fc.userList.userList.getItem(i);
							if (name.startsWith(words[1])) {
								fc.userList.userList.remove(i);
							}
						}
						fc.message.messageArea.append(words[1] + "disconnected" + "\n");
					} else if (words[0].equals(Command.GAME)) {
						ArrayList<Game> games;
						ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
						try {
							games = (ArrayList<Game>) ois.readObject();
							fc.showGames(games);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (words[0].equals(Command.MANUAL)) {
						Manual manual;
						ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
						try {
							manual = (Manual) ois.readObject();
							fc.playBack(manual);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (words[0].equals(Command.TALK)) {
						fc.message.messageArea.append(words[1] + ":" + words[3] + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public boolean login(String iP, String userName, String passWord) {// 用于客户端连接服务器：登录
		try {
			s = new Socket(iP, FiveServer.TCP_PORT);
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(Command.LOGIN + ":" + userName + ":" + passWord);
			String msg = dis.readUTF();
			String[] words = msg.split(":");
			if (words[0].equals(Command.LOGIN) && words[1].equals("true")) {
				fc.isConnected = true;
				new ReceaveThread(s).start();
				fc.control.loginButton.setEnabled(false);
				fc.control.joinGameButton.setEnabled(true);
				fc.control.cancelGameButton.setEnabled(false);
				fc.control.chessManualButton.setEnabled(true);
				fc.control.exitGameButton.setEnabled(true);
				fc.control.talkButton.setEnabled(true);
				return true;
			} else if (words[0].equals(Command.LOGIN) && (words[1].equals("hasLogin"))) {
				s.close();
				JOptionPane.showMessageDialog(fc, "该用户已经登录!");
				return false;
			} else {
				s.close();
				JOptionPane.showMessageDialog(fc, "用户名或密码不符!");
				return false;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}
		return false;
	}

	public void join(String opponentName, String time) {
		try {
			dos.writeUTF(Command.JOIN + ":" + opponentName + ":" + time);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void talk() {
		String strMessage = JOptionPane.showInputDialog("请输入您要发送的消息：");
		try {
			dos.writeUTF(Command.TALK + ":" + strMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void go(int col, int row) {
		try {
			String msg = Command.GO + ":" + col + ":" + row;
			dos.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void wins() {
		try {
			dos.writeUTF(Command.WIN);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void giveup() {
		try {
			dos.writeUTF(Command.GIVEUP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disConnect() {
		try {
			dos.writeUTF(Command.QUIT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getGames(String myName) {
		try {

			String msg = Command.GAME + ":" + myName;
			dos.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getManual(String fileName) {
		try {
			String msg = Command.MANUAL + ":" + fileName;
			dos.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
