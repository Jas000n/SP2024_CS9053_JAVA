package NYU.SPJAVA.DBEntity;

import org.joda.time.LocalDateTime;

public class Game {
	private final Integer gameID;
	private final Word word;
	private final char mode;
	private final char status;
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
		this.started = new LocalDateTime();
	}
	
	public Integer getGameID() {
		return this.gameID;
	}
	
	public Word getWord() {
		return this.word;
	}
	
	public char getMode() {
		return this.mode;
	}
	
	public char getStatus() {
		return this.status;
	}
	
	public int getTimeLimit() {
		return this.timeLimit;
	}
	
	public Player getCreator() {
		return this.creator;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
	
	public LocalDateTime getCreated() {
		return this.created;
	}
	
	public LocalDateTime getStarted() {
		return this.started;
	}
	
	public LocalDateTime getEnded() {
		return this.ended;
	}
	
	// setters
	public void setStarted(LocalDateTime started) {
		this.started = started;
	}
	
	public void setEnded(LocalDateTime ended) {
		this.ended = ended;
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
