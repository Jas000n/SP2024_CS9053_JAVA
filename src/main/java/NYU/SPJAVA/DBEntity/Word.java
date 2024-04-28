package NYU.SPJAVA.DBEntity;

public class Word {
	public final Integer wordID;
	public final String word;

	public Word(Integer wordID, String word) {
		this.wordID = wordID;
		this.word = word;
	}

	public Word(String word) {
		this(null, word);
	}
	
	public int getWordID() {
		return this.wordID;
	}
	
	public String getWord() {
		return this.word;
	}

	@Override
	public String toString() {
		return String.format("Word: %s, wordID: %d", this.word, this.wordID);
	}
}
