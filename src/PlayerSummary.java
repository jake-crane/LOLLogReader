import java.util.Comparator;
import java.util.HashMap;

public class PlayerSummary {

	private String name;
	private HashMap<String, ChampionSummary> championSummaries = new HashMap<String, ChampionSummary>();

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

}