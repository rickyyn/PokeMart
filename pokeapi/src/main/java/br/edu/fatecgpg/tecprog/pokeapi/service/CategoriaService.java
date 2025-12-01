package br.edu.fatecgpg.tecprog.pokeapi.service;

import br.edu.fatecgpg.tecprog.pokeapi.model.Categoria;
import br.edu.fatecgpg.tecprog.pokeapi.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CategoriaService {
    //Verificações Tratamentos
    //Regras antes de salvar ou deletar
    //Chamadas ao repository
    @Autowired
    private CategoriaRepository categoriaRepository;
    public Categoria salvar(Categoria categoria) {
        if (categoriaRepository.existsByNome(categoria.getNome())) {
            throw new RuntimeException("Categoria ja existente");
        }
        return categoriaRepository.save(categoria);
    }
}
