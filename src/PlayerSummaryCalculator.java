import java.util.Arrays;
import java.util.HashMap;

public class PlayerSummaryCalculator {

	private Game[] games;

	public PlayerSummaryCalculator(Game[] games) {
		this.games = games;
	}

	public void summarizePlayerForAllPlayers(Game game, Player player, HashMap<String, PlayerSummary> playerSummaries) {
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

	public void summarizePlayer(Game game, Player player, HashMap<String, PlayerSummary> playerSummaries) {
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

	public PlayerSummary[] createPlayerSummaries(GameFilter gameFilter) {

		HashMap<String, PlayerSummary> playerSummaryHashMap = new HashMap<String, PlayerSummary>();
		for (Game game : games) {
			if (gameFilter.accept(game)) {
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

		Arrays.sort(playerSummaries);
		return playerSummaries;
	}

}
