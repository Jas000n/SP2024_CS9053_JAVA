package NYU.SPJAVA.DBEntity;

import org.joda.time.LocalDateTime;

public class Game {
	private final Integer gameID;
	private final Word word;
	private final char mode;
	private char status;
	private final int timeLimit;
	private final Player creator;
	private final int capacity;
	private final LocalDateTime created;
	private LocalDateTime started;
	private LocalDateTime ended;

	public Game(Integer gameID, Word word, char mode, char status, int timeLimit, Player creator, int capacity,
			LocalDateTime created, LocalDateTime started, LocalDateTime ended) {
		this.gameID = gameID;
		this.word = word;
		this.mode = mode;
		this.status = status;
		this.timeLimit = timeLimit;
		this.creator = creator;
		this.capacity = capacity;
		this.created = created;
		this.started = started;
		this.ended = ended;
	}

	public Game(Word word, Player creator) {
		// minimal game creations
		this.word = word;
		this.creator = creator;
		
		// defaults
		this.gameID = null;
		this.mode = 'S';
		this.status = 'N';
		this.timeLimit = 180;
		this.capacity = 1;
		this.created = new LocalDateTime();
	}
	
	public Integer getGameID() {
		return this.gameID;
	}
	
	public char getMode() {
		return this.mode;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Game) {
			Game that = (Game)other;
			return this.gameID == that.gameID;
		} else {
			return false;
		}
	}

}
