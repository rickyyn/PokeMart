package br.edu.fatecgpg.tecprog.pokeapi.repository;
import br.edu.fatecgpg.tecprog.pokeapi.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    //consultas personalizadas
    boolean existsByNome(String nome);
    List<Item> findByCategoriaNome(String nomeCategoria);


}
