package pe.edu.unsa.daisi.lis.cel.util.exception;

public class DomainException extends CustomException {

	public DomainException(ErrorCode code) {
		super(code);
		// TODO Auto-generated constructor stub
	}

	public DomainException(String message, ErrorCode code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public DomainException(String message, Throwable cause, ErrorCode code) {
		super(message, cause, code);
		// TODO Auto-generated constructor stub
	}

	public DomainException(Throwable cause, ErrorCode code) {
		super(cause, code);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
