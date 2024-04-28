package NYU.SPJAVA.utils;

public class Response {
	public ResponseCode code;
	public String msg;
	public Exception ex;
	public Object data;
	
	public Response(ResponseCode code, String msg, Exception ex, Object data) {
		this.code = code;
		this.msg = msg;
		this.ex = ex;
		this.data = data;
	}

	public enum ResponseCode {
		SUCCESS(1, "Success"), FAILED(0, "Failed");

		private final int code;
		private final String description;

		ResponseCode(int code, String description) {
			this.code = code;
			this.description = description;
		}

		public int getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}
	}
	
	@Override
	public String toString() {
		return String.format("Response: %s, Msg: %s", this.code, this.msg);
	}
	

	public static void main(String[] args) {
		Response myResponse = new Response(ResponseCode.SUCCESS, "succeeded", null, null);
		System.out.println(myResponse.code);
		System.out.println(myResponse);
	}
}
