package br.com.infnet.FunTranslations;

import br.com.infnet.FunTranslations.utils.WebUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class EndpointTest {
    @Test
    @DisplayName("Deve retornar os dados da frase com id igual a 1")
    public void obtemFrasePeloId() {
        Long id = 1L;
        String urlBase = "http://localhost:8080/frases";
        String url = "%s/%d".formatted(urlBase, id);
        try {
            HttpResponse<String> resposta = WebUtils.get(url);
            assertEquals(200, resposta.statusCode());
            JsonNode respostaJson = WebUtils.getJsonFromHttpResponse(resposta);
            assertEquals(id, Long.parseLong(respostaJson.path("id").asText()));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Deve retornar uma resposta HTTP com status 404 ao solicitar uma frase inexistente.")
    public void obtemFrasePeloIdErro() {
        Long id = 999L;
        String urlBase = "http://localhost:8080/frases";
        String url = "%s/%d".formatted(urlBase, id);
        try {
            HttpResponse<String> resposta = WebUtils.get(url);
            assertEquals(404, resposta.statusCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Deve retornar uma resposta HTTP com status 400 quando solicitadas frases com parâmetros inválidos.")
    public void listaFrasePorPaginaErro() {
        try {
            String urlBase = "http://localhost:8080/frases";

            int page = -1;
            int size = 10;
            String url = "%s?page=%d&size=%d".formatted(urlBase, page, size);
            HttpResponse<String> resposta = WebUtils.get(url);
            assertEquals(400, resposta.statusCode());

            page = 2001;
            size = 10;
            url = "%s?page=%d&size=%d".formatted(urlBase, page, size);
            resposta = WebUtils.get(url);
            assertEquals(400, resposta.statusCode());

            page = 1;
            size = -1;
            url = "%s?page=%d&size=%d".formatted(urlBase, page, size);
            resposta = WebUtils.get(url);
            assertEquals(400, resposta.statusCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Deve retornar uma resposta HTTP com status 201 ao criar uma nova frase.")
    public void criaNovaFrase() {
        try {
            String urlBase = "http://localhost:8080/frases";
            String bodyString = "{\"fraseOriginal\":\"I am testing my POST endpoint.\"}";

            HttpResponse<String> resposta = WebUtils.post(urlBase, bodyString);
            assertEquals(201, resposta.statusCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Deve retornar uma resposta HTTP com status 400 ao criar uma nova frase com um corpo HTTP inválido.")
    public void criaNovaFraseErro() {
        try {
            String urlBase = "http://localhost:8080/frases";
            String bodyString = "{\"chaveInexistente\":\"Example of an incorrect body.\"}";

            HttpResponse<String> resposta = WebUtils.post(urlBase, bodyString);
            assertEquals(400, resposta.statusCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("Deve retornar um JSON contendo a tradução da frase enviada para a API do FunTranslation.")
    public void obtemTraducao() {
        try {
            String url = "https://api.funtranslations.com/translate/minion.json";
            String frase = "Testing an external API.";
            String dadosPost = "{\"text\": \"%s\"}".formatted(frase);

            HttpResponse<String> resposta = WebUtils.post(url, dadosPost);
            assertEquals(200, resposta.statusCode());
            JsonNode respostaJson = WebUtils.getJsonFromHttpResponse(resposta);
            assertTrue(respostaJson.path("contents").has("translated"));
        } catch (Exception e) {
            fail();
        }
    }
}
