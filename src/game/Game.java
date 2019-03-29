//棋局类
package game;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

public class Game implements Serializable {// 启用序列化功能
	private static final long serialVersionUID = -1301902854300541648L;
	// 五个属性：黑色用户、白色用户、下棋日期、赢棋用户、保存棋谱的文件
	String bUser;
	String wUser;
	Date date;
	String winner;
	String fileName;

	public Game(String bUser, String wUser, Date date, String winner) {
		this.bUser = bUser;
		this.wUser = wUser;
		this.date = date;
		this.winner = winner;
		fileName = initFileName();
	}

	public Game(String bUser, String wUser, Date date, String winner, String fileName) {
		this.bUser = bUser;
		this.wUser = wUser;
		this.date = date;
		this.winner = winner;
		this.fileName = fileName;
	}

	private String initFileName() {
		String name;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		// 文件名格式：黑色用户_白色用户_年_月_日_时_分_秒_.fiv
		name = bUser + "_" + wUser + "_" + year + "_" + month + "_" + day + "_" + hour + "_" + minute + "_" + second
				+ "_" + ".fiv";
		return name;
	}

	public String getbUser() {
		return bUser;
	}

	public void setbUser(String bUser) {
		this.bUser = bUser;
	}

	public String getwUer() {
		return wUser;
	}

	public void setwUser(String wUser) {
		this.wUser = wUser;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFilename(String fileName) {
		this.fileName = fileName;
	}
}
