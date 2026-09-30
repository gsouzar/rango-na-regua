package br.edu.unisenai.rangonaregua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.unisenai.rangonaregua.R;
import br.edu.unisenai.rangonaregua.model.Lugar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LugarAdapter extends RecyclerView.Adapter<LugarAdapter.VH> {

    public interface Acoes {
        void aoClicar(Lugar lugar);
        void aoVotar(Lugar lugar);
    }

    private static final int TIPO_LIDER = 0;
    private static final int TIPO_NORMAL = 1;
    private static final Object POSICAO = new Object();

    private final List<Lugar> lugares = new ArrayList<>();
    private final Acoes acoes;
    private boolean modoGrade = false;
    private final NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public LugarAdapter(Acoes acoes) { this.acoes = acoes; }

    /** O adapter nasce vazio; recebe os dados quando o Firestore responde. */
    public void atualizar(List<Lugar> novos) {
        final List<Lugar> antigos = new ArrayList<>(lugares);
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return antigos.size(); }
            @Override public int getNewListSize() { return novos.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                return Objects.equals(antigos.get(o).getId(), novos.get(n).getId());
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                Lugar a = antigos.get(o), b = novos.get(n);
                return a.getVotos() == b.getVotos() && a.getPrecoMedio() == b.getPrecoMedio()
                        && Objects.equals(a.getNome(), b.getNome())
                        && Objects.equals(a.getCategoria(), b.getCategoria())
                        && Objects.equals(a.getObservacao(), b.getObservacao());
            }
        });
        lugares.clear();
        lugares.addAll(novos);
        diff.dispatchUpdatesTo(this);              // anima entrada, saída, troca de posição
        notifyItemRangeChanged(0, lugares.size(), POSICAO); // atualiza o número do ranking
    }

    public void setModoGrade(boolean grade) { this.modoGrade = grade; notifyDataSetChanged(); }
    public boolean isLider(int position) { return position == 0; }

    @Override public int getItemViewType(int position) {
        return position == 0 ? TIPO_LIDER : TIPO_NORMAL;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == TIPO_LIDER ? R.layout.item_lider : R.layout.item_lugar;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new VH(v, viewType == TIPO_LIDER);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Lugar l = lugares.get(position);
        String preco = moeda.format(l.getPrecoMedio());
        String votos = l.getVotos() == 1 ? "1 voto" : l.getVotos() + " votos";

        h.nome.setText(l.getNome());
        h.categoria.setText(l.getCategoria());
        h.preco.setText("· " + preco);
        h.votos.setText(h.lider ? votos : "· " + votos);
        if (h.lider) {
            h.observacao.setText(l.getObservacao());
        } else {
            h.posicao.setText(String.valueOf(position + 1));
        }
        h.votar.setOnClickListener(v -> acoes.aoVotar(l));
        h.itemView.setOnClickListener(v -> acoes.aoClicar(l));
    }

    @Override public int getItemCount() { return lugares.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final boolean lider;
        final TextView nome, categoria, preco, votos, observacao, posicao;
        final Button votar;

        VH(View v, boolean lider) {
            super(v);
            this.lider = lider;
            nome = v.findViewById(lider ? R.id.txtNomeLider : R.id.txtNome);
            categoria = v.findViewById(lider ? R.id.txtCategoriaLider : R.id.txtCategoria);
            preco = v.findViewById(lider ? R.id.txtPrecoLider : R.id.txtPreco);
            votos = v.findViewById(lider ? R.id.txtVotosLider : R.id.txtVotos);
            votar = v.findViewById(lider ? R.id.btnVotarLider : R.id.btnVotar);
            observacao = lider ? v.findViewById(R.id.txtObservacaoLider) : null;
            posicao = lider ? null : v.findViewById(R.id.txtPosicao);
        }
    }
}
