package rafaelcds.crowdchat;

import java.io.Serializable;

public class Mensagem implements Serializable {
	private static final long serialVersionUID = 1L;

	public String nomeRemetente;
	public String mensagem;

	public Mensagem(String nomeRemetente, String mensagem) {
		super();
		this.nomeRemetente = nomeRemetente;
		this.mensagem = mensagem;
	}

	@Override
	public String toString() {
		return nomeRemetente + ": " + mensagem;
	}
}
