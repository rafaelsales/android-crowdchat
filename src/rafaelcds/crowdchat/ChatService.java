package rafaelcds.crowdchat;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.net.Uri;
import android.net.Uri.Builder;

public class ChatService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String MSG_ERRO_CONEXAO = "Ocorreu um erro inesperado na conexão.";

	private final String nome;
	private String idSessao;
	private String ultimoTimestamp;

	public ChatService(String nome) {
		this.nome = nome;
	}

	public void entrar() throws ChatException {
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("nome", this.nome);

		try {
			JSONObject jsonObject = requestAction("entrar", parametros);

			this.idSessao = jsonObject.getString("id_sessao");
			this.ultimoTimestamp = jsonObject.getString("timestamp");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ChatException(MSG_ERRO_CONEXAO);
		}
	}

	public void enviarMensagem(String mensagem) throws ChatException {
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("id_sessao", this.idSessao);
		parametros.put("mensagem", mensagem);
		try {
			requestAction("enviarMensagem", parametros);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ChatException(MSG_ERRO_CONEXAO);
		}
	}

	public List<Mensagem> obterMensagens() throws ChatException {
		List<Mensagem> mensagens = new ArrayList<Mensagem>();
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("id_sessao", this.idSessao);
		parametros.put("timestamp", this.ultimoTimestamp);
		try {
			JSONObject jsonObject = requestAction("obterMensagens", parametros);
			this.ultimoTimestamp = jsonObject.getString("timestamp");

			JSONArray mensagensJSON = jsonObject.getJSONArray("mensagens");
			for (int i = 0; i < mensagensJSON.length(); i++) {
				JSONObject msgJSON = mensagensJSON.getJSONObject(i);
				mensagens.add(new Mensagem(msgJSON.getString("nome_remetente"), msgJSON.getString("mensagem")));
			}
			return mensagens;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ChatException(MSG_ERRO_CONEXAO);
		}
	}

	private JSONObject requestAction(String action, Map<String, String> parametros) throws ParseException, IOException,
			JSONException {
		final String URL = "http://rafael.pyboys.com/chat/api/" + action + ".json";
		Builder uriBuilder = Uri.parse(URL).buildUpon();
		for (Entry<String, String> parametro : parametros.entrySet()) {
			uriBuilder.appendQueryParameter(parametro.getKey(), parametro.getValue());
		}

		HttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(uriBuilder.toString());
		getRequest.setHeader(HTTP.CONTENT_TYPE, "application/json");

		HttpResponse response = client.execute(getRequest);
		String resultStr = EntityUtils.toString(response.getEntity());

		JSONTokener jsonTokener = new JSONTokener(resultStr);
		if (jsonTokener.more()) {
			return (JSONObject) jsonTokener.nextValue();
		}
		return null;
	}

}
