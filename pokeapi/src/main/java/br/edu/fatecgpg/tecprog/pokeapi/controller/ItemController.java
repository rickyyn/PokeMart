package br.edu.fatecgpg.tecprog.pokeapi.controller;
import br.edu.fatecgpg.tecprog.pokeapi.model.Categoria;
import br.edu.fatecgpg.tecprog.pokeapi.repository.CategoriaRepository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import br.edu.fatecgpg.tecprog.pokeapi.model.Item;
import br.edu.fatecgpg.tecprog.pokeapi.repository.ItemRepository;
import br.edu.fatecgpg.tecprog.pokeapi.service.ItemImagemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.web.SortDefault;


@RestController
@RequestMapping("/itens")
public class ItemController {
    @Autowired
    private CategoriaRepository categoriaRepo;
    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private ItemImagemService servicoImagem;
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> criarItemComImagem(
            @RequestPart("dados") String itemJson,
            @RequestPart("imagem") MultipartFile imagem
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Item item = objectMapper.readValue(itemJson, Item.class);
            if (item.getCategoria() != null) {
                Categoria catJson = item.getCategoria();
                Categoria categoriaFinal = null;
                if (catJson.getId() != null) {
                    categoriaFinal = categoriaRepo.findById(catJson.getId())
                            .orElseThrow(() -> new RuntimeException("Categoria não encontrada com o ID: " + catJson.getId()));
                }
                else if (catJson.getNome() != null && !catJson.getNome().isEmpty()) {
                    Optional<Categoria> catExistente = categoriaRepo.findByNomeIgnoreCase(catJson.getNome());
                    if (catExistente.isPresent()) {
                        categoriaFinal = catExistente.get();
                    } else {
                        categoriaFinal = categoriaRepo.save(catJson);
                    }
                }
                if (categoriaFinal != null) {
                    item.setCategoria(categoriaFinal);
                } else {
                    return ResponseEntity.badRequest().body("Categoria inválida: informe ID ou Nome.");
                }
            }
            String nomeArquivo = servicoImagem.salvarImagem(imagem);
            item.setImagemUrl(nomeArquivo);
            Item novoItem = itemRepo.save(item);
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            novoItem.setImagemUrl(baseUrl + "/imagens/" + nomeArquivo);
            return ResponseEntity.ok(novoItem);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar item: " + e.getMessage());
        }
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> atualizarItem(
            @PathVariable Long id,
            @RequestPart("dados") String itemJson,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem // <--- IMAGEM OPCIONAL
    ) {
        try {
            Item itemExistente = itemRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item não encontrado com id: " + id));
            ObjectMapper objectMapper = new ObjectMapper();
            Item dadosNovos = objectMapper.readValue(itemJson, Item.class);
            itemExistente.setNome(dadosNovos.getNome());
            itemExistente.setDescricao(dadosNovos.getDescricao());
            itemExistente.setPreco(dadosNovos.getPreco());
            itemExistente.setQtd(dadosNovos.getQtd());
            if (dadosNovos.getCategoria() != null) {
                Categoria catJson = dadosNovos.getCategoria();
                Categoria categoriaFinal = null;
                if (catJson.getId() != null) {
                    categoriaFinal = categoriaRepo.findById(catJson.getId())
                            .orElseThrow(() -> new RuntimeException("Categoria não encontrada ID: " + catJson.getId()));
                }
                else if (catJson.getNome() != null && !catJson.getNome().isEmpty()) {
                    Optional<Categoria> catExistente = categoriaRepo.findByNomeIgnoreCase(catJson.getNome());
                    if (catExistente.isPresent()) {
                        categoriaFinal = catExistente.get();
                    } else {
                        categoriaFinal = categoriaRepo.save(catJson);
                    }
                }
                if (categoriaFinal != null) {
                    itemExistente.setCategoria(categoriaFinal);
                }
            }
            if (imagem != null && !imagem.isEmpty()) {
                String novoNomeArquivo = servicoImagem.salvarImagem(imagem);
                itemExistente.setImagemUrl(novoNomeArquivo);
            }
            Item itemAtualizado = itemRepo.save(itemExistente);
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String nomeNoBanco = itemAtualizado.getImagemUrl();
            if (nomeNoBanco != null) {
                itemAtualizado.setImagemUrl(baseUrl + "/imagens/" + nomeNoBanco);
            }
            return ResponseEntity.ok(itemAtualizado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<Item>> listarItens(
            @SortDefault (sort= {"categoria.nome","preco","nome"}, direction=Sort.Direction.ASC) Sort sort)
    {
        List<Item> itens = itemRepo.findAll(sort);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        for (Item item : itens) {
            String nomeArquivoNoBanco = item.getImagemUrl();
            if (nomeArquivoNoBanco != null && !nomeArquivoNoBanco.startsWith("http")) {
                String urlCompleta = baseUrl + "/imagens/" + nomeArquivoNoBanco;
                item.setImagemUrl(urlCompleta);
            }
        }
        return ResponseEntity.ok(itens);
    }
    @PostMapping("/{id}/comprar")
    public ResponseEntity<?> comprarItem(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> compra
    ) {
        try {
            Integer qtdComprada = compra.get("qtd");
            if (qtdComprada == null || qtdComprada <= 0) {
                return ResponseEntity.badRequest().body("Quantidade inválida.");
            }
            Item item = itemRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Item não encontrado."));
            if (item.getQtd() < qtdComprada) {
                return ResponseEntity.badRequest().body("Estoque insuficiente! Restam apenas: " + item.getQtd());
            }
            item.setQtd(item.getQtd() - qtdComprada);
            itemRepo.save(item);
            return ResponseEntity.ok("Compra realizada com sucesso! Novo estoque: " + item.getQtd());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar compra: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarItem(@PathVariable Long id) {
        try {
            if (itemRepo.existsById(id)) {
                itemRepo.deleteById(id);
                return ResponseEntity.ok("Item " + id + " apagado");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao apagar: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarItem(@PathVariable Long id) {
        return itemRepo.findById(id)
                .map(item -> ResponseEntity.ok(item))
                .orElse(ResponseEntity.notFound().build());
    }

}


