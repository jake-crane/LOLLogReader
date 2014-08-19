public class ChampionSummary {

	private String championName;
	private int wins;
	private int losses; //kept track of so undetermined outcomes can be calculated
	private int gamesPlayed = 1; //assumed 1 game played
	private long minutesPlayed;
	private int lastTeamId;
	private long firstSeen = Long.MAX_VALUE;
	private long lastSeen = 0;

	public ChampionSummary(String championName) {
		this.championName = championName;
	}

	public ChampionSummary(String championName, Game game, int lastTeamId, GameResult lastGameResult) {
		this.championName = championName;
		this.minutesPlayed = game.getGameLength();
		this.lastTeamId = lastTeamId;
		if (lastGameResult == GameResult.WON) {
			wins++;
		} else if (lastGameResult == GameResult.LOST) {
			losses++;
		}
		updateFirstLastSeen(game.getEndTime());
	}

	public String getChampionName() {
		return championName;
	}

	public int getWins() {
		return wins;
	}

	public void incrementWins() {
		wins++;
	}

	public void incrementWinsBy(int wins) {
		this.wins += wins;
	}

	public int getLosses() {
		return losses;
	}

	public void incrementLosses() {
		losses++;
	}

	public void incrementLossesBy(int losses) {
		this.losses += losses;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void incrementGamesPlayed() {
		gamesPlayed++;
	}

	public void incrementGamesPlayedBy(int gamesPlayed) {
		this.gamesPlayed += gamesPlayed;
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

}
