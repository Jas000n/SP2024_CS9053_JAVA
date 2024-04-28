package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.Line;
import NYU.SPJAVA.DBEntity.Picture;
import NYU.SPJAVA.exceptions.PictureDoesNotExistException;
import NYU.SPJAVA.utils.Response;
import NYU.SPJAVA.utils.Response.ResponseCode;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LineDBConnector extends DBConnector {
	private final static String insertLineQuery = "INSERT INTO picasso.line"
			+ "(picture_id, pen_size, color_r, color_g, color_b, time, is_eraser, pre_x, pre_y, x, y, is_deleted) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ;";

	private final static String getLinesQuery = "SELECT line_id, picture_id, pen_size, color_r, color_g, color_b,"
			+ " time, is_eraser, pre_x, pre_y, x, y, is_deleted" + " FROM line WHERE picture_id = ? ;";

	public LineDBConnector() throws Exception {
		super();
		connect(); // connects to the db
	}

	private void savePictureLines(ArrayList<Line> lines) throws SQLException {
		conn.setAutoCommit(false); // batch mode
		PreparedStatement statement = conn.prepareStatement(LineDBConnector.insertLineQuery);
		for (Line l : lines) {
			statement.setInt(1, l.getPicture_id());
			statement.setInt(2, l.getPen_size());
			statement.setInt(3, l.getColor_r());
			statement.setInt(4, l.getColor_g());
			statement.setInt(5, l.getColor_b());
			statement.setLong(6, l.getTime());
			statement.setBoolean(7, l.isIs_eraser());
			statement.setInt(8, l.getPre_x());
			statement.setInt(9, l.getPre_y());
			statement.setInt(10, l.getX());
			statement.setInt(11, l.getY());
			statement.setBoolean(12, l.isIs_deleted());

			statement.addBatch(); // stage for commit

		}

		statement.executeBatch();
		conn.commit();

	}

	private ArrayList<Line> getPictureLines(Picture picture) throws Exception {
		// return all lines for a picture
		PreparedStatement statement = conn.prepareStatement(LineDBConnector.getLinesQuery);
		statement.setInt(1, picture.getPictureID());
		ResultSet res = statement.executeQuery();

		if (!res.next()) {
			// result set is empty
			String msg = String.format("Picture does not exist!");
			throw new PictureDoesNotExistException(msg);

		} else {
			ArrayList<Line> lines = new ArrayList<>();
			do {
				lines.add(new Line(res.getInt("line_id"), picture.getPictureID(), res.getInt("pen_side"),
						res.getInt("color_r"), res.getInt("color_g"), res.getInt("color_b"), res.getLong("time"),
						res.getBoolean("is_eraser"), res.getInt("pre_x"), res.getInt("pre_y"), res.getInt("x"),
						res.getInt("y"), res.getBoolean("is_deleted")));
			} while (res.next());
			return lines;
		}
	}

	public Response saveLines(ArrayList<Line> lines) {
		try {
			savePictureLines(lines);
			String msg = String.format("Saved %d lines to db", lines.size());
			return new Response(ResponseCode.SUCCESS, msg, null, null);
		} catch (Exception ex) {
			String msg = "failed to save lines";
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}
	}

	// retrieve all lines from a picture
	// passing in a picture id
	// return all lines encapsulated in an array, Lines[], sorted in timestamp
	public Response getLines(Picture picture) {
		try {
			ArrayList<Line> lines = getPictureLines(picture);
			String msg = String.format("got %d lines from db for picture id %", lines.size(), picture.getPictureID());
			return new Response(ResponseCode.SUCCESS, msg, null, lines);
		} catch (Exception ex) {
			String msg = String.format("Failed to fetch lines for picture id %d", picture.getPictureID());
			return new Response(ResponseCode.FAILED, msg, ex, null);
		}
	}

}
