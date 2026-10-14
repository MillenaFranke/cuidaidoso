package br.com.fiap.cuidaidoso.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CuidaIdosoApiTest {

    @Autowired
    private MockMvc mvc;

    private int criar(String url, String json) throws Exception {
        String corpo = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(corpo, "$.id");
    }

    private int criarFamilia(String email, String plano) throws Exception {
        return criar("/api/familias", """
                {"nome":"Família Teste","email":"%s","plano":"%s"}
                """.formatted(email, plano));
    }

    private static final String IDOSO = """
            {"nome":"Dona Maria","dataNascimento":"1945-03-10","observacoes":"Hipertensa"}
            """;

    @Test
    void homeInformaAmbiente() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projeto").value("CuidaIdoso"))
                .andExpect(jsonPath("$.ambiente").value("test"));
    }

    @Test
    void healthEstaUp() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void planoBasicoBloqueiaTerceiroIdoso() throws Exception {
        int familia = criarFamilia("basico@exemplo.com", "BASICO");
        criar("/api/familias/" + familia + "/idosos", IDOSO);
        criar("/api/familias/" + familia + "/idosos", IDOSO);

        mvc.perform(post("/api/familias/" + familia + "/idosos")
                        .contentType(MediaType.APPLICATION_JSON).content(IDOSO))
                .andExpect(status().isConflict());

        mvc.perform(get("/api/familias/" + familia + "/idosos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void planoPremiumAceitaVariosIdosos() throws Exception {
        int familia = criarFamilia("premium@exemplo.com", "PREMIUM");
        for (int i = 0; i < 4; i++) {
            criar("/api/familias/" + familia + "/idosos", IDOSO);
        }
        mvc.perform(get("/api/familias/" + familia + "/idosos"))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void emailDuplicadoRetornaConflito() throws Exception {
        criarFamilia("duplicado@exemplo.com", "BASICO");
        mvc.perform(post("/api/familias").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Outra","email":"duplicado@exemplo.com","plano":"BASICO"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void dadosInvalidosRetornamBadRequest() throws Exception {
        mvc.perform(post("/api/familias").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"","email":"nao-e-email","plano":"BASICO"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void familiaInexistenteRetornaNotFound() throws Exception {
        mvc.perform(get("/api/familias/999999")).andExpect(status().isNotFound());
    }

    @Test
    void agendaMostraDosesERotinasEmOrdemDeHorario() throws Exception {
        int familia = criarFamilia("agenda@exemplo.com", "BASICO");
        int idoso = criar("/api/familias/" + familia + "/idosos", IDOSO);
        int medicamento = criar("/api/idosos/" + idoso + "/medicamentos", """
                {"nome":"Losartana","dosagem":"50mg","horarios":["08:00","20:00"]}
                """);
        // 2026-10-12 é segunda-feira
        criar("/api/idosos/" + idoso + "/rotinas", """
                {"descricao":"Caminhada","diaSemana":"MONDAY","horario":"07:00"}
                """);
        criar("/api/idosos/" + idoso + "/rotinas", """
                {"descricao":"Fisioterapia","diaSemana":"TUESDAY","horario":"15:00"}
                """);

        mvc.perform(put("/api/medicamentos/" + medicamento + "/doses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"2026-10-12","horario":"08:00","tomada":true}
                                """))
                .andExpect(status().isOk());

        mvc.perform(get("/api/idosos/" + idoso + "/agenda").param("data", "2026-10-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens", hasSize(3)))
                .andExpect(jsonPath("$.itens[0].tipo").value("ROTINA"))
                .andExpect(jsonPath("$.itens[0].descricao").value("Caminhada"))
                .andExpect(jsonPath("$.itens[1].tipo").value("MEDICAMENTO"))
                .andExpect(jsonPath("$.itens[1].tomada").value(true))
                .andExpect(jsonPath("$.itens[2].tomada").value(false));

        // a rotina de terça não aparece na segunda, e a dose de segunda não conta na terça
        mvc.perform(get("/api/idosos/" + idoso + "/agenda").param("data", "2026-10-13"))
                .andExpect(jsonPath("$.itens", hasSize(3)))
                .andExpect(jsonPath("$.itens[0].tipo").value("MEDICAMENTO"))
                .andExpect(jsonPath("$.itens[0].tomada").value(false));
    }

    @Test
    void doseComHorarioForaDaPrescricaoEhRejeitada() throws Exception {
        int familia = criarFamilia("dose@exemplo.com", "BASICO");
        int idoso = criar("/api/familias/" + familia + "/idosos", IDOSO);
        int medicamento = criar("/api/idosos/" + idoso + "/medicamentos", """
                {"nome":"Metformina","dosagem":"850mg","horarios":["12:00"]}
                """);

        mvc.perform(put("/api/medicamentos/" + medicamento + "/doses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"data":"2026-10-12","horario":"09:30","tomada":true}
                                """))
                .andExpect(status().isConflict());
    }
}
