package br.com.infnet.FunTranslations.controller;

import br.com.infnet.FunTranslations.Exception.ResourceNotFoundException;
import br.com.infnet.FunTranslations.model.Frase;
import br.com.infnet.FunTranslations.model.FrasePostBody;
import br.com.infnet.FunTranslations.model.FrasePutBody;
import br.com.infnet.FunTranslations.model.ResponsePayload;
import br.com.infnet.FunTranslations.service.FraseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/frases")
public class FraseController {
    @Autowired
    FraseService fraseService;
//Em um dos seus endpoints você deverá consumir alguma API externa à sua escolha.
//Você deverá converter a resposta dessa chamada de JSON para um objeto java. Imprima(com Log) o status code dessa resposta.
//Você deverá criar os testes para o seus métodos. Pelo menos um dos testes deverá ter um assertThrows. Não utilize System.out e sim o mecanismo de LOG. Utilize o Lombok.

    @GetMapping
    public ResponseEntity<Map<Long,Frase>> getAllFrases() {
        Map<Long,Frase> frases = fraseService.getAllFrases();
        return ResponseEntity.ok(frases);
    }

    //No endpoint do tipo GET você deverá receber 2 parâmetros opcionais.
    //Algum de seus métodos deve tratar a requisição do usuário, em caso de erro retorne algum código de erro e em caso de sucesso retorne 200.
    @GetMapping("/{id}")
    public ResponseEntity<Frase> getFraseById(@PathVariable Long id) {
        try{
            Frase frase = fraseService.getFraseById(id);
            return ResponseEntity.ok(frase);
        }catch (ResourceNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
//No Endpoint que receberá um POST você deverá receber um JSON com pelo menos um campo do tipo String, um do tipo número e um array de qualquer tipo.
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
