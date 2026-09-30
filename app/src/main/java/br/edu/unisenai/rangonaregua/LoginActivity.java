package br.edu.unisenai.rangonaregua;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText edtEmail, edtSenha;
    private TextView txtErro;
    private Button btnEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        // Sessão persistente: quem já entrou vai direto para o ranking.
        if (auth.getCurrentUser() != null) { abrirRanking(); return; }

        setContentView(R.layout.activity_login);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        txtErro = findViewById(R.id.txtErro);
        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(v -> entrar());
        findViewById(R.id.btnCriarConta).setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
    }

    private void entrar() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        txtErro.setText(R.string.dica_senha);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError(getString(R.string.erro_email)); return; }
        if (senha.length() < 6) { edtSenha.setError(getString(R.string.dica_senha)); return; }

        btnEntrar.setEnabled(false);
        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, task -> {
            btnEntrar.setEnabled(true);
            if (task.isSuccessful()) abrirRanking();
            else txtErro.setText(Erros.auth(task.getException()));
        });
    }

    private void abrirRanking() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
