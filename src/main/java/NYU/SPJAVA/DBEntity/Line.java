package NYU.SPJAVA.DBEntity;

import java.sql.Timestamp;

public class Line {
	private Integer line_id; // int cannot be null, has to be Integer
	private int picture_id;
	private int pen_size;
	private int color_r;
	private int color_g;
	private int color_b;
	private Long time;
	private boolean is_eraser;
	private int pre_x;
	private int pre_y;
	private int x;
	private int y;
	private boolean is_deleted = false;

	public Line(Integer line_id, int picture_id, int pen_size, int color_r, int color_g, int color_b, Long time,
			boolean is_eraser, int pre_x, int pre_y, int x, int y, boolean is_deleted) {
		this.line_id = line_id;
		this.picture_id = picture_id;
		this.pen_size = pen_size;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
		this.time = time;
		this.is_eraser = is_eraser;
		this.pre_x = pre_x;
		this.pre_y = pre_y;
		this.x = x;
		this.y = y;
		this.is_deleted = is_deleted;
	}

	public Line(int picture_id, int pen_size, int color_r, int color_g, int color_b, Long time, boolean is_eraser,
			int pre_x, int pre_y, int x, int y) {
		this(null, picture_id, pen_size, color_r, color_g, color_b, time, is_eraser, pre_x, pre_y, x, y, false);
	}

	public int getLine_id() {
		return line_id;
	}

	public void setLine_id(int line_id) {
		this.line_id = line_id;
	}

	public int getPicture_id() {
		return picture_id;
	}

	public void setPicture_id(int picture_id) {
		this.picture_id = picture_id;
	}

	public int getPen_size() {
		return pen_size;
	}

	public void setPen_size(int pen_size) {
		this.pen_size = pen_size;
	}

	public int getColor_r() {
		return color_r;
	}

	public void setColor_r(int color_r) {
		this.color_r = color_r;
	}

	public int getColor_g() {
		return color_g;
	}

	public void setColor_g(int color_g) {
		this.color_g = color_g;
	}

	public int getColor_b() {
		return color_b;
	}

	public void setColor_b(int color_b) {
		this.color_b = color_b;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public boolean isIs_eraser() {
		return is_eraser;
	}

	public void setIs_eraser(boolean is_eraser) {
		this.is_eraser = is_eraser;
	}

	public int getPre_x() {
		return pre_x;
	}

	public void setPre_x(int pre_x) {
		this.pre_x = pre_x;
	}

	public int getPre_y() {
		return pre_y;
	}

	public void setPre_y(int pre_y) {
		this.pre_y = pre_y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public boolean isIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}
}
