package br.com.infnet.FunTranslations.service;

import br.com.infnet.FunTranslations.Exception.ResourceNotFoundException;
import br.com.infnet.FunTranslations.model.Frase;
import br.com.infnet.FunTranslations.model.FrasePostBody;
import br.com.infnet.FunTranslations.model.FrasePutBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class FraseService {
    private static final Logger logger = LoggerFactory.getLogger(FraseService.class);
    private Long ultimoId = 0L;
    private Map<Long, Frase> frases = initFrases();

    public Map<Long,Frase> getAllFrases() {
        return frases;
    }

    public Frase getFraseById(Long id) {
        Frase frase = frases.get(id);
        if(idNaoExiste(id)) throw new ResourceNotFoundException("Nenhuma frase encontrada com o id %d!".formatted(id));
        return frase;
    }

    private Map<Long, Frase> initFrases(){
        Map<Long, Frase> frases = new HashMap<>();

        String fraseOriginal = "The way to get started is to quit talking and begin doing.";
        String fraseMinion = "ta piu da ref capley tis da zek bidnib yee rob mido.";
        Frase frase = new Frase(obterId(), new ArrayList<Integer>(), fraseOriginal, fraseMinion);
        frases.put(frase.getId(), frase);

        fraseOriginal = "The future belongs to those who believe in the beauty of their dreams.";
        fraseMinion = "ta tegpul gotbah da be yob tilten een ta bitgym mud tus dibweb.";
        frase = new Frase(obterId(), new ArrayList<Integer>(), fraseOriginal, fraseMinion);
        frases.put(frase.getId(), frase);

        fraseOriginal = "If you look at what you have in life, you'll always have more. If you look at what you don't have in life, you'll never have enough";
        fraseMinion = "asa to bida ka Whaaat, yikai? . to kaylay een levo, to'll adtut kaylay mas, Yi kai yai yai!  asa to bida ka Whaaat, Yi kai yai yai! to domo kaylay een levo, to'll nopa kaylay sodre";
        frase = new Frase(obterId(), new ArrayList<Integer>(), fraseOriginal, fraseMinion);
        frases.put(frase.getId(), frase);

        return frases;
    }

    public Long obterId() {
        ultimoId++;
        return ultimoId;
    }

    private boolean idNaoExiste(long id) {
        return !frases.containsKey(id);
    }
    public void deleteById(long id) {
        if(idNaoExiste(id)) throw new ResourceNotFoundException("Nenhuma frase encontrada com o id %d!".formatted(id));
        frases.remove(id);
        logger.info("Frase removida com sucesso");
    }

    public void update(long id, FrasePutBody dadosAtualizados) {
        if(idNaoExiste(id)) throw new ResourceNotFoundException("Nenhuma frase encontrada com o id %d!".formatted(id));
        Frase frase = getFraseById(id);
        String novaFrase = dadosAtualizados.getFraseOriginal();
        Integer avaliacao = dadosAtualizados.getAvaliacaoEstrelasCliente();
        if((novaFrase != null) && (!frase.getFraseOriginal().equals(novaFrase))){
            frase.setFraseOriginal(novaFrase);
            String novaFraseTraducao = traduzParaMinion(frase.getFraseOriginal());
            frase.setFraseTraducao(novaFraseTraducao);
            frase.setAvaliacaoEstrelas(new ArrayList<Integer>());
        }
        else if(avaliacao != null){
            frase.avalia(avaliacao);
        }
    }

    public void create(FrasePostBody frase) throws BadRequestException, RuntimeException {
        String fraseOriginal = frase.getFraseOriginal();
        String fraseTraducao = traduzParaMinion(fraseOriginal);
        long id = obterId();
        Frase fraseObj = new Frase(id, new ArrayList<Integer>(), fraseOriginal, fraseTraducao);
        frases.put(fraseObj.getId(), fraseObj);
    }

    public String traduzParaMinion(String frase) {
        String url = "https://api.funtranslations.com/translate/minion.json";
        String dadosPost = "{\"text\": \"%s\"}".formatted(frase);
        try {
            HttpRequest traducaoReq = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(dadosPost))
                    .header("Content-Type", "application/json")
                    .version(HttpClient.Version.HTTP_2)
                    .uri(new URI(url))
                    .build();

            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> resposta = client.send(traducaoReq, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() >= 400) {
                logger.error("Resposta obtida a partir do servidor do FunTranslation. Status Code: {}",resposta.statusCode());
                throw new RuntimeException("Algum erro ocorreu!");
            } else {
                logger.info("Resposta obtida a partir do servidor do FunTranslation. Status Code: {}", resposta.statusCode());
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(resposta.body());
            JsonNode fraseTraduzidaNode = root.path("contents").path("translated");
            String fraseTraducao = fraseTraduzidaNode.asText();
            return fraseTraducao;

        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Frase> getAll() {
        return frases.values().stream().toList();
    }

    public List<Frase> getByPage(int page, int size) {
        List<Frase> all = getAll();
        int totalFrases =getTotalFrases();
        int start = (page -1) * size;
        int end = start + size;
        if(end > totalFrases) end = totalFrases;
        return all.subList(start,end);
    }

    public int getTotalFrases() {
        return frases.size();
    }

    public int getTotalPaginas(int size) {
        int totalFrases =getTotalFrases();
        return (int) Math.ceil((double)totalFrases / (double)size);
    }
}
