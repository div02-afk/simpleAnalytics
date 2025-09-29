package com.simpleAnalytics.Gateway.controller;


import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.UserEvent;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;
import com.simpleAnalytics.Gateway.service.EventPipelineService;
import com.simpleAnalytics.Gateway.service.EventPipelineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;

@RestController
@RequestMapping("/event")
public class EventController {
    EventPipelineService eventPipelineService;

    @Autowired
    EventController(EventPipelineService eventPipelineService) {
        this.eventPipelineService = eventPipelineService;
    }


    @PostMapping
    public ResponseEntity<String> postEvent(@RequestBody UserEvent event, ServerWebExchange exchange) {
        UUID auth = UUID.randomUUID();
        Context context = exchange.getAttribute("context");
        try{
            eventPipelineService.processEvent(event,auth, context);
        }catch (InsufficientCreditsException | InvalidAPIKeyException e ){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing event");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
