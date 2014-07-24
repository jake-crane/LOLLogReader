import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	private JLabel readingFilesLabel = new JLabel();
	private JTable table = new JTable();
	private JScrollPane tableScrollPane;
	private JScrollPane listScrollPane;
	private JList<PlayerSummary> jList = new JList<PlayerSummary>();
	private PlayerSummary[] playerSummaries;

	public Gui() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setLayout(new FlowLayout(FlowLayout.LEFT));

		setTitle("LOL Log Reader");
		setSize(740, 450);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		add(readingFilesLabel);

		listScrollPane = new JScrollPane(jList);
		listScrollPane.setVisible(false);
		listScrollPane.setPreferredSize(new Dimension(listScrollPane.getPreferredSize().width, 423));
		add(listScrollPane);

		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateChampionTable();
			}
		});

		table.setAutoCreateRowSorter(true);
		table.setFocusable(false);

		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setVisible(false);
		add(tableScrollPane);

		setVisible(true);
		pack();
		setLocationRelativeTo(null);
	}

	public JLabel getreadingFilesLabel() {
		return readingFilesLabel;
	}

	public void setPlayerSummaries(PlayerSummary[] playerSummaries) {
		readingFilesLabel.setVisible(false);
		listScrollPane.setVisible(true);
		tableScrollPane.setVisible(true);
		this.playerSummaries = playerSummaries;
		jList.setListData(playerSummaries);
		jList.setSelectedIndex(0);
		setLocationRelativeTo(null);
	}

	public void updateChampionTable() {

		ChampionSummary[] championSummaries = playerSummaries[jList.getSelectedIndex()].getChampionSummaries().toArray(new ChampionSummary[0]);

		final String[] columnNames = {"Champion", "Win %", "Games Played", "Minutes Played"};
		Object[][] data = new Object[championSummaries.length + 1][columnNames.length];

		ChampionSummary total = new ChampionSummary("Total");
		total.incrementGamesPlayedBy(-1); //remove the assumed played game

		for (int i = 0; i < championSummaries.length; i++) {
			data[i + 1][0] = championSummaries[i].getChampionName();
			int knownWinLossGames = championSummaries[i].getWins() + championSummaries[i].getLosses();
			if (championSummaries[i].getWins() + championSummaries[i].getLosses() > 0) {
				data[i + 1][1] = new Double(100d * ((double)championSummaries[i].getWins() / (double)knownWinLossGames));
			}
			total.incrementLossesBy(championSummaries[i].getLosses());
			total.incrementWinsBy(championSummaries[i].getWins());
			data[i + 1][2] = new Integer(championSummaries[i].getGamesPlayed());
			total.incrementGamesPlayedBy(championSummaries[i].getGamesPlayed());
			data[i + 1][3] = new Long(championSummaries[i].getMinutesPlayed());
			total.incrementMinutesPlayedBy(championSummaries[i].getMinutesPlayed());
		}

		data[0][0] = total.getChampionName();
		int knownWinLossGames = total.getWins() + total.getLosses();
		if (total.getWins() + total.getLosses() > 0) {
			data[0][1] = new Double(100d * ((double)total.getWins() / (double)knownWinLossGames));
		}
		data[0][2] = new Integer(total.getGamesPlayed());
		data[0][3] = new Long(total.getMinutesPlayed());

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(int column) {
				if (column == 1) {
					return Double.class;
				} else if (column == 2) {
					return Integer.class;
				} else if (column == 3) {
					return Long.class;
				}
				return String.class;
			}
		};

		table.setModel(model);
		table.getRowSorter().toggleSortOrder(2);
		table.getRowSorter().toggleSortOrder(2);
		pack();
	}

}
