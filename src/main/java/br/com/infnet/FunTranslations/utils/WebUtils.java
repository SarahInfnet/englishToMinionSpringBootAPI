package br.com.infnet.FunTranslations.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WebUtils {
    public static HttpResponse<String> post(String url, String bodyString) throws URISyntaxException, InterruptedException, IOException {
        try {
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(bodyString))
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_2)
                    .uri(new URI(url))
                    .build();

            HttpClient cliente = HttpClient.newBuilder().build();
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

            return resposta;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw e;
        }
    }

    public static HttpResponse<String> get(String url) throws URISyntaxException, InterruptedException, IOException {
        try {
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .GET()
                    .version(HttpClient.Version.HTTP_2)
                    .uri(new URI(url))
                    .build();

            HttpClient cliente = HttpClient.newBuilder().build();
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());

            return resposta;
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw e;
        }
    }

    public static JsonNode getJsonFromHttpResponse(HttpResponse<String> resposta) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(resposta.body());
        return root;
    }

}
