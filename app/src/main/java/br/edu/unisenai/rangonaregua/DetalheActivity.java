package br.edu.unisenai.rangonaregua;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.edu.unisenai.rangonaregua.data.LugarRepository;
import br.edu.unisenai.rangonaregua.model.Lugar;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.NumberFormat;
import java.util.Locale;

public class DetalheActivity extends AppCompatActivity {

    private final LugarRepository repo = new LugarRepository();
    private Lugar lugar;
    private ListenerRegistration registro;
    private TextView txtNome, txtCategoria, txtPreco, txtVotos, txtObs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });

        lugar = (Lugar) getIntent().getSerializableExtra("lugar");
        if (lugar == null) { finish(); return; }

        ((Toolbar) findViewById(R.id.toolbarDetalhe)).setNavigationOnClickListener(v -> finish());
        txtNome = findViewById(R.id.txtDetalheNome);
        txtCategoria = findViewById(R.id.txtDetalheCategoria);
        txtPreco = findViewById(R.id.txtDetalhePreco);
        txtVotos = findViewById(R.id.txtDetalheVotos);
        txtObs = findViewById(R.id.txtDetalheObservacao);

        findViewById(R.id.btnEditar).setOnClickListener(v ->
                startActivity(new Intent(this, NovoLugarActivity.class).putExtra("lugar", lugar)));
        findViewById(R.id.btnExcluir).setOnClickListener(v -> confirmarExclusao());
        preencher();
    }

    /** Acompanha o documento: se editar ou votar, a tela de detalhe também muda. */
    @Override protected void onStart() {
        super.onStart();
        if (lugar == null) return;
        registro = FirebaseFirestore.getInstance().collection("lugares").document(lugar.getId())
                .addSnapshotListener((doc, e) -> {
                    if (e != null || doc == null || !doc.exists()) return;
                    Lugar atual = doc.toObject(Lugar.class);
                    if (atual != null) { atual.setId(doc.getId()); lugar = atual; preencher(); }
                });
    }

    @Override protected void onStop() {
        super.onStop();
        if (registro != null) { registro.remove(); registro = null; }
    }

    private void preencher() {
        txtNome.setText(lugar.getNome());
        txtCategoria.setText(lugar.getCategoria());
        txtPreco.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(lugar.getPrecoMedio()));
        txtVotos.setText(lugar.getVotos() == 1 ? "1 voto" : lugar.getVotos() + " votos");
        String obs = lugar.getObservacao();
        txtObs.setText(obs == null || obs.isEmpty() ? getString(R.string.sem_observacao) : obs);
    }

    private void confirmarExclusao() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.excluir_titulo)
                .setMessage(getString(R.string.excluir_msg, lugar.getNome()))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.excluir, (d, w) -> {
                    if (registro != null) { registro.remove(); registro = null; }
                    repo.excluir(lugar.getId())
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, R.string.lugar_removido, Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, R.string.erro_excluir, Toast.LENGTH_LONG).show());
                }).show();
    }
}
