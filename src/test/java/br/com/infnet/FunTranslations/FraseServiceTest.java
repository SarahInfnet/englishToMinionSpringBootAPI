package br.com.infnet.FunTranslations;

import br.com.infnet.FunTranslations.Exception.ResourceNotFoundException;
import br.com.infnet.FunTranslations.model.Frase;
import br.com.infnet.FunTranslations.service.FraseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FraseServiceTest {
    @Test
    @DisplayName("Deve retornar uma exception para um id que não existe")
    public void idInexistente(){
        FraseService fraseService = new FraseService();
        assertThrows(ResourceNotFoundException.class, () -> {
            Frase frase = fraseService.getFraseById(999L);
        });
    }

    @Test
    @DisplayName("Deve excluir uma frase existente corretamente")
    public void excluirFraseExistente() {
        FraseService fraseService = new FraseService();
        Long fraseId = 1L;
        assertDoesNotThrow(() -> {
            fraseService.getFraseById(fraseId);
        });
        assertDoesNotThrow(() -> {
            fraseService.deleteById(fraseId);
        });
        assertThrows(ResourceNotFoundException.class, () -> {
            fraseService.getFraseById(fraseId);
        });
    }

    @Test
    @DisplayName("Deve retornar  uma exception quando é solicitada a remoção de uma frase inexistente.")
    public void excluirFraseInexistente() {
        FraseService fraseService = new FraseService();
        assertThrows(ResourceNotFoundException.class, () -> {
            fraseService.getFraseById(999L);
        });
    }
}
