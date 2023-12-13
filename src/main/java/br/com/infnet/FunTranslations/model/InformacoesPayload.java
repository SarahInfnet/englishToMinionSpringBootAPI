package br.com.infnet.FunTranslations.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class InformacoesPayload {
    private int totalSize;
    private int totalPages;
}