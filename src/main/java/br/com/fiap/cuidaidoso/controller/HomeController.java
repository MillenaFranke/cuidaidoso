package br.com.fiap.cuidaidoso.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint raiz usado para mostrar em qual ambiente a aplicação está rodando (staging ou production). */
@RestController
public class HomeController {

    private final String ambiente;

    public HomeController(@Value("${app.ambiente:local}") String ambiente) {
        this.ambiente = ambiente;
    }

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "projeto", "CuidaIdoso",
                "status", "online",
                "ambiente", ambiente);
    }
}
