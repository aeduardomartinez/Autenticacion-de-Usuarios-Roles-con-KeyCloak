package com.api.rest.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestController {


    @GetMapping("/hola-1")
    @PreAuthorize("hasRole('admin_client_role')")
    public String helloAdmi(){
        return "hola";
    }


    @GetMapping("/hola-2")
    @PreAuthorize("hasRole('user_cliente_role') or hasRole('admin_client_role')")
    public String helloUser(){
        return "hola";
    }
}
