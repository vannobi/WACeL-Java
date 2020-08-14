package pe.edu.unsa.daisi.lis.cel.util.exception;

public class ServiceException extends CustomException {

	public ServiceException(ErrorCode code) {
		super(code);
		// TODO Auto-generated constructor stub
	}

	public ServiceException(String message, ErrorCode code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public ServiceException(String message, Throwable cause, ErrorCode code) {
		super(message, cause, code);
		// TODO Auto-generated constructor stub
	}

	public ServiceException(Throwable cause, ErrorCode code) {
		super(cause, code);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
