import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	public static final DecimalFormat df = new DecimalFormat("#.##");

	private JLabel readingFilesLabel = new JLabel();
	private JTable table = new JTable();
	private JScrollPane tableScrollPane;
	private JScrollPane listScrollPane;
	private JList<PlayerSummary> jList = new JList<PlayerSummary>();
	private PlayerSummary[] playerSummaries;
	private JPanel panel = new JPanel();
	private JLabel teamInfoLabel = new JLabel("<HTML>Blue Stats:<br>Red Stat:</HTML>");
	private TableColumnAdjuster tca = new TableColumnAdjuster(table);

	public Gui() {

		setLayout(new GridBagLayout());	

		GridBagConstraints c = new GridBagConstraints();

		setTitle("LOL Log Reader");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		add(readingFilesLabel);

		panel.add(teamInfoLabel);
		panel.setVisible(false);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.gridy = 1;
		c.gridheight = 1;
		c.weightx = 0;
		add(panel, c);

		listScrollPane = new JScrollPane(jList);
		listScrollPane.setVisible(false);


		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.weightx = .30;
		c.weighty = 100;
		add(listScrollPane, c);

		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateChampionTable();
			}
		});

		table.setAutoCreateRowSorter(true);
		table.setFocusable(false);

		tca.setColumnHeaderIncluded(true);
		tca.setOnlyAdjustLarger(true);

		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setVisible(false);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = .50;
		add(tableScrollPane, c);

		setVisible(true);
		setLocationRelativeTo(null);

	}

	public JLabel getreadingFilesLabel() {
		return readingFilesLabel;
	}

	public void setPlayerSummaries(PlayerSummary[] playerSummaries) {
		readingFilesLabel.setVisible(false);
		listScrollPane.setVisible(true);
		tableScrollPane.setVisible(true);
		panel.setVisible(true);
		this.playerSummaries = playerSummaries;
		jList.setListData(playerSummaries);
		jList.setSelectedIndex(0);
		setLocationRelativeTo(null);
	}

	public void updateChampionTable() {

		ChampionSummary[] championSummaries = playerSummaries[jList.getSelectedIndex()].getChampionSummaries().toArray(new ChampionSummary[0]);

		final String[] columnNames = {"Champion", "Win %", "Games Played", "Minutes Played", "Last Seen"};
		final Object[][] data = new Object[championSummaries.length + 1][columnNames.length];

		ChampionSummary total = new ChampionSummary("Total");
		total.incrementGamesPlayedBy(-1); //remove the assumed played game

		for (int i = 0; i < championSummaries.length; i++) {
			data[i + 1][0] = championSummaries[i].getChampionName();
			double knownWinLossGames = championSummaries[i].getWins() + championSummaries[i].getLosses();
			if (championSummaries[i].getWins() + championSummaries[i].getLosses() > 0) {
				data[i + 1][1] = new Double(100d * (championSummaries[i].getWins() / knownWinLossGames));
				//data[i + 1][1] = new Double(100d * (championSummaries[i].getWins() / knownWinLossGames)) + " " + championSummaries[i].getWins() + "/(" + championSummaries[i].getWins() + "+" + championSummaries[i].getLosses() + ")";
			}
			total.incrementLossesBy(championSummaries[i].getLosses());
			total.incrementWinsBy(championSummaries[i].getWins());
			data[i + 1][2] = new Integer(championSummaries[i].getGamesPlayed());
			total.incrementGamesPlayedBy(championSummaries[i].getGamesPlayed());
			data[i + 1][3] = new Long(championSummaries[i].getMinutesPlayed());
			total.incrementMinutesPlayedBy(championSummaries[i].getMinutesPlayed());
			data[i + 1][4] = new Date(championSummaries[i].getLastSeen());

			total.updateLastSeen(championSummaries[i].getLastSeen());

		}

		data[0][0] = total.getChampionName();
		double knownWinLossGames = total.getWins() + total.getLosses();
		if (total.getWins() + total.getLosses() > 0) {
			data[0][1] = new Double(100d * (total.getWins() / knownWinLossGames));
			//data[0][1] = new Double(100d * (total.getWins() / knownWinLossGames)) + " " + total.getWins() + "/(" + total.getWins() + "+" + total.getLosses() + ")";
		}
		data[0][2] = new Integer(total.getGamesPlayed());
		data[0][3] = new Long(total.getMinutesPlayed());
		data[0][4] = new Date(total.getLastSeen());

		DefaultTableModel model = new DefaultTableModel(data, columnNames) {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int column) {
				if (data[0][column] != null) {
					return data[0][column].getClass();
				}
				return String.class;
			}
		};

		table.setModel(model);
		table.getColumnModel().getColumn(4).setCellRenderer(
				new DefaultTableCellRenderer() {
					@Override
					public void setValue(Object value) {
						setText((value == null) ? "" : new SimpleDateFormat(" MM/dd/y").format(value));
					}
				}
		);

		table.getRowSorter().toggleSortOrder(2);
		table.getRowSorter().toggleSortOrder(2);

		teamInfoLabel.setText("<HTML>Blue Team Wins:"
				+ "&nbsp;&nbsp;"
				+ playerSummaries[jList.getSelectedIndex()].getBlueTeamWins()
				+ "/" + playerSummaries[jList.getSelectedIndex()].getBlueTeamGames()
				+ "&nbsp;&nbsp;"
				+ df.format(100.0d * (double)playerSummaries[jList.getSelectedIndex()].getBlueTeamWins() / (double)playerSummaries[jList.getSelectedIndex()].getBlueTeamGames())
				+ "%"
				+ "<br>Red Team Wins:"
				+ "&nbsp;&nbsp;"
				+ playerSummaries[jList.getSelectedIndex()].getRedTeamWins()
				+ "/" + playerSummaries[jList.getSelectedIndex()].getRedTeamGames()
				+ "&nbsp;&nbsp;"
				+ df.format(100.0d * (double)playerSummaries[jList.getSelectedIndex()].getRedTeamWins() / (double)playerSummaries[jList.getSelectedIndex()].getRedTeamGames())
				+ "%"
				+ "</HTML>");


		pack();
		tca.adjustColumns();
	}

}
