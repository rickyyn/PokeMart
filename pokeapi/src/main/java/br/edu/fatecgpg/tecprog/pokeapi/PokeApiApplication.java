package br.edu.fatecgpg.tecprog.pokeapi;

import br.edu.fatecgpg.tecprog.pokeapi.model.Categoria;
import br.edu.fatecgpg.tecprog.pokeapi.model.Item;
import br.edu.fatecgpg.tecprog.pokeapi.repository.CategoriaRepository;
import br.edu.fatecgpg.tecprog.pokeapi.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class PokeApiApplication implements CommandLineRunner {
    @Autowired
    private ItemRepository itemRepo;
    @Autowired
    private CategoriaRepository categoriaRepo;

    public static void main(String[] args) {
        SpringApplication.run(PokeApiApplication.class, args);
    }
    @Override
    public void run(String... args) throws Exception{
//        Categoria ItensBatalha = new Categoria("Batalha");
//        Categoria ItensTMs = new Categoria("TMs");
//        Categoria ItensPedras = new Categoria("Pedras");
//        Categoria ItensEvolução = new Categoria("Evolução");
//        Categoria ItensEXP = new Categoria("Experiência");
//        Categoria Frutas = new Categoria("Frutas");
//        Categoria ItensVitaminas = new Categoria("Vitaminas");
//        Categoria ItensUtilitarios = new Categoria("Utilitarios");
//        categoriaRepo.saveAll(Arrays.asList(ItensBatalha,ItensEvolução,ItensUtilitarios,
//                ItensEXP,Frutas,ItensVitaminas,ItensPedras,ItensTMs));

        List<Item> itensPorCategoria =
        itemRepo.findByCategoriaNome("Pokébola");
        itensPorCategoria.forEach(i -> System.out.println("Produto por categoria: " +
                i.getNome()));

        System.out.println("rodando");

    }
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/imagens/**")
                        .addResourceLocations("file:uploads/");
            }
        };
    }

}
