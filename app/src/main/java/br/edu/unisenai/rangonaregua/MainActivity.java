package br.edu.unisenai.rangonaregua;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.unisenai.rangonaregua.adapter.LugarAdapter;
import br.edu.unisenai.rangonaregua.data.LugarRepository;
import br.edu.unisenai.rangonaregua.model.Lugar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String CHAVE_GRADE = "modo_grade";

    private final LugarRepository repo = new LugarRepository();
    private final List<Lugar> todos = new ArrayList<>();
    private LugarAdapter adapter;
    private RecyclerView rv;
    private View grupoVazio;
    private TextView txtSubtitulo;
    private Toolbar toolbar;
    private ListenerRegistration registro;
    private boolean modoGrade = false;
    private String categoriaFiltro = null; // null = todas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });
        if (savedInstanceState != null) modoGrade = savedInstanceState.getBoolean(CHAVE_GRADE);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.acaoLayout) { alternarLayout(); return true; }
            if (id == R.id.acaoFiltrar) { escolherCategoria(); return true; }
            if (id == R.id.acaoSair) { sair(); return true; }
            return false;
        });

        txtSubtitulo = findViewById(R.id.txtSubtitulo);
        grupoVazio = findViewById(R.id.grupoVazio);
        rv = findViewById(R.id.rvLugares);

        adapter = new LugarAdapter(new LugarAdapter.Acoes() {
            @Override public void aoClicar(Lugar l) {
                startActivity(new Intent(MainActivity.this, DetalheActivity.class).putExtra("lugar", l));
            }
            @Override public void aoVotar(Lugar l) {
                repo.votar(l.getId()).addOnFailureListener(e -> erro("Não foi possível votar."));
            }
        });
        rv.setAdapter(adapter);
        aplicarLayout();

        FloatingActionButton fab = findViewById(R.id.fabNovo);
        fab.setOnClickListener(v -> startActivity(new Intent(this, NovoLugarActivity.class)));
    }

    @Override protected void onStart() {
        super.onStart();
        String uid = LugarRepository.uidAtual();
        if (uid == null) { irParaLogin(); return; }
        registro = repo.observar(uid, new LugarRepository.Ouvinte() {
            @Override public void aoMudar(List<Lugar> lugares) {
                todos.clear();
                todos.addAll(lugares);
                atualizarTela();
            }
            @Override public void aoFalhar(Exception e) { erro("Falha ao carregar o ranking."); }
        });
    }

    @Override protected void onStop() {
        super.onStop();
        if (registro != null) { registro.remove(); registro = null; } // não vaza listener
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putBoolean(CHAVE_GRADE, modoGrade);
    }

    private void atualizarTela() {
        List<Lugar> visiveis = new ArrayList<>();
        for (Lugar l : todos) {
            if (categoriaFiltro == null || categoriaFiltro.equalsIgnoreCase(l.getCategoria())) visiveis.add(l);
        }
        adapter.atualizar(visiveis);
        boolean vazia = visiveis.isEmpty();
        rv.setVisibility(vazia ? View.GONE : View.VISIBLE);
        grupoVazio.setVisibility(vazia ? View.VISIBLE : View.GONE);
        String onde = categoriaFiltro == null ? "todas as categorias" : categoriaFiltro;
        txtSubtitulo.setText(visiveis.size() + (visiveis.size() == 1 ? " lugar em " : " lugares em ") + onde);
    }

    private void alternarLayout() { modoGrade = !modoGrade; aplicarLayout(); }

    private void aplicarLayout() {
        if (modoGrade) {
            GridLayoutManager glm = new GridLayoutManager(this, 2);
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override public int getSpanSize(int pos) { return adapter.isLider(pos) ? 2 : 1; }
            });
            rv.setLayoutManager(glm);
        } else {
            rv.setLayoutManager(new LinearLayoutManager(this));
        }
        adapter.setModoGrade(modoGrade);
        toolbar.getMenu().findItem(R.id.acaoLayout)
                .setTitle(modoGrade ? R.string.menu_ver_lista : R.string.menu_ver_grade);
    }

    private void escolherCategoria() {
        Set<String> cats = new LinkedHashSet<>();
        for (Lugar l : todos) if (l.getCategoria() != null) cats.add(l.getCategoria());
        final List<String> opcoes = new ArrayList<>();
        opcoes.add(getString(R.string.todas_categorias));
        opcoes.addAll(cats);
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_filtrar)
                .setItems(opcoes.toArray(new String[0]), (d, which) -> {
                    categoriaFiltro = which == 0 ? null : opcoes.get(which);
                    atualizarTela();
                }).show();
    }

    private void sair() {
        FirebaseAuth.getInstance().signOut(); // senão o próximo usuário entra na conta anterior
        irParaLogin();
    }

    private void irParaLogin() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private void erro(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
}
