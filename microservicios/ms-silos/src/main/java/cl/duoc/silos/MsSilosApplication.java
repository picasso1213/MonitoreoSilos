package cl.duoc.silos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsSilosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsSilosApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(SiloRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            repo.save(new Silo("Granja Los Aromos", "Silo A1", 20000, 16400, "Engorda", 1500));
            repo.save(new Silo("Granja Los Aromos", "Silo A2", 20000, 5200, "Crecimiento", 1400));
            repo.save(new Silo("Granja Los Aromos", "Silo A3", 15000, 1800, "Inicio", 900));
            repo.save(new Silo("Granja Los Aromos", "Silo A4", 20000, 12000, "Postura", 1300));
            repo.save(new Silo("Granja El Sauce", "Silo B1", 25000, 21000, "Engorda", 1800));
            repo.save(new Silo("Granja El Sauce", "Silo B2", 25000, 6900, "Crecimiento", 1700));
        };
    }
}