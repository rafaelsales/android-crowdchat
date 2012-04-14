package rafaelcds.crowdchat;

import rafaelcds.crowdchat.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginAct extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	public void joinChat(final View view) {
		EditText etNome = (EditText) findViewById(R.id.login_etNome);
		final String nome = etNome.getText().toString().trim();
		if (nome.length() == 0) {
			Toast.makeText(this, R.string.login_nome_obrigatorio, Toast.LENGTH_SHORT).show();
			return;
		}

		final ProgressDialog progressDialog = ProgressDialog.show(view.getContext(), "",
				getString(R.string.msg_aguarde));

		new Thread() {
			@Override
			public void run() {
				ChatService chatService = new ChatService(nome);
				try {
					chatService.entrar();

					Intent intent = new Intent(view.getContext(), ChatAct.class);
					intent.putExtra(ChatAct.EXTRA_CHAT_SERVICE, chatService);
					startActivity(intent);
				} catch (ChatException e) {
					Toast.makeText(LoginAct.this, e.getMessage(), Toast.LENGTH_LONG).show();
					return;
				} finally {
					progressDialog.dismiss();
				}
			}
		}.start();
	}
}