package br.edu.fatecgpg.tecprog.pokeapi.controller;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import br.edu.fatecgpg.tecprog.pokeapi.model.Item;
import br.edu.fatecgpg.tecprog.pokeapi.repository.ItemRepository;
import br.edu.fatecgpg.tecprog.pokeapi.service.ItemImagemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/itemimg")
public class ItemImagemController {
    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private ItemImagemService servicoImagem;
    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem
    ) {
        try {
            Item item = itemRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item não encontrado"));
            String nomeArquivo = servicoImagem.salvarImagem(imagem);
            item.setImagemUrl(nomeArquivo);
            itemRepo.save(item);
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String urlParaRetorno = baseUrl + "/imagens/" + nomeArquivo;
            return ResponseEntity.ok(urlParaRetorno);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar a imagem: " + e.getMessage());
        }
    }
}
