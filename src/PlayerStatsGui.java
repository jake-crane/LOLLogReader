import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

	private JRadioButton anyvAnyRadio = new JRadioButton("Any v Any");
	private JRadioButton sixvSixRadio = new JRadioButton("6v6");
	private JRadioButton fivevFiveRadio = new JRadioButton("5v5", true);
	private JRadioButton fourvFourRadio = new JRadioButton("4v4");
	private JRadioButton threevThreeRadio = new JRadioButton("3v3");
	private JRadioButton twovTwoRadio = new JRadioButton("2v2");
	private JRadioButton onevOneRadio = new JRadioButton("1v1");

	private JCheckBox showBotGamesCheckBox = new JCheckBox("View Bot Games");

	private DatePicker fromDatePicker = new DatePicker();
	private DatePicker toDatePicker = new DatePicker();

	private Game[] games = null;

	private GameFilter teamSizeFilter = null;
	private GameFilter dateFilter = null;

	private String lastSelectedPlayerName;

	public PlayerStatsGui(Game[] games) {

		this.games = games;

		setLayout(new GridBagLayout());

		setTitle("LOL Log Reader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JRadioButton[] radioButtons = {anyvAnyRadio, sixvSixRadio, fivevFiveRadio, fourvFourRadio,
				threevThreeRadio, twovTwoRadio, onevOneRadio};

		ButtonGroup group = new ButtonGroup();
		JPanel optionPanel = new JPanel();
		for (final JRadioButton r : radioButtons) {
			group.add(r);
			optionPanel.add(r);
			r.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						updatePlayerSummaries(); //updateChampionTable will be called when playersummary index gets set
					}
				}
			});
		}

		optionPanel.add(showBotGamesCheckBox);
		showBotGamesCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePlayerSummaries(); //updateChampionTable will be called when playersummary index gets set
			}
		});

		optionPanel.add(new JLabel("From:"));
		fromDatePicker.setDate(1970, 0, 1, 0, 0, 0);
		optionPanel.add(fromDatePicker);

		optionPanel.add(new JLabel("To:"));
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		toDatePicker.setDate(c.getTime());
		toDatePicker.setTimeofDayToZero();
		optionPanel.add(toDatePicker);

		GridBagConstraints c3 = new GridBagConstraints();
		c3.fill = GridBagConstraints.HORIZONTAL;
		c3.gridx = 0;
		c3.gridy = 0;
		c3.gridheight = 1;
		c3.gridwidth = 2;
		add(optionPanel, c3);

		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateChampionTable();
			}
		});

		jList.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (jList.getSelectedValue() != null) {
					//Remember selection if user chose a player name
					lastSelectedPlayerName = jList.getSelectedValue().getName();
				}
			}
		});

		listScrollPane = new JScrollPane(jList);

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.VERTICAL;
		c1.gridx = 0;
		c1.gridy = 1;
		c1.gridheight = 2;
		c1.weightx = .3;
		c1.weighty = 1;
		add(listScrollPane, c1);

		twf.setAutoCreateRowSorter(true);

		GridBagConstraints c2 = new GridBagConstraints();
		c2.fill = GridBagConstraints.BOTH;
		c2.gridx = 1;
		c2.gridy = 1;
		c2.weightx = .7;
		c2.weighty = 1;
		c2.gridheight = 1;
		add(twf, c2);

		updatePlayerSummaries();

		toDatePicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePlayerSummaries(); //updateChampionTable will be called when playersummary index gets set
			}
		});
		fromDatePicker.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updatePlayerSummaries(); //updateChampionTable will be called when playersummary index gets set
			}
		});

		twf.getTable().addMouseListener(new MouseAdapter() { //add right click select and menu
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = twf.getTable().rowAtPoint(e.getPoint());
				if (r >= 0 && r < twf.getTable().getRowCount()) {
					twf.getTable().setRowSelectionInterval(r, r);
				} else {
					twf.getTable().clearSelection();
				}

				int rowindex = twf.getTable().getSelectedRow();
				if (rowindex < 0)
					return;
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
					JPopupMenu popup = new MyMenu(jList, twf.getTable());
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		Date firstGameDate = (Date)twf.getFooterTable().getValueAt(0, 5);
		fromDatePicker.setDate(firstGameDate);
		fromDatePicker.setTimeofDayToZero();
	}

	public static void summarizePlayerForAllPlayers(Game game, Player player, HashMap<String, PlayerSummary> playerSummaries) {
		PlayerSummary playerSummary = playerSummaries.get("(All Players)");
		if (playerSummary == null) { //user does not exist create new and add to list with champ
			ChampionSummary championSummary = new ChampionSummary(player.getChampionName(), game, player.getTeam(), player.getGameResult());
			playerSummary = new PlayerSummary("(All Players)", championSummary);
			playerSummaries.put(playerSummary.getName(), playerSummary);
		} else { //user found get list of champions and add new champion info
			HashMap<String, ChampionSummary> championsummaries = playerSummary.getChampionSummaries();
			ChampionSummary championSummary = championsummaries.get(player.getChampionName());
			if (championSummary == null) { //champion does not exist for this user
				championSummary = new ChampionSummary(player.getChampionName(), game, player.getTeam(), player.getGameResult());
				championsummaries.put(championSummary.getChampionName(), championSummary);
			} else { //user has used this champion before
				championSummary.updateTeamInfo(player);
				championSummary.incrementTimePlayedBy(game.getGameLength());
				championSummary.updateFirstLastSeen(game.getEndTime());
				championSummary.addGame(game);
			}
		}
	}

	public static void summarizePlayer(Game game, Player player, HashMap<String, PlayerSummary> playerSummaries) {
		PlayerSummary playerSummary = playerSummaries.get(player.getName());
		if (playerSummary == null) { //user does not exist create new and add to list with champ
			ChampionSummary championSummary = new ChampionSummary(player.getChampionName(), game, player.getTeam(), player.getGameResult());
			playerSummary = new PlayerSummary(player.getName(), championSummary);
			playerSummaries.put(playerSummary.getName(), playerSummary);
		} else { //user found get list of champions and add new champion info
			HashMap<String, ChampionSummary> championsummaries = playerSummary.getChampionSummaries();
			ChampionSummary championSummary = championsummaries.get(player.getChampionName());
			if (championSummary == null) { //champion does not exist for this user
				championSummary = new ChampionSummary(player.getChampionName(), game, player.getTeam(), player.getGameResult());
				championsummaries.put(championSummary.getChampionName(), championSummary);
			} else { //user has used this champion before
				championSummary.updateTeamInfo(player);
				championSummary.incrementTimePlayedBy(game.getGameLength());
				championSummary.updateFirstLastSeen(game.getEndTime());
				championSummary.addGame(game);
			}
		}
	}

	/**
	 * updateChampionTable will also be called when jList index gets set
	 */
	public void updatePlayerSummaries() {
		if (anyvAnyRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return true;
				}
			};
		} else if (sixvSixRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return game.getBlueTeam().size() == 6 && game.getRedTeam().size() == 6;
				}
			};
		} else if (fivevFiveRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return game.getBlueTeam().size() == 5 && game.getRedTeam().size() == 5;
				}
			};
		} else if (fourvFourRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return game.getBlueTeam().size() == 4 && game.getRedTeam().size() == 4;
				}
			};
		} else if (threevThreeRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return game.getBlueTeam().size() == 3 && game.getRedTeam().size() == 3;
				}
			};
		} else if (twovTwoRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return game.getBlueTeam().size() == 2 && game.getRedTeam().size() == 2;
				}
			};
		} else if (onevOneRadio.isSelected()) {
			teamSizeFilter = new GameFilter() {
				@Override
				public boolean accept(Game game) {
					return game.getBlueTeam().size() == 1 && game.getRedTeam().size() == 1;
				}
			};
		}

		dateFilter = new GameFilter() {
			@Override
			public boolean accept(Game game) {
				return game.getStartTime() >= fromDatePicker.getDate().getTime()
						&& game.getStartTime() < toDatePicker.getDate().getTime();
			}
		};

		HashMap<String, PlayerSummary> playerSummaryHashMap = new HashMap<String, PlayerSummary>();
		for (Game game : games) {
			if (game.isBotGame() == showBotGamesCheckBox.isSelected()
					&& teamSizeFilter.accept(game)
					&& dateFilter.accept(game)
					) {
				for (Player player : game.getBlueTeam()) {
					summarizePlayer(game, player, playerSummaryHashMap);
					summarizePlayerForAllPlayers(game, player, playerSummaryHashMap);
				}
				for (Player player : game.getRedTeam()) {
					summarizePlayer(game, player, playerSummaryHashMap);
					summarizePlayerForAllPlayers(game, player, playerSummaryHashMap);
				}
			}
		}

		PlayerSummary[] playerSummaries = playerSummaryHashMap.values().toArray(new PlayerSummary[0]);

		Arrays.sort(playerSummaries, PlayerSummary.GAMES_PLAYED_COMPARATOR);

		if (playerSummaries.length == 0) {
			//DefaultTableModel model = new DefaultTableModel(new String[][]{{}}, new String[][]{{}});
			//twf.setModel(model, model);
			twf.getTable().setVisible(false);
			twf.getFooterTable().setVisible(false);
			jList.setListData(new PlayerSummary[0]);
			return;
		}

		twf.getTable().setVisible(true);
		twf.getFooterTable().setVisible(true);

		jList.setListData(playerSummaries);

		for (PlayerSummary ps : playerSummaries) {
			if (ps.getName().equals(lastSelectedPlayerName)) {
				jList.setSelectedValue(ps, true);
				break;
			}
		}

		if (jList.getSelectedIndex() == -1) {
			jList.setSelectedIndex(0);
		}

	}

	public void updateChampionTable() {

		if (jList.getSelectedValue() == null) return;

		ChampionSummary[] championSummaries = jList.getSelectedValue().getChampionSummaries().values().toArray(new ChampionSummary[0]);

		final String[] columnNames = {"Champion", "Win %", "Games Played", "Time Played", "Avg Game Time", "First Seen", "Last Seen",
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

			data[i][3] = new Long(championSummaries[i].getTimePlayed());
			total.incrementTimePlayedBy(championSummaries[i].getTimePlayed());
			data[i][4] = new Long((long) (championSummaries[i].getTimePlayed() / (double)championSummaries[i].getGamesPlayed()));
			data[i][5] = new Date(championSummaries[i].getFirstSeen());
			data[i][6] = new Date(championSummaries[i].getLastSeen());
			if (championSummaries[i].blueTeamGamesWithKnownOutcome() > 0) {
				data[i][7] = championSummaries[i].getBlueTeamWins()
						+ "/"
						+ championSummaries[i].blueTeamGamesWithKnownOutcome()
						+ "  ("
						+ DF.format(100d * ((double)championSummaries[i].getBlueTeamWins() / (double)championSummaries[i].blueTeamGamesWithKnownOutcome()))
						+ "%)";
			}
			if (championSummaries[i].redTeamGamesWithKnownOutcome() > 0) {
				data[i][8] = championSummaries[i].getRedTeamWins()
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
		footerData[0][3] = new Long(total.getTimePlayed());
		footerData[0][4] = new Long((long) (total.getTimePlayed() / (double)total.getGamesPlayed()));
		footerData[0][5] = new Date(total.getFirstSeen());
		footerData[0][6] = new Date(total.getLastSeen());
		if (total.blueTeamGamesWithKnownOutcome() > 0) {
			footerData[0][7] = total.getBlueTeamWins()
					+ "/"
					+ total.blueTeamGamesWithKnownOutcome()
					+ "  ("
					+ DF.format(100d * ((double)total.getBlueTeamWins() / (double)total.blueTeamGamesWithKnownOutcome()))
					+ "%)";
		}
		if (total.redTeamGamesWithKnownOutcome() > 0) {
			footerData[0][8] = total.getRedTeamWins()
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
			@Override
			public Class<?> getColumnClass(int column) {
				if (data[0][column] != null) {
					return data[0][column].getClass();
				}
				return String.class;
			}
		};

		twf.setModel(model, footerModel);

		DefaultTableCellRenderer doubleCellRenderer = new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				setText((value == null) ? "" : DF.format(value));
			}
		};
		doubleCellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);

		DefaultTableCellRenderer timePlayedCellRenderer = new DefaultTableCellRenderer() {

			private String formatTime(long milliseconds) {
				final int days = (int)(milliseconds / (1000 * 60 * 60 * 24));
				final int hours = (int)(milliseconds / (1000 * 60 * 60) % 24);
				final int minutes = (int)(milliseconds / (1000 * 60) % 60);
				final int seconds = (int)(milliseconds / 1000 % 60);
				return (
						(days < 10 ? "0" + days : Integer.toString(days)) + "d"
						+ ":" + (hours < 10 ? "0" + hours : Integer.toString(hours)) + "h"
						+ ":" + (minutes < 10 ? "0" + minutes : Integer.toString(minutes)) + "m"
						+ ":" + (seconds < 10 ? "0" + seconds : Integer.toString(seconds)) + "s"
						);
			}

			@Override
			public void setValue(Object value) {
				Long timePlayed = (Long)value;
				setText(formatTime(timePlayed.longValue()));
			}
		};

		DefaultTableCellRenderer gameTimeCellRenderer = new DefaultTableCellRenderer() {

			private String formatTime(long milliseconds) {
				final int minutes = (int)(milliseconds / (1000 * 60));
				final int seconds = (int)(milliseconds / 1000 % 60);
				return (
						(minutes < 10 ? "0" + minutes : Integer.toString(minutes)) + "m"
						+ ":" + (seconds < 10 ? "0" + seconds : Integer.toString(seconds)) + "s"
						);
			}

			@Override
			public void setValue(Object value) {
				Long timePlayed = (Long)value;
				setText(formatTime(timePlayed.longValue()));
			}
		};

		twf.getTable().getColumnModel().getColumn(1).setCellRenderer(doubleCellRenderer);
		twf.getFooterTable().getColumnModel().getColumn(1).setCellRenderer(doubleCellRenderer);

		twf.getTable().getColumnModel().getColumn(3).setCellRenderer(timePlayedCellRenderer);
		twf.getFooterTable().getColumnModel().getColumn(3).setCellRenderer(timePlayedCellRenderer);

		twf.getTable().getColumnModel().getColumn(4).setCellRenderer(gameTimeCellRenderer);
		twf.getFooterTable().getColumnModel().getColumn(4).setCellRenderer(gameTimeCellRenderer);

		DefaultTableCellRenderer dateCellRenderer = new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				setText((value == null) ? "" : new SimpleDateFormat(" MMM dd, y").format(value));
			}
		};

		for (int i = 5; i < 7; i++) {
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

	private static interface GameFilter {
		abstract boolean accept(Game game);
	}

	private static class MyMenu extends JPopupMenu {

		//TODO try to combine actionlisteners before commit
		public MyMenu(final JList<PlayerSummary> jList, final JTable table) {

			JMenuItem AllyStatsItem = new JMenuItem("Ally Stats");
			AllyStatsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(null,
								"No Champion Selected",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					ChampionSummary championSummary = jList.getSelectedValue().getChampionSummaries().get(
							table.getValueAt(table.getSelectedRow(), 0)
							);
					new AllyOrEnemyGui(true, jList.getSelectedValue().getName(), championSummary).pack();
				}
			});
			add(AllyStatsItem);

			JMenuItem enemyStatsItem = new JMenuItem("Enamy Stats");
			enemyStatsItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(null,
								"No Champion Selected",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					ChampionSummary championSummary = jList.getSelectedValue().getChampionSummaries().get(
							table.getValueAt(table.getSelectedRow(), 0)
							);
					new AllyOrEnemyGui(false, jList.getSelectedValue().getName(), championSummary).pack();
				}
			});
			add(enemyStatsItem);

			add(new JMenuItem("cancel"));

		}
	}
}
