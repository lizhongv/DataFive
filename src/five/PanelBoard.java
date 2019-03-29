
//创建棋盘类
package five;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PanelBoard extends JPanel {
	public static final int MARGIN = 15; // 边距
	public static final int SPAN = 20; // 网格宽度
	public static final int ROWS = 18; // 棋盘行数
	public static final int COLS = 18; // 棋盘列数
	Image img;
	public Chess[] chessList = new Chess[100]; // 记录棋盘上的棋子
	int chessCount; // 棋盘上的棋子个数
	boolean isBlack = true; // 下一步轮到哪一方下棋，默认开始是黑棋先
	boolean isGamming = false; // 是否正在游戏
	boolean isGoing = false;// 表示是否轮到自己下棋
	FiveClient fc;

	public PanelBoard(FiveClient fc) {
		this.fc = fc;
		img = Toolkit.getDefaultToolkit().getImage("img/board.jpg");

		this.addMouseListener(new MouseMonitor()); // 监听鼠标
		this.addMouseMotionListener((MouseMotionListener) new MouseMotionMonitor());
	}

	public Dimension getPreferredSize() { // 构造一个Dimension
		return new Dimension(MARGIN * 2 + SPAN * COLS, MARGIN * 2 + SPAN * ROWS);
	}

	class MouseMotionMonitor extends MouseMotionAdapter {// 内部监听类
		public void mouseMoved(MouseEvent e) { // 光标改变
			int col = (e.getX() - MARGIN + SPAN / 2) / SPAN;
			int row = (e.getY() - MARGIN + SPAN / 2) / SPAN;
			if (col < 0 || col > COLS || row < 0 || row > ROWS || !isGamming || hasChess(col, row))
				PanelBoard.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			else
				PanelBoard.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
	}

	class MouseMonitor extends MouseAdapter { // 内部监听类
		public void mousePressed(MouseEvent e) {
			if (!isGamming)
				return;
			if (!isGoing)
				return;
			// 将鼠标单击的像素坐标转换成网格索引
			int col = (e.getX() - MARGIN + SPAN / 2) / SPAN;
			int row = (e.getY() - MARGIN + SPAN / 2) / SPAN;
			// 落在棋盘外不能下棋
			if (col < 0 || col > COLS || row < 0 || row > ROWS)
				return;
			// 如果x，y位置已经有棋子存在，不能下棋
			if (hasChess(col, row))
				return;
			Chess ch = new Chess(PanelBoard.this, col, row, isBlack ? Color.black : Color.white);
			chessList[chessCount++] = ch;
			repaint(); // 通知系统重新绘制
			isGoing = false;
			fc.c.go(col, row);
			if (isWin(col, row)) {
				fc.c.wins();
				/*
				 * String colorName = isBlack ? "黑棋" : "白棋"; String msg =
				 * String.format("恭喜,%s 赢了!", colorName);
				 * JOptionPane.showMessageDialog(PanelBoard.this, msg); isGamming = false;
				 */
			}
			// isBlack = !isBlack;
		}
	}

	public void paintComponent(Graphics g) { // 画棋盘
		super.paintComponent(g);
		g.drawImage(img, 0, 0, this);
		for (int i = 0; i <= ROWS; i++) { // 画横线
			g.drawLine(MARGIN, MARGIN + i * SPAN, MARGIN + COLS * SPAN, MARGIN + i * SPAN);
		}
		for (int i = 0; i <= COLS; i++) { // 画竖线
			g.drawLine(MARGIN + i * SPAN, MARGIN, MARGIN + i * SPAN, MARGIN + ROWS * SPAN);
		}
		// 画9个黑方块
		g.fillRect(MARGIN + 3 * SPAN - 2, MARGIN + 3 * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + (COLS / 2) * SPAN - 2, MARGIN + 3 * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + (COLS - 3) * SPAN - 2, MARGIN + 3 * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + 3 * SPAN - 2, MARGIN + (ROWS / 2) * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + (COLS / 2) * SPAN - 2, MARGIN + (ROWS / 2) * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + (COLS - 3) * SPAN - 2, MARGIN + (ROWS / 2) * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + 3 * SPAN - 2, MARGIN + (ROWS - 3) * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + (COLS / 2) * SPAN - 2, MARGIN + (ROWS - 3) * SPAN - 2, 5, 5);
		g.fillRect(MARGIN + (COLS - 3) * SPAN - 2, MARGIN + (ROWS - 3) * SPAN - 2, 5, 5);

		for (int i = 0; i < chessCount; i++) { // 循环绘制棋子
			chessList[i].draw(g);
			if (i == chessCount - 1) { // 最后一个棋子
				int xPos = chessList[i].getCol() * SPAN + MARGIN;
				int yPos = chessList[i].getRow() * SPAN + MARGIN;
				g.setColor(Color.red);
				g.drawRect(xPos - Chess.DIAMETER / 2, yPos - Chess.DIAMETER / 2, Chess.DIAMETER, Chess.DIAMETER);
			}
		}
	}

	private boolean hasChess(int col, int row) { // 测试该位置是否已经有棋子
		for (int i = 0; i < chessCount; i++) {
			Chess ch = chessList[i];
			if (ch != null && ch.getCol() == col && ch.getRow() == row)
				return true;
		}
		return false;
	}

	// 判断某个点有没有黑子，或者白子
	private boolean hasChess(int col, int row, Color color) {
		for (int i = 0; i < chessCount; i++) {
			Chess ch = chessList[i];
			if (ch != null && ch.getCol() == col && ch.getRow() == row && ch.getColor() == color)
				return true;
		}
		return false;
	}

	private boolean isWin(int col, int row) { // 判断胜负的方法
		int continueCount = 1; // 连续棋子的个数
		Color c = isBlack ? Color.black : Color.white;

		for (int x = col - 1; x >= 0; x--) { // 横向向左查找
			if (hasChess(x, row, c))
				continueCount++;
			else
				break;
		}
		for (int x = col + 1; x <= COLS; x++) { // 横向向右查找
			if (hasChess(x, row, c))
				continueCount++;
			else
				break;
		}
		if (continueCount >= 5)
			return true;
		else
			continueCount = 1;

		for (int y = row - 1; y >= 0; y--) { // 纵向向上搜索
			if (hasChess(col, y, c))
				continueCount++;
			else
				break;
		}
		for (int y = row + 1; y <= ROWS; y++) { // 纵向向下搜索
			if (hasChess(col, y, c))
				continueCount++;
			else
				break;
		}
		if (continueCount >= 5)
			return true;
		else
			continueCount = 1;

		for (int x = col + 1, y = row - 1; y >= 0 && x <= COLS; x++, y--) {
			if (hasChess(x, y, c)) // 向右上寻找
				continueCount++;
			else
				break;
		}
		for (int x = col - 1, y = row + 1; x >= 0 && y <= ROWS; x--, y++) {
			if (hasChess(x, y, c)) // 向左下寻找
				continueCount++;
			else
				break;
		}
		if (continueCount >= 5)
			return true;
		else
			continueCount = 1;

		for (int x = col - 1, y = row - 1; x >= 0 && y >= 0; x--, y--) { // 向左上寻找
			if (hasChess(x, y, c))
				continueCount++;
			else
				break;
		}
		for (int x = col + 1, y = row + 1; x < COLS && y <= ROWS; x++, y++) {
			if (hasChess(x, y, c)) // 向右下寻找
				continueCount++;
			else
				break;
		}
		if (continueCount >= 5)
			return true;
		else
			return false;
	}

	public void addOpponentChess(int col, int row) {
		Chess ch = new Chess(this, col, row, isBlack ? Color.white : Color.black);
		chessList[chessCount++] = ch;
		isGoing = true;
		repaint();
	}

	public void winsGame() {
		resetGame();
		String colorName = isBlack ? "黑棋" : "白棋";
		String msg = String.format("恭喜，%s赢了!", colorName);
		JOptionPane.showMessageDialog(PanelBoard.this, msg);
	}

	public void lossesGame() {
		resetGame();
		String colorName = isBlack ? "黑棋" : "白棋";
		String msg = String.format("遗憾，%s输了！", colorName);
		JOptionPane.showMessageDialog(PanelBoard.this, msg);
	}

	public void resetGame() {
		chessCount = 0;
		isGamming = false;
		for (int i = 0; i < chessList.length; i++) {
			chessList[i] = null;
		}
		repaint();
		fc.control.joinGameButton.setEnabled(true);
	}

	public int getChessCount() {
		return chessCount;
	}

	public void setChessCount(int chessCount) {
		this.chessCount = chessCount;
	}

}
