package com.rn.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {


    @GetMapping
    public Object entry(){
        return new Response("done");
    }




    private static class Response {

        public String status;

        public Response (String status){
            this.status = status;
        }
    }
}
