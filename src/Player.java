public class Player {
		private String name;
		private GameResult gameResult = GameResult.UNKNOWN;
		private String championName;
		private int clientId;
		private int team = -1;

		public Player(String playerName, String championName, int clientId, int team) {
			this.name = playerName;
			this.championName = championName;
			this.clientId = clientId;
			this.team = team;
		}

		public GameResult getGameResult() {
			return gameResult;
		}

		public void setGameResult(GameResult gameResult) {
			this.gameResult = gameResult;
		}

		public String getName() {
			return name;
		}
		
		public String getChampionName() {
			return championName;
		}

		public int getClientId() {
			return clientId;
		}

		public int getTeam() {
			return team;
		}
		
		@Override
		public String toString() {
			return name + "|" + clientId + "|" + championName + "|" + team + "|" + gameResult.name();
		}

	}