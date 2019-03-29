//棋局对话框
package five;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import game.Game;

public class DialogGames extends JDialog implements ActionListener {
	private final int width = 500;
	private final int height = 400;
	private final int left = 100;
	private final int top = 100;
	FiveClient parent;
	JScrollPane mainPanel;
	JPanel southPanel = new JPanel();
	JTable tbGames;
	GamesTableModel model;
	JButton btnOK = new JButton("确定");
	JButton btnCancel = new JButton("取消");

	public DialogGames(FiveClient parent, ArrayList<Game> games) {
		super(parent, "选择棋局", true);
		this.parent = parent;
		setBounds(left, top, width, height);
		model = createTableModel(games);
		tbGames = new JTable(model);
		tbGames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mainPanel = new JScrollPane(tbGames);
		southPanel.add(btnOK);
		southPanel.add(btnCancel);
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);
		add(mainPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
		setVisible(true);
	}

	public GamesTableModel createTableModel(ArrayList<Game> games) {
		String[] strColumnName = new String[5];
		strColumnName[0] = "黑方";
		strColumnName[1] = "白方";
		strColumnName[2] = "日期";
		strColumnName[3] = "胜方";
		strColumnName[4] = "文件名";
		GamesTableModel model = null;
		System.out.println(games.size());
		model = new GamesTableModel(games, strColumnName);
		return model;
	}

	public class GamesTableModel extends DefaultTableModel {
		ArrayList<Game> games;

		public GamesTableModel(ArrayList<Game> games, String[] columnNames) {
			super(columnNames, games.size());
			this.games = games;
		}

		public Object getValueAt(int r, int c) {
			Game game = games.get(r);
			switch (c) {
			case 0:
				return game.getbUser();
			case 1:
				return game.getwUer();
			case 2:
				return game.getDate();
			case 3:
				return game.getWinner();
			case 4:
				return game.getFileName();
			default:
				return null;
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnOK) {
			int row = tbGames.getSelectedRow();
			String fileName = (String) tbGames.getValueAt(row, 4);
			this.dispose();

			parent.getC().getManual(fileName);
		} else {
			this.dispose();
		}
	}
}
