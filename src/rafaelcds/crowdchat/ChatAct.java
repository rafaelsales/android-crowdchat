package rafaelcds.crowdchat;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rafaelcds.crowdchat.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatAct extends Activity {

	public static final String EXTRA_CHAT_SERVICE = "chatService";
	private static final long TIMER_ATUALIZACAO_PERIOD = 2000;

	private static final int MENU_ITEM_ENVIAR = 0;
	private static final int MENU_ITEM_SAIR = 1;

	private ChatService chatService;
	private Timer timerAtualizacao;
	private EditText etMensagem;
	private LinearLayout ltMensagens;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		chatService = (ChatService) getIntent().getSerializableExtra(EXTRA_CHAT_SERVICE);

		timerAtualizacao = new Timer();
		timerAtualizacao.schedule(new TimerTask() {
			@Override
			public void run() {
				atualizarMensagens();
			}

		}, 0, TIMER_ATUALIZACAO_PERIOD);

		etMensagem = (EditText) findViewById(R.id.chat_etMensagem);
		ltMensagens = (LinearLayout) findViewById(R.id.chat_ltMensagens);
	}

	private void atualizarMensagens() {
		try {
			final List<Mensagem> novasMensagens = chatService.obterMensagens();
			ltMensagens.post(new Runnable() {

				public void run() {
					for (Mensagem msg : novasMensagens) {
						TextView tvMsg = new TextView(ChatAct.this);
						tvMsg.setTextSize(15);
						tvMsg.setLayoutParams(new LinearLayout.LayoutParams(
								android.view.ViewGroup.LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
						tvMsg.setText(msg.nomeRemetente + ": " + msg.mensagem);
						ltMensagens.addView(tvMsg);
					}
				}
			});
		} catch (ChatException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
		}
	}

	public void enviarMensagem(View view) {
		Context context = this;
		if (view != null) {
			context = view.getContext();
		}

		final String mensagem = etMensagem.getEditableText().toString();
		etMensagem.setText("");
		if (mensagem.trim().length() == 0) {
			return;
		}

		final ProgressDialog progressDialog = ProgressDialog.show(context, "", getString(R.string.msg_aguarde));
		new Thread() {
			@Override
			public void run() {
				try {
					chatService.enviarMensagem(mensagem);
				} catch (ChatException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
				} finally {
					progressDialog.dismiss();
				}
			};
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_ITEM_ENVIAR, 0, R.string.login_submit_text);
		menu.add(0, MENU_ITEM_SAIR, 0, R.string.sair);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_ENVIAR:
			enviarMensagem(null);
			return true;
		case MENU_ITEM_SAIR:
			moveTaskToBack(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
