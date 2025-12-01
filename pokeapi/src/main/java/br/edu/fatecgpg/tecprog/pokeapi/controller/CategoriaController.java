package br.edu.fatecgpg.tecprog.pokeapi.controller;

import br.edu.fatecgpg.tecprog.pokeapi.model.Categoria;
import br.edu.fatecgpg.tecprog.pokeapi.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Repository
@RestController
@RequestMapping("/categoria")
public class CategoriaController {
    @Autowired
    private CategoriaRepository categoriaRepo;
    @GetMapping
    public List<Categoria> listar(){
        return categoriaRepo.findAll();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCategoria(@PathVariable Long id) {
        try {
            if (categoriaRepo.existsById(id)) {
                categoriaRepo.deleteById(id);
                return ResponseEntity.ok("Categoria " + id + " apagada");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao apagar: " + e.getMessage());
        }
    }
}
