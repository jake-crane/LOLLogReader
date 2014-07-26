import java.util.ArrayList;
import java.util.Comparator;

public class PlayerSummary {
	
	private String name;
	private ArrayList<ChampionSummary> championSummaries = new ArrayList<ChampionSummary>();

	public static Comparator<PlayerSummary> gamesPlayedComparator = new Comparator<PlayerSummary>() {

		public int compare(PlayerSummary p1, PlayerSummary p2) {

			if (p1.championSummaries.size() < p2.getChampionSummaries().size()) {
				return 1;
			} else if (p1.championSummaries.size() > p2.getChampionSummaries().size()) {
				return -1;
			}
			return 0;
		}

	};

	public PlayerSummary(String name) {
		this.name = name;
	}

	public PlayerSummary(String name, ChampionSummary firstFoundChampionSummary) {
		this.name = name;
		championSummaries.add(firstFoundChampionSummary);
	}

	public String getName() {
		return name;
	}

	public ArrayList<ChampionSummary> getChampionSummaries() {
		return championSummaries;
	}

	@Override
	public String toString() {
		return name;
	}

}