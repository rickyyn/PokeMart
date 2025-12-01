package br.edu.fatecgpg.tecprog.pokeapi.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ItemImagemService {
    @Value("${upload.dir}")
    private String uploadDir;
    public String salvarImagem(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String nomeUnico = file.getOriginalFilename();
        String ext = "";
        if (nomeUnico != null && nomeUnico.contains(".")) {
            ext = nomeUnico.substring(nomeUnico.lastIndexOf("."));
        }
        String nomeFinal = UUID.randomUUID().toString() + ext;
        Path finalPath = uploadPath.resolve(nomeFinal);
        Files.copy(file.getInputStream(), finalPath, StandardCopyOption.REPLACE_EXISTING);
        return nomeFinal;
    }
}
