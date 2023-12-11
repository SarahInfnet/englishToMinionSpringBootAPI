package br.com.infnet.FunTranslations.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrasePutBody {
    String fraseOriginal;
    Integer avaliacaoEstrelasCliente;
}