public class ChampionSummary {

	private String championName;
	private int wins;
	private int losses; //kept track of so undetermined outcomes can be calculated
	private int gamesPlayed = 1;
	private long minutesPlayed;
	private int lastTeamId;

	public ChampionSummary(String championName) {
		this.championName = championName;
	}

	public ChampionSummary(String championName, long minutesPlayed, int lastTeamId, GameResult lastGameResult) {
		this.championName = championName;
		this.minutesPlayed = minutesPlayed;
		this.lastTeamId = lastTeamId;
		if (lastGameResult == GameResult.WON) {
			wins++;
		} else if (lastGameResult == GameResult.LOST) {
			losses++;
		}
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

	@Override
	public boolean equals(Object obj) {
		return championName.equals(((ChampionSummary)obj).getChampionName());
	}
}