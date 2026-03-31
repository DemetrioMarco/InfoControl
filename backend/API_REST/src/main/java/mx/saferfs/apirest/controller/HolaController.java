package mx.saferfs.apirest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HolaController {

    @GetMapping("/hola")
    public String hola(){
        return "Hola mundo desde Spring Boot";
    }

}
