package pe.edu.unsa.daisi.lis.cel.util.exception;

public class ViewException extends CustomException {

	public ViewException(ErrorCode code) {
		super(code);
		// TODO Auto-generated constructor stub
	}

	public ViewException(String message, ErrorCode code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public ViewException(String message, Throwable cause, ErrorCode code) {
		super(message, cause, code);
		// TODO Auto-generated constructor stub
	}

	public ViewException(Throwable cause, ErrorCode code) {
		super(cause, code);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
