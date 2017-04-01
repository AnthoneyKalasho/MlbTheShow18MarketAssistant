public class PlayerCard {
	private String playerName;
	private int buyNow;
	private int sellNow;
	private int playerID;
	private int playerCardRank;
	private int pageNumber;
	private String playerTeam;
	private String playerSeries;

	PlayerCard(int cardRank, String name, String series, int buy, int sell,
			int ID, String team, int pageNum) {
		playerName = name;
		buyNow = buy;
		sellNow = sell;
		playerID = ID;
		playerCardRank = cardRank;
		playerTeam = team;
		playerSeries = series;
		pageNumber = pageNum;
	}

	public String getTeam() {
		return playerTeam;
	}

	public String getName() {
		return playerName;
	}

	public int getCardRank() {
		return playerCardRank;
	}

	public int getBuyNow() {
		return buyNow;
	}

	public int getSellNow() {
		return sellNow;
	}

	public int getID() {
		return playerID;
	}
	public int getPg() {
		return pageNumber;
	}
	public String getSeries() {
		return playerSeries;
	}

}