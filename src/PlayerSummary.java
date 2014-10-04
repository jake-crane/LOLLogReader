import java.util.Comparator;
import java.util.HashMap;

public class PlayerSummary {
	
	private String name;
	private HashMap<String, ChampionSummary> championSummaries = new HashMap<String, ChampionSummary>();
	private int blueTeamWins = 0;
	private int blueTeamLosses = 0; //kept track of so undetermined outcomes can be calculated
	private int redTeamWins = 0;
	private int redTeamLosses = 0; //kept track of so undetermined outcomes can be calculated

	public static final Comparator<PlayerSummary> GAMES_PLAYED_COMPARATOR = new Comparator<PlayerSummary>() {
		@Override
		public int compare(PlayerSummary p1, PlayerSummary p2) {
			if (p1.championSummaries.size() < p2.getChampionSummaries().size()) {
				return 1;
			} else if (p1.championSummaries.size() > p2.getChampionSummaries().size()) {
				return -1;
			}
			return 0;
		}
	};
	
	public PlayerSummary(Player player, ChampionSummary firstFoundChampionSummary, long lastSeen) {
		this.name = player.getName();
		championSummaries.put(firstFoundChampionSummary.getChampionName(),firstFoundChampionSummary);
		updateTeamInfo(player);
	}

	public String getName() {
		return name;
	}

	public HashMap<String, ChampionSummary> getChampionSummaries() {
		return championSummaries;
	}

	public void updateTeamInfo(Player player) {
		if (player.getTeam() == Game.BLUE_TEAM) {
			incrementBlueGamsPlayed();
			if (player.getGameResult() == GameResult.WON) {
				incrementblueTeamWins();
			} else if (player.getGameResult() == GameResult.LOST) {
				incrementBlueTeamLosses();
			}
		} else if (player.getTeam() == Game.RED_TEAM) {
			incrementRedGamsPlayed();
			if (player.getGameResult() == GameResult.WON) {
				incrementRedTeamWins();
			} else if (player.getGameResult() == GameResult.LOST) {
				incrementRedTeamLosses();
			}
		}
	}
	
	/**
	 * Outcome of all games may no be known. <p>
	 * See also getRedTeamsGamesWithKnownOutcome()
	 * @return
	 */
	//public int getRedTeamGames() {
		//return redTeamGames;
	//}
	
	public void incrementRedGamsPlayed() {
		redTeamGames++;
	}
	
	public void incrementRedGamsPlayedBy(int amount) {
		redTeamGames += amount;
	}
	
	/**
	 * Outcome of all games may no be known. <p>
	 * See also getBlueTeamsGamesWithKnownOutcome()
	 * @return
	 */
	//public int getBlueTeamGames() {
		//return blueTeamGames;
	//}
	
	public void incrementBlueGamsPlayed() {
		blueTeamGames++;
	}
	
	public void incrementBlueGamsPlayedBy(int amount) {
		blueTeamGames += amount;
	}
	
	public void incrementblueTeamWins() {
		blueTeamWins++;
	}
	
	public void incrementBlueTeamLosses() {
		blueTeamLosses++;
	}
	
	public void incrementRedTeamWins() {
		redTeamWins++;
	}
	
	public void incrementRedTeamLosses() {
		redTeamLosses++;
	}
	
	public int getBlueTeamWins() {
		return blueTeamWins;
	}
	
	public int getBlueTeamLosses() {
		return blueTeamLosses;
	}
	
	public int getBlueTeamsGamesWithKnownOutcome() {
		return blueTeamWins + blueTeamLosses;
	}
	
	public int getRedTeamWins() {
		return redTeamWins;
	}
	
	public int getRedTeamLosses() {
		return redTeamLosses;
	}
	
	public int getRedTeamsGamesWithKnownOutcome() {
		return redTeamWins + redTeamLosses;
	}

	@Override
	public String toString() {
		return name;
	}

}