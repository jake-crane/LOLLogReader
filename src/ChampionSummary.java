import java.util.ArrayList;

public class ChampionSummary {

	private String championName;
	private int blueTeamGames = 0;
	private int redTeamGames = 0;
	private int blueTeamWins = 0;
	private int blueTeamLosses = 0; //kept track of so undetermined outcomes can be calculated
	private int redTeamWins = 0;
	private int redTeamLosses = 0; //kept track of so undetermined outcomes can be calculated
	private long minutesPlayed;
	private int lastTeamId;
	private long firstSeen = Long.MAX_VALUE;
	private long lastSeen = 0;
	
	private ArrayList<Game> games = new ArrayList<Game>();

	public ChampionSummary(String championName) {
		this.championName = championName;
	}

	public ChampionSummary(String championName, Game game, int lastTeamId, GameResult gameResult) {
		this.championName = championName;
		this.minutesPlayed = game.getGameLength();
		this.lastTeamId = lastTeamId;
		updateTeamInfo(lastTeamId, gameResult);
		updateFirstLastSeen(game.getEndTime());
		games.add(game);
	}

	public String getChampionName() {
		return championName;
	}

	public void incrementBlueTeamWins() {
		blueTeamWins++;
	}

	public void incrementBlueTeamWinsBy(int wins) {
		this.blueTeamWins += wins;
	}
	
	public void incrementRedTeamWins() {
		redTeamWins++;
	}
	
	public void incrementRedTeamWinsBy(int wins) {
		this.redTeamWins += wins;
	}

	public void incrementBlueTeamLosses() {
		blueTeamLosses++;
	}

	public void incrementBlueTeamLossesBy(int losses) {
		this.blueTeamLosses += losses;
	}
	
	public void incrementRedTeamLosses() {
		redTeamLosses++;
	}

	public void incrementRedTeamLossesBy(int losses) {
		this.redTeamLosses += losses;
	}
	
	public void updateTeamInfo(Player player) {
		if (player.getTeam() == Game.BLUE_TEAM) {
			incrementBlueTeamGamesPlayed();
			if (player.getGameResult() == GameResult.WON) {
				incrementBlueTeamWins();
			} else if (player.getGameResult() == GameResult.LOST) {
				incrementBlueTeamLosses();
			}
		} else if (player.getTeam() == Game.RED_TEAM) {
			incrementRedTeamGamesPlayed();
			if (player.getGameResult() == GameResult.WON) {
				incrementRedTeamWins();
			} else if (player.getGameResult() == GameResult.LOST) {
				incrementRedTeamLosses();
			}
		}
	}

	public void updateTeamInfo(int team, GameResult gameResult) {
		if (team == Game.BLUE_TEAM) {
			incrementBlueTeamGamesPlayed();
			if (gameResult == GameResult.WON) {
				incrementBlueTeamWins();
			} else if (gameResult == GameResult.LOST) {
				incrementBlueTeamLosses();
			}
		} else if (team == Game.RED_TEAM) {
			incrementRedTeamGamesPlayed();
			if (gameResult == GameResult.WON) {
				incrementRedTeamWins();
			} else if (gameResult == GameResult.LOST) {
				incrementRedTeamLosses();
			}
		}
	}

	public int getGamesPlayed() {
		return redTeamGames + blueTeamGames;
	}

	public void incrementBlueTeamGamesPlayed() {
		blueTeamGames++;
	}
	
	public void incrementRedTeamGamesPlayed() {
		blueTeamGames++;
	}

	public void incrementBlueGamesPlayedBy(int gamesPlayed) {
		this.blueTeamGames += gamesPlayed;
	}
	
	public void incrementRedGamesPlayedBy(int gamesPlayed) {
		this.redTeamGames += gamesPlayed;
	}
	
	public int getBlueTeamGames() {
		return blueTeamGames;
	}
	
	public int getBlueTeamWins() {
		return blueTeamWins;
	}
	
	public int getBlueTeamLosses() {
		return blueTeamLosses;
	}
	
	public int getRedTeamGames() {
		return redTeamGames;
	}
	
	public int getRedTeamWins() {
		return redTeamWins;
	}
	
	public int getRedTeamLosses() {
		return redTeamLosses;
	}

	public long getMinutesPlayed() {
		return minutesPlayed;
	}

	public void incrementMinutesPlayedBy(long l) {
		minutesPlayed += l;
	}

	public int getLastTeamId() {
		return lastTeamId;
	}
	
	/**
	 * Sets lastSeen if the timeSeen is more recent or firstSeen if timeSeen is less recent.
	 * @param timeSeen
	 */
	public void updateFirstLastSeen(long timeSeen) {
		if (timeSeen > this.lastSeen) {
			this.lastSeen = timeSeen;
		}
		if (timeSeen < this.firstSeen) {
			this.firstSeen = timeSeen;
		}
	}

	public long getFirstSeen() {
		return firstSeen;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public int totalWins() {
		return blueTeamWins + redTeamWins;
	}

	public int totalLosses() {
		return blueTeamLosses + redTeamLosses;
	}

	public int blueTeamGamesWithKnownOutcome() {
		return blueTeamWins + blueTeamLosses;
	}

	public int redTeamGamesWithKnownOutcome() {
		return redTeamWins + redTeamLosses;
	}

	public boolean addGame(Game game) {
		return games.add(game);
	}
	
	public ArrayList<Game> getGames() {
		return games;
	}

}
