package com.simpleAnalytics.TenetService.controller;

import com.simpleAnalytics.TenetService.dto.TenetDTO;
import com.simpleAnalytics.TenetService.entity.Tenet;
import com.simpleAnalytics.TenetService.exception.PlanNotFoundException;
import com.simpleAnalytics.TenetService.exception.TenetNotFoundException;
import com.simpleAnalytics.TenetService.service.TenetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/tenets")
@RequiredArgsConstructor
public class TenetController {

    private final TenetService tenetService;

    @PostMapping
    public ResponseEntity<UUID> createTenet(@RequestBody TenetDTO tenetDTO) {
        try {
            UUID tenetId = tenetService.createTenet(tenetDTO);
            log.info("Created tenet with ID: {}", tenetId);
            return ResponseEntity.status(HttpStatus.CREATED).body(tenetId);
        } catch (Exception e) {
            log.error("Error creating tenet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenetDTO> getTenet(@PathVariable UUID id) {
        try {
            TenetDTO tenet = tenetService.getTenet(id);
            if (tenet != null) {
                return ResponseEntity.ok(tenet);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (TenetNotFoundException e) {
            log.error("Tenet not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving tenet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTenet(@PathVariable UUID id, @RequestBody Tenet tenet) {
        try {
            tenetService.updateTenet(id, tenet);
            log.info("Updated tenet with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (TenetNotFoundException e) {
            log.error("Tenet not found for update: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating tenet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{tenetId}/plan/{planId}")
    public ResponseEntity<Void> setPlan(@PathVariable UUID tenetId, @PathVariable UUID planId) {
        try {
            tenetService.setPlan(tenetId, planId);
            log.info("Set plan {} for tenet {}", planId, tenetId);
            return ResponseEntity.ok().build();
        } catch (TenetNotFoundException e) {
            log.error("Tenet not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (PlanNotFoundException e) {
            log.error("Plan not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error setting plan for tenet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenet(@PathVariable UUID id) {
        try {
            tenetService.deleteTenet(id);
            log.info("Deleted tenet with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (TenetNotFoundException e) {
            log.error("Tenet not found for deletion: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting tenet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
