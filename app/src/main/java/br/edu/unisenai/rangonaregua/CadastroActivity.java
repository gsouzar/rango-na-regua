package br.edu.unisenai.rangonaregua;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class CadastroActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText edtEmail, edtSenha, edtConfirma;
    private TextView txtErro;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        auth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtConfirma = findViewById(R.id.edtConfirma);
        txtErro = findViewById(R.id.txtErro);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> cadastrar());
        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());
    }

    private void cadastrar() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        txtErro.setText("");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError(getString(R.string.erro_email)); return; }
        if (senha.length() < 6) { edtSenha.setError(getString(R.string.dica_senha)); return; }
        if (!senha.equals(edtConfirma.getText().toString())) { edtConfirma.setError(getString(R.string.erro_confirma)); return; }

        btnCadastrar.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, task -> {
            btnCadastrar.setEnabled(true);
            if (task.isSuccessful()) { // cadastro já deixa a pessoa logada
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            } else {
                txtErro.setText(Erros.auth(task.getException()));
            }
        });
    }
}
