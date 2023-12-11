package br.com.infnet.FunTranslations.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponsePayload {
    private String mensagem;
    private LocalDateTime dataHora;

    public ResponsePayload(String mensagem) {
        this.mensagem = mensagem;
        dataHora = LocalDateTime.now();
    }
}
