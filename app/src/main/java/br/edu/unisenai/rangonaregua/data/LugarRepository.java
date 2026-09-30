package br.edu.unisenai.rangonaregua.data;

import androidx.annotation.NonNull;

import br.edu.unisenai.rangonaregua.model.Lugar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/** Único lugar que conhece o Firestore. As telas só chamam estes métodos. */
public final class LugarRepository {

    public interface Ouvinte {
        void aoMudar(List<Lugar> lugares);
        void aoFalhar(Exception e);
    }

    private static final String COLECAO = "lugares";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static String uidAtual() {
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        return u == null ? null : u.getUid();
    }

    /** Escuta em tempo real só os lugares de quem está logado. */
    public ListenerRegistration observar(@NonNull String uid, Ouvinte ouvinte) {
        return db.collection(COLECAO).whereEqualTo("uid", uid)
                .addSnapshotListener((query, e) -> {
                    if (e != null) { ouvinte.aoFalhar(e); return; }
                    List<Lugar> lista = new ArrayList<>();
                    for (DocumentSnapshot d : query.getDocuments()) {
                        Lugar l = d.toObject(Lugar.class);
                        if (l == null) continue;
                        l.setId(d.getId());
                        lista.add(l);
                    }
                    Catalogo.ordenarPorVotos(lista); // ordena no app: evita índice composto
                    ouvinte.aoMudar(lista);
                });
    }

    public Task<?> criar(Lugar l) {
        l.setUid(uidAtual());
        return db.collection(COLECAO).add(l);
    }

    public Task<Void> atualizar(Lugar l) {
        return db.collection(COLECAO).document(l.getId()).update(
                "nome", l.getNome(),
                "categoria", l.getCategoria(),
                "precoMedio", l.getPrecoMedio(),
                "observacao", l.getObservacao());
    }

    public Task<Void> votar(String id) {
        return db.collection(COLECAO).document(id).update("votos", FieldValue.increment(1));
    }

    public Task<Void> excluir(String id) {
        return db.collection(COLECAO).document(id).delete();
    }
}
