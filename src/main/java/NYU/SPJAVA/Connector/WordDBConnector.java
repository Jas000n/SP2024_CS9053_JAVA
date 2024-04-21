package NYU.SPJAVA.Connector;

import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import NYU.SPJAVA.DBEntity.Word;

public class WordDBConnector extends DBConnector {
	public final static String getWordsQuery = "SELECT * FROM picasso.word ORDER BY RAND() limit ? ;";

	public WordDBConnector() throws Exception {
		super();
		connect();
	}

	private ArrayList<Word> getRandomWords(int num) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(WordDBConnector.getWordsQuery);
		statement.setInt(1, num);

		ResultSet res = statement.executeQuery();

		ArrayList<Word> words;
		if (!res.next()) {
			throw new SQLException("word table is empty!");
		} else {
			// found words
			words = new ArrayList<Word>();
			do {
				Word w = new Word(res.getInt("word_id"), res.getString("word"));
				words.add(w);
			} while (res.next());
		}
		return words;

	}

	/**
	 * returns up to n random words from the db word table
	 * @param n
	 * @return Response where data object is ArrayList<Word>
	 * Response.Exception: SQLException, Exception
	 */
	public Response getWord(int n) {
		try {
			ArrayList<Word> words = getRandomWords(n);
			String msg = String.format("fetched %d random words", words.size());
			return new Response(ResponseCode.SUCCESS, msg, null, words);
		} catch (Exception ex) {
			return new Response(ResponseCode.FAILED, ex.getMessage(), ex, null);
		}
	}

	public static void main(String[] args) throws Exception {
		
		// test retrieveWord
		WordDBConnector wc = new WordDBConnector();
		Response resp = wc.getWord(2);
		System.out.println(resp);
		if (resp.ex != null) {
			resp.ex.printStackTrace();
		} else {
			ArrayList<Word> words = (ArrayList<Word>)resp.data;
			System.out.println(words.toString());
		}
	}

}
