import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class AllyOrEnemyGui extends JFrame {

	private TableWithFooter twf = new TableWithFooter();

	private ChampionSummary myChampionSummary;

	private String playerName;

	private boolean ally;

	public AllyOrEnemyGui(boolean ally, String playerName, ChampionSummary myChampionSummary) {
		setLayout(new GridBagLayout());

		setTitle((ally ? "Ally": "Enemy")
				+ " stats when "
				+ playerName
				+ " played " + myChampionSummary.getChampionName());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.ally = ally;
		this.playerName = playerName;
		this.myChampionSummary = myChampionSummary;

		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.gridx = 0;
		c2.gridy = 0;
		//c2.weightx = .7;
		//c2.weighty = 1;
		//c2.gridheight = 1;
		add(twf, c2);

		updateAllyTable();

		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void updateAllyTable() {
		HashMap<String, ChampionSummary> map = getAllyMap();
		final String[] columnNames = {"Champion", "Win %", "Games Played", "Minutes Played", "First Seen", "Last Seen",
				"Blue Wins", "Red Wins"};
		final Object[][] data = new Object[map.size()][columnNames.length];

		ChampionSummary total = new ChampionSummary("Total");
		int i = 0;
		for (String name: map.keySet()){
			ChampionSummary championSummary = map.get(name);
			data[i][0] = championSummary.getChampionName();
			double knownWinLossGames = championSummary.totalWins() + championSummary.totalLosses();
			if (knownWinLossGames > 0) {
				data[i][1] = new Double(100d * (championSummary.totalWins() / knownWinLossGames));
			}
			total.incrementBlueTeamLossesBy(championSummary.getBlueTeamLosses());
			total.incrementRedTeamLossesBy(championSummary.getRedTeamLosses());
			total.incrementBlueTeamWinsBy(championSummary.getBlueTeamWins());
			total.incrementRedTeamWinsBy(championSummary.getRedTeamWins());

			data[i][2] = new Integer(championSummary.getGamesPlayed());
			total.incrementBlueGamesPlayedBy(championSummary.getBlueTeamGames());
			total.incrementRedGamesPlayedBy(championSummary.getRedTeamGames());

			data[i][3] = new Long(championSummary.getTimePlayed());
			total.incrementTimePlayedBy(championSummary.getTimePlayed());
			data[i][4] = new Date(championSummary.getFirstSeen());
			data[i][5] = new Date(championSummary.getLastSeen());
			if (championSummary.blueTeamGamesWithKnownOutcome() > 0) {
				data[i][6] = championSummary.getBlueTeamWins()
						+ "/"
						+ championSummary.blueTeamGamesWithKnownOutcome()
						+ "  ("
						+ PlayerStatsGui.DF.format(100d * ((double)championSummary.getBlueTeamWins() / (double)championSummary.blueTeamGamesWithKnownOutcome()))
						+ "%)";
			}
			if (championSummary.redTeamGamesWithKnownOutcome() > 0) {
				data[i][7] = championSummary.getRedTeamWins()
						+ "/"
						+ championSummary.redTeamGamesWithKnownOutcome()
						+ "  ("
						+ PlayerStatsGui.DF.format(100d * ((double)championSummary.getRedTeamWins() / (double)championSummary.redTeamGamesWithKnownOutcome()))
						+ "%)";
			}
			//update both first and last seen
			total.updateFirstLastSeen(championSummary.getFirstSeen());
			total.updateFirstLastSeen(championSummary.getLastSeen());
			i++;
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
		footerData[0][3] = new Long(total.getTimePlayed());
		footerData[0][4] = new Date(total.getFirstSeen());
		footerData[0][5] = new Date(total.getLastSeen());
		if (total.blueTeamGamesWithKnownOutcome() > 0) {
			footerData[0][6] = total.getBlueTeamWins()
					+ "/"
					+ total.blueTeamGamesWithKnownOutcome()
					+ "  ("
					+ PlayerStatsGui.DF.format(100d * ((double)total.getBlueTeamWins() / (double)total.blueTeamGamesWithKnownOutcome()))
					+ "%)";
		}
		if (total.redTeamGamesWithKnownOutcome() > 0) {
			footerData[0][7] = total.getRedTeamWins()
					+ "/"
					+ total.redTeamGamesWithKnownOutcome()
					+ "  ("
					+ PlayerStatsGui.DF.format(100d * ((double)total.getRedTeamWins() / (double)total.redTeamGamesWithKnownOutcome()))
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
				setText((value == null) ? "" : PlayerStatsGui.DF.format(value));
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

		for (i = 4; i < 6; i++) {
			twf.getTable().getColumnModel().getColumn(i).setCellRenderer(dateCellRenderer);
			twf.getFooterTable().getColumnModel().getColumn(i).setCellRenderer(dateCellRenderer);
		}

		twf.setAutoCreateRowSorter(true);

		twf.getTable().getRowSorter().toggleSortOrder(1);
		twf.getTable().getRowSorter().toggleSortOrder(1);

		twf.adjustColumns();
		pack();
		twf.adjustColumns();
		pack();
	}

	public HashMap<String, ChampionSummary> getAllyMap() {
		HashMap<String, ChampionSummary> map = new HashMap<String, ChampionSummary>();
		for (Game game : myChampionSummary.getGames()) {
			ArrayList<Player> myTeam;
			if (ally) {
				if (game.getLocalPlayer().getTeam() == Game.BLUE_TEAM) {
					myTeam = game.getBlueTeam();
				} else {
					myTeam = game.getRedTeam();
				}
			} else {
				if (game.getLocalPlayer().getTeam() != Game.BLUE_TEAM) {
					myTeam = game.getBlueTeam();
				} else {
					myTeam = game.getRedTeam();
				}
			}
			for (Player player : myTeam) {
				if (player.getName().equals(playerName)) continue; //exclude myself as an ally
				ChampionSummary cs = map.get(player.getChampionName());
				if (cs == null) {
					ChampionSummary newcs = new ChampionSummary(player.getChampionName(), game,
							player.getTeam(), player.getGameResult());
					map.put(newcs.getChampionName(), newcs);
				} else {
					cs.updateTeamInfo(player);
					cs.incrementTimePlayedBy(game.getGameLength());
					cs.updateFirstLastSeen(game.getEndTime());
					cs.addGame(game);
				}
			}
		}
		return map;
	}

}
