package pe.edu.unsa.daisi.lis.cel.util.exception;

public class RepositoryException extends CustomException {

	public RepositoryException(ErrorCode code) {
		super(code);
		// TODO Auto-generated constructor stub
	}

	public RepositoryException(String message, ErrorCode code) {
		super(message, code);
		// TODO Auto-generated constructor stub
	}

	public RepositoryException(String message, Throwable cause, ErrorCode code) {
		super(message, cause, code);
		// TODO Auto-generated constructor stub
	}

	public RepositoryException(Throwable cause, ErrorCode code) {
		super(cause, code);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
