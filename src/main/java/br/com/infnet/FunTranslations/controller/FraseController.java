package br.com.infnet.FunTranslations.controller;

import br.com.infnet.FunTranslations.Exception.ResourceNotFoundException;
import br.com.infnet.FunTranslations.model.*;
import br.com.infnet.FunTranslations.service.FraseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/frases")
public class FraseController {
    @Autowired
    FraseService fraseService;
    @GetMapping
    public ResponseEntity<?> getFrasesByPage(
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "1") int page
    ){
        try {
            int MAX_RESULTS_PER_PAGE = 2000;
            int qtdPaginas = fraseService.getTotalPaginas(size);
            if (page < 1 || page > qtdPaginas) throw new InvalidParameterException("Parâmetro page inválido.");
            if (size < 1 || size > MAX_RESULTS_PER_PAGE)
                throw new InvalidParameterException("Parâmetro size inválido.");

            List<Frase> frasesPagina = fraseService.getByPage(page, size);

            int totalFrases = frasesPagina.size();
            new InformacoesPayload(totalFrases, qtdPaginas);
            InformacoesPayload infoPayload = InformacoesPayload.builder()
                    .totalSize(totalFrases)
                    .totalPages(qtdPaginas)
                    .build();

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("total-size", String.valueOf(totalFrases));
            responseHeaders.set("total-pages", String.valueOf(qtdPaginas));
            responseHeaders.set("Content-Type", "application/json");

            FrasePayload frasePayload = new FrasePayload(frasesPagina, infoPayload);

            return ResponseEntity.status(HttpStatus.OK)
                    .headers(responseHeaders).body(frasePayload);
        } catch (InvalidParameterException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponsePayload(e.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(500).body(new ResponsePayload("Houve um erro no servidor"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Frase> getFraseById(@PathVariable Long id) {
        try{
            Frase frase = fraseService.getFraseById(id);
            return ResponseEntity.ok(frase);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping
    public ResponseEntity<ResponsePayload> create(@Valid @RequestBody FrasePostBody frase){
        try{
            fraseService.create(frase);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponsePayload("Frase cadastrada com sucesso!"));
        }catch(Exception e){
            return ResponseEntity.status(500)
                    .body(new ResponsePayload("Houve um erro no servidor"));
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponsePayload> delete(@PathVariable Long id){
        try {
            fraseService.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponsePayload(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponsePayload> update(@PathVariable Long id, @RequestBody FrasePutBody dadosAtualizados){
        try{
            fraseService.update(id,dadosAtualizados);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);

        } catch (ResourceNotFoundException ex){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponsePayload(ex.getMessage()));
        } catch (Exception e){
            return ResponseEntity.status(500)
                    .body(new ResponsePayload("Houve um erro no servidor"));
        }
    }
}
