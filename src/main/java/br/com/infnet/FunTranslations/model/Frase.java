package br.com.infnet.FunTranslations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.InvalidParameterException;
import java.util.ArrayList;

@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class Frase {
    private long id;
    private ArrayList<Integer> avaliacaoEstrelas;
    private String fraseOriginal;
    private String fraseTraducao;

    public void avalia(int qtdEstrelas){
        if(qtdEstrelas > 5 || qtdEstrelas < 1){
            throw new InvalidParameterException("Quantidade de estrelas deve ser entre 1 e 5!");
        }
        avaliacaoEstrelas.add(qtdEstrelas);
    }
}
