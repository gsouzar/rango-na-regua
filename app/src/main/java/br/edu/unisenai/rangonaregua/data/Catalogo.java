package br.edu.unisenai.rangonaregua.data;

import br.edu.unisenai.rangonaregua.model.Lugar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class Catalogo {

    private Catalogo() { }

    public static ArrayList<Lugar> inicial() {
        ArrayList<Lugar> lista = new ArrayList<>(Arrays.asList(
                new Lugar("Pastel do Seu Jorge", "Salgado", 9.50,
                        "Pastel de carne com queijo e caldo de cana. Fila grande depois das 19h.", 12),
                new Lugar("Marmita da Dona Rita", "Almoço", 22.00,
                        "Prato feito com repetição de arroz e feijão. Melhor custo por caloria da região.", 10),
                new Lugar("Burger do Léo", "Lanche", 28.00,
                        "Hambúrguer artesanal. Caro para o dia a dia, salva o pós-prova.", 8),
                new Lugar("Café da Tia Neide", "Café", 7.00,
                        "Pão na chapa e café coado de verdade. Abre às 6h.", 7),
                new Lugar("Sushi da Esquina", "Japonesa", 45.00,
                        "Rodízio no almoço de terça sai por menos da metade.", 5),
                new Lugar("Açaí 24h", "Sobremesa", 18.00,
                        "Único lugar aberto na madrugada de entrega de trabalho.", 4),
                new Lugar("Bar do Português", "Petisco", 32.00,
                        "Porção de calabresa que alimenta uma equipe inteira de PI.", 3),
                new Lugar("Padaria Aurora", "Salgado", 6.00,
                        "Coxinha grande e o melhor pão de queijo do quarteirão.", 2)
        ));
        ordenarPorVotos(lista);
        return lista;
    }


    public static void ordenarPorVotos(List<Lugar> lista) {
        Collections.sort(lista, new Comparator<Lugar>() {
            @Override
            public int compare(Lugar a, Lugar b) {
                if (b.getVotos() != a.getVotos()) {
                    return Integer.compare(b.getVotos(), a.getVotos());
                }
                return a.getNome().compareToIgnoreCase(b.getNome());
            }
        });
    }
}
