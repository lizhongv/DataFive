
package five;

import java.awt.GridLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelTiming extends JPanel {// 创建计时面板类
	// 整体结构：2个JLabel标签（头像）、2个Panel面板（名字、时间）
	JLabel myIcon;// 己方头像
	JLabel opIcon;// 对手头像
	JLabel myName;// 己方名字
	JLabel opName;// 对手名字
	JLabel myTimer;// 己方剩余时间
	JLabel opTimer;// 对手剩余时间
	Icon blackIcon;
	Icon whiteIcon;

	public PanelTiming() {
		blackIcon = new ImageIcon("img/black.jpg");
		whiteIcon = new ImageIcon("img/white.jpg");

		this.myIcon = new JLabel(blackIcon, SwingConstants.CENTER); // 某区域的中心位置
		this.opIcon = new JLabel(whiteIcon, SwingConstants.CENTER);
		this.myName = new JLabel("My Name", SwingConstants.CENTER);
		this.opName = new JLabel("Op Name", SwingConstants.CENTER);
		this.myTimer = new JLabel("00:00:00");
		this.opTimer = new JLabel("00:00:00");

		JPanel myText = new JPanel(new GridLayout(2, 1));
		myText.add(myName);
		myText.add(myTimer);
		JPanel opText = new JPanel(new GridLayout(2, 1));
		opText.add(opName);
		opText.add(opTimer);
		this.add(myIcon);
		this.add(myText);
		this.add(opIcon);
		this.add(opText);
	}

	public void setMyIcon(String color) {
		if (color.equals("black")) {
			this.myIcon.setIcon(blackIcon);
		} else {
			this.myIcon.setIcon(whiteIcon);
		}
	}

	public void setOpIcon(String color) {
		if (color.equals("black")) {
			this.opIcon.setIcon(blackIcon);
		} else {
			this.opIcon.setIcon(whiteIcon);
		}
	}

	public void setMyName(String name) {
		this.myName.setText(name);
	}

	public void setOpName(String name) {
		this.opName.setText(name);
	}

	public void setMyTime(int time) {
		int h = time / 3600;
		int m = (time - h * 3600) / 60;
		int s = time % 60;
		this.myTimer.setText(h + ":" + m + ":" + s);
	}

	public void setOpTime(int time) {
		int h = time / 3600;
		int m = (time - h * 3600) / 60;
		int s = time % 60;
		this.opTimer.setText(h + ":" + m + ":" + s);
	}
}
