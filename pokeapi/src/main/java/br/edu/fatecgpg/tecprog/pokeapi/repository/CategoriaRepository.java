package br.edu.fatecgpg.tecprog.pokeapi.repository;
import br.edu.fatecgpg.tecprog.pokeapi.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    //consultas personalizadas
    boolean existsByNome(String nome);
    Optional<Categoria> findByNomeIgnoreCase(String nome);
}

