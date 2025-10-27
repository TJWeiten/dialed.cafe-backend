package cafe.dialed.controllers;

import cafe.dialed.entities.User;
import cafe.dialed.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cafe.dialed.entities.Bean;
import cafe.dialed.repositories.BeanRepository;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/beans")
public class BeanController {

    private final BeanRepository beanRepository;
    private final UserRepository userRepository;

    public BeanController(BeanRepository beanRepository, UserRepository userRepository) {
        this.beanRepository = beanRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/hw")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/getAll")
    public List<Bean> getDaBeans() {
        System.out.println("requested to get all beans");
        return beanRepository.findAll();
    }

    @GetMapping("/user/getAll")
    public List<User> getDaUsers() {
        return userRepository.findAll();
    }

//    @GetMapping("/user/{userId}") // Responds to GET requests at /beans/user/{userId}
//    public List<Bean> getBeansByUser(@PathVariable UUID userId) {
//        return beanRepository.findByUserId(userId);
//    }

}
