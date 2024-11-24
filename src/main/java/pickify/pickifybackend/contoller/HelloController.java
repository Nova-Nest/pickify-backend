package pickify.pickifybackend.contoller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pickify.pickifybackend.domain.Hello;
import pickify.pickifybackend.repository.HelloRepository;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final HelloRepository helloRepository;

    @PostMapping("/hello/save")
    public String saveHello() {
        Hello hello = new Hello();
        Hello save = helloRepository.save(hello);
        return save.getId();

    }

}
