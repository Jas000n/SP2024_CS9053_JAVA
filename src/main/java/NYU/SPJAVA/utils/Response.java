package NYU.SPJAVA.utils;

public class Response {
    ResponseCode res_code;
    String res_msg;
    Object data;


    public static void main(String[] args) {
        Response myResponse = new Response();
        myResponse.res_code = ResponseCode.SUCCESS;
        System.out.println(myResponse.res_code);
    }
}

enum ResponseCode {
    SUCCESS(1, "Success"),
    FAILED(0, "Failed");

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
