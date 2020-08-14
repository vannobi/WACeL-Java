package pe.edu.unsa.daisi.lis.cel.util.exception;

public enum ErrorCode implements ErrorCodeCostant {
	ACTIVE_USER_FOUND(CODE_100, "User not found"), //get from messages.properties
	INVALID_REQUEST(CODE_101, "The request is invalid"),
	INACTIVE_USER(CODE_102, "Inactive user"),
	UNEXPECTED_ERROR(CODE_103, "UNEXPEDTED ERROR");
	
	private final int id;
	private final String msg;
	ErrorCode(int id, String msg) {
		this.id = id;
		this.msg = msg;
	}
	public int getId() {
		return this.id;
	}
	public String getMsg() {
		return this.msg;
	}
}
