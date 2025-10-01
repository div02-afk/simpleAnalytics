package com.simpleAnalytics.TenetService.controller;


import com.simpleAnalytics.TenetService.entity.APIKeyInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TenetController {
    @GetMapping
    public ResponseEntity<APIKeyInfo> getAPIKeyInfo() {
        try{
            return ResponseEntity.ok(new APIKeyInfo());
        }catch(Exception e){

            return ResponseEntity.notFound().build();
        }
    }


    
}
