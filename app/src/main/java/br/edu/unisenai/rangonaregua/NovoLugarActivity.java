package br.edu.unisenai.rangonaregua;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.edu.unisenai.rangonaregua.data.LugarRepository;
import br.edu.unisenai.rangonaregua.model.Lugar;

/** Serve para indicar um lugar novo e, se receber o extra "lugar", para editar um existente. */
public class NovoLugarActivity extends AppCompatActivity {

    private final LugarRepository repo = new LugarRepository();
    private EditText edtNome, edtCategoria, edtPreco, edtObservacao;
    private Button btnSalvar;
    private Lugar editando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_novo_lugar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });

        ((Toolbar) findViewById(R.id.toolbarNovo)).setNavigationOnClickListener(v -> finish());
        edtNome = findViewById(R.id.edtNome);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtPreco = findViewById(R.id.edtPreco);
        edtObservacao = findViewById(R.id.edtObservacao);
        btnSalvar = findViewById(R.id.btnSalvar);

        editando = (Lugar) getIntent().getSerializableExtra("lugar");
        if (editando != null && savedInstanceState == null) {
            ((Toolbar) findViewById(R.id.toolbarNovo)).setTitle(R.string.editar_titulo);
            edtNome.setText(editando.getNome());
            edtCategoria.setText(editando.getCategoria());
            edtPreco.setText(String.valueOf(editando.getPrecoMedio()));
            edtObservacao.setText(editando.getObservacao());
        }
        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        String nome = edtNome.getText().toString().trim();
        String cat = edtCategoria.getText().toString().trim();
        String precoTxt = edtPreco.getText().toString().trim().replace(',', '.');
        String obs = edtObservacao.getText().toString().trim();

        if (nome.isEmpty()) { edtNome.setError(getString(R.string.erro_nome)); return; }
        if (cat.isEmpty()) { edtCategoria.setError(getString(R.string.erro_categoria)); return; }
        double preco;
        try { preco = Double.parseDouble(precoTxt); }
        catch (NumberFormatException e) { edtPreco.setError(getString(R.string.erro_preco)); return; }
        if (preco < 0) { edtPreco.setError(getString(R.string.erro_preco)); return; }

        btnSalvar.setEnabled(false);
        if (editando == null) {
            Lugar novo = new Lugar(nome, cat, preco, obs, 0);
            repo.criar(novo)
                    .addOnSuccessListener(r -> finish()) // a lista da tela principal atualiza sozinha
                    .addOnFailureListener(e -> falhou());
        } else {
            editando.setNome(nome); editando.setCategoria(cat);
            editando.setPrecoMedio(preco); editando.setObservacao(obs);
            repo.atualizar(editando)
                    .addOnSuccessListener(r -> finish())
                    .addOnFailureListener(e -> falhou());
        }
    }

    private void falhou() {
        btnSalvar.setEnabled(true);
        Toast.makeText(this, R.string.erro_salvar, Toast.LENGTH_LONG).show();
    }
}
