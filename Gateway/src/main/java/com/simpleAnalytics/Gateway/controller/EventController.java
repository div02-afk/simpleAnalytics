package com.simpleAnalytics.Gateway.controller;


import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.UserEvent;
import com.simpleAnalytics.Gateway.service.EventPipelineService;
import com.simpleAnalytics.Gateway.service.EventPipelineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;

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
        Context context = exchange.getAttribute("context");
        eventPipelineService.processEvent(event, context);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
