package wekarest

import wekarest.model.DataFile
import wekarest.mongodb.FileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Application implements CommandLineRunner {

    @Autowired
    FileRepository repository;

    static void main(String[] args) {
        SpringApplication.run(Application, args)
    }

    public void run(String... args) throws Exception {
        repository.deleteAll();
        def fileContent = 'hello world'
        repository.save(new DataFile(hash: fileContent.asMD5(), data: fileContent as byte[]));
    }
}
