package pl.lodz.p.it.usermodule.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/guest")
    public String hiGuest() {
        return "Hi guest, your email: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @GetMapping("/user")
    public String hiUser() {
        return "Hi user, your email: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/admin")
    public String hiAdmin() {
        return "Hi admin, your email: " + SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
