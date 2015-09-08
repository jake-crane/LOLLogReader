import java.util.HashMap;

public class PlayerSummary implements Comparable<PlayerSummary> {

	private String name;
	private HashMap<String, ChampionSummary> championSummaries = new HashMap<String, ChampionSummary>();

	public PlayerSummary(String playerName, ChampionSummary firstFoundChampionSummary) {
		this.name = playerName;
		championSummaries.put(firstFoundChampionSummary.getChampionName(), firstFoundChampionSummary);
	}

	public String getName() {
		return name;
	}

	public HashMap<String, ChampionSummary> getChampionSummaries() {
		return championSummaries;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(PlayerSummary playerSummary) {
		if (championSummaries.size() < playerSummary.getChampionSummaries().size()) {
			return 1;
		} else if (championSummaries.size() > playerSummary.getChampionSummaries().size()) {
			return -1;
		}
		return 0;
	}

}
