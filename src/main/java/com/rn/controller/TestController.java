package com.rn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public Object entry() {
        return new Response("done");
    }

    @GetMapping("/admin")
    public Object adminEntry(){
        return new Response("admin done");
    }

    @GetMapping("/user")
    public Object userEntry(){
        return new Response("user done");
    }




    private static class Response {

        public String status;

        public Response (String status){
            this.status = status;
        }
    }
}
