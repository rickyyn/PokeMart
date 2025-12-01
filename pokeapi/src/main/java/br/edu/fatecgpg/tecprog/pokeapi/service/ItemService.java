package br.edu.fatecgpg.tecprog.pokeapi.service;

import br.edu.fatecgpg.tecprog.pokeapi.model.Item;
import br.edu.fatecgpg.tecprog.pokeapi.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.ConditionalOnOAuth2ClientRegistrationProperties;

public class ItemService {
//Verificações Tratamentos
//Regras antes de salvar ou deletar
//Chamadas ao repository
    @Autowired
    private ItemRepository itemRepository;

    public Item salvar(Item item) {
        if (itemRepository.existsByNome(item.getNome())) {
            throw new RuntimeException("Esse item já existe");
        }
        return itemRepository.save(item);
    }
}
