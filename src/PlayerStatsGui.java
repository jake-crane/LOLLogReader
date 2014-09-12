import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class PlayerStatsGui extends JFrame {

	public static final DecimalFormat DF = new DecimalFormat("#.##");

	private TableWithFooter twf = new TableWithFooter();
	private JScrollPane listScrollPane;
	private JList<PlayerSummary> jList = new JList<PlayerSummary>();
	private PlayerSummary[] playerSummaries;

	public PlayerStatsGui() {

		setLayout(new GridBagLayout());

		setTitle("LOL Log Reader");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateChampionTable();
			}
		});
		
		twf.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			long lastSelected = System.currentTimeMillis();
			@Override
			public void valueChanged(ListSelectionEvent e) {
				//limit this event to once every 1500 milliseconds
				if (System.currentTimeMillis() - lastSelected < 1500) return;
				lastSelected = System.currentTimeMillis();
				ChampionSummary championSummary = getChampionSummaryWithChampionName(
						jList.getSelectedValue().getChampionSummaries(),
						(String)twf.getTable().getValueAt(twf.getTable().getSelectedRow(), 0));
				new AllyOrEnemyGui(true, jList.getSelectedValue().getName(), championSummary).pack();
				new AllyOrEnemyGui(false, jList.getSelectedValue().getName(), championSummary).pack();
			}
		});

		listScrollPane = new JScrollPane(jList);

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.BOTH;
		c1.gridx = 0;
		c1.gridy = 0;
		c1.gridheight = 2;
		c1.weightx = .3;
		c1.weighty = 1;
		add(listScrollPane, c1);

		twf.setAutoCreateRowSorter(true);

		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.gridx = 1;
		c2.gridy = 0;
		c2.weightx = .7;
		c2.weighty = 1;
		c2.gridheight = 1;
		add(twf, c2);

	}
	
	public ChampionSummary getChampionSummaryWithChampionName(ArrayList<ChampionSummary> championSummaries,
			String name) {
		for (ChampionSummary championSummary : championSummaries) {
			if (championSummary.getChampionName().equals(name)) {
				return championSummary;
			}
		}
		return null;
	}

	public void setPlayerSummaries(PlayerSummary[] playerSummaries) {
		this.playerSummaries = playerSummaries;
		jList.setListData(playerSummaries);
		jList.setSelectedIndex(0);
		setLocationRelativeTo(null);
	}

	public void updateChampionTable() {

		ChampionSummary[] championSummaries = playerSummaries[jList.getSelectedIndex()].getChampionSummaries().toArray(new ChampionSummary[0]);

		final String[] columnNames = {"Champion", "Win %", "Games Played", "Minutes Played", "First Seen", "Last Seen",
				"Blue Wins", "Red Wins"};
		final Object[][] data = new Object[championSummaries.length][columnNames.length];

		ChampionSummary total = new ChampionSummary("Total");

		for (int i = 0; i < championSummaries.length; i++) {
			data[i][0] = championSummaries[i].getChampionName();
			double knownWinLossGames = championSummaries[i].totalWins() + championSummaries[i].totalLosses();
			if (knownWinLossGames > 0) {
				data[i][1] = new Double(100d * (championSummaries[i].totalWins() / knownWinLossGames));
			}
			total.incrementBlueTeamLossesBy(championSummaries[i].getBlueTeamLosses());
			total.incrementRedTeamLossesBy(championSummaries[i].getRedTeamLosses());
			total.incrementBlueTeamWinsBy(championSummaries[i].getBlueTeamWins());
			total.incrementRedTeamWinsBy(championSummaries[i].getRedTeamWins());

			data[i][2] = new Integer(championSummaries[i].getGamesPlayed());
			total.incrementBlueGamesPlayedBy(championSummaries[i].getBlueTeamGames());
			total.incrementRedGamesPlayedBy(championSummaries[i].getRedTeamGames());

			data[i][3] = new Long(championSummaries[i].getMinutesPlayed());
			total.incrementMinutesPlayedBy(championSummaries[i].getMinutesPlayed());
			data[i][4] = new Date(championSummaries[i].getFirstSeen());
			data[i][5] = new Date(championSummaries[i].getLastSeen());
			if (championSummaries[i].blueTeamGamesWithKnownOutcome() > 0) {
				data[i][6] = championSummaries[i].getBlueTeamWins()
						+ "/" 
						+ championSummaries[i].blueTeamGamesWithKnownOutcome()
						+ "  ("
						+ DF.format(100d * ((double)championSummaries[i].getBlueTeamWins() / (double)championSummaries[i].blueTeamGamesWithKnownOutcome()))
						+ "%)";
			}
			if (championSummaries[i].redTeamGamesWithKnownOutcome() > 0) {
				data[i][7] = championSummaries[i].getRedTeamWins()
						+ "/" 
						+ championSummaries[i].redTeamGamesWithKnownOutcome()
						+ "  ("
						+ DF.format(100d * ((double)championSummaries[i].getRedTeamWins() / (double)championSummaries[i].redTeamGamesWithKnownOutcome()))
						+ "%)";
			}
			//update both first and last seen
			total.updateFirstLastSeen(championSummaries[i].getFirstSeen());
			total.updateFirstLastSeen(championSummaries[i].getLastSeen());

		}

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

		Object[][] footerData = new Object[1][data[0].length];
		double knownWinLossGames = total.totalWins() + total.totalLosses();
		footerData[0][0] = total.getChampionName();
		if (knownWinLossGames > 0) {
			footerData[0][1] = new Double(100d * (total.totalWins() / knownWinLossGames));
		}
		footerData[0][2] = new Integer(total.getGamesPlayed());
		footerData[0][3] = new Long(total.getMinutesPlayed());
		footerData[0][4] = new Date(total.getFirstSeen());
		footerData[0][5] = new Date(total.getLastSeen());
		if (total.blueTeamGamesWithKnownOutcome() > 0) {
			footerData[0][6] = total.getBlueTeamWins()
					+ "/" 
					+ total.blueTeamGamesWithKnownOutcome()
					+ "  ("
					+ DF.format(100d * ((double)total.getBlueTeamWins() / (double)total.blueTeamGamesWithKnownOutcome()))
					+ "%)";
		}
		if (total.redTeamGamesWithKnownOutcome() > 0) {
			footerData[0][7] = total.getRedTeamWins()
					+ "/" 
					+ total.redTeamGamesWithKnownOutcome()
					+ "  ("
					+ DF.format(100d * ((double)total.getRedTeamWins() / (double)total.redTeamGamesWithKnownOutcome()))
					+ "%)";
		}

		DefaultTableModel footerModel = new DefaultTableModel(footerData, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		twf.setModel(model, footerModel);

		DefaultTableCellRenderer doubleCellRenderer = new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				setText((value == null) ? "" : DF.format(value));
			}
		};

		twf.getTable().getColumnModel().getColumn(1).setCellRenderer(doubleCellRenderer);
		twf.getFooterTable().getColumnModel().getColumn(1).setCellRenderer(doubleCellRenderer);

		DefaultTableCellRenderer dateCellRenderer = new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				setText((value == null) ? "" : new SimpleDateFormat(" MMM dd, y").format(value));
			}
		};

		for (int i = 4; i < 6; i++) {
			twf.getTable().getColumnModel().getColumn(i).setCellRenderer(dateCellRenderer);
			twf.getFooterTable().getColumnModel().getColumn(i).setCellRenderer(dateCellRenderer);
		}

		twf.getTable().getRowSorter().toggleSortOrder(2);
		twf.getTable().getRowSorter().toggleSortOrder(2);

		twf.adjustColumns();
		pack();
		twf.adjustColumns();
		pack();
	}

}
