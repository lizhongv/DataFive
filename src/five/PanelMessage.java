package five;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.TextArea;

public class PanelMessage extends Panel {// 创建信息面板类
	public TextArea messageArea;

	public PanelMessage() {
		setLayout(new BorderLayout());
		messageArea = new TextArea("", 12, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);// 12行、20列
		add(messageArea, BorderLayout.CENTER);
	}
}
