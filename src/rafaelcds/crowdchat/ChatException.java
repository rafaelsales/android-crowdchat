package rafaelcds.crowdchat;

public class ChatException extends Exception {

	private static final long serialVersionUID = 1L;

	public ChatException() {
	}

	public ChatException(String detailMessage) {
		super(detailMessage);
	}

	public ChatException(Throwable throwable) {
		super(throwable);
	}

	public ChatException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
