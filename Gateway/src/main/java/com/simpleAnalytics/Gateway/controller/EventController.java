package com.simpleAnalytics.Gateway.controller;


import com.simpleAnalytics.Gateway.entity.Context;
import com.simpleAnalytics.Gateway.entity.UserEvent;
import com.simpleAnalytics.Gateway.exception.InsufficientCreditsException;
import com.simpleAnalytics.Gateway.exception.InvalidAPIKeyException;
import com.simpleAnalytics.Gateway.service.AuthenticationService;
import com.simpleAnalytics.Gateway.service.EventPipelineService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventPipelineService eventPipelineService;
    private final AuthenticationService authenticationService;




    @PostMapping
    public ResponseEntity<String> postEvent(HttpServletRequest request, @RequestBody UserEvent event, @RequestHeader("X-Auth") UUID apikey) {

        try {
            Context context = (Context) request.getAttribute("context");
//            log.info("Context: {}", context);
            authenticationService.authenticate(apikey, event.getAppId());
            eventPipelineService.processEvent(event, apikey, context);
        } catch (InsufficientCreditsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        catch(InvalidAPIKeyException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing event");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
