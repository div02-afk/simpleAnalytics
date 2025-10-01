package com.simpleAnalytics.TenetService.controller;

import com.simpleAnalytics.TenetService.dto.APIKeyDTO;
import com.simpleAnalytics.TenetService.dto.ApplicationDTO;
import com.simpleAnalytics.TenetService.entity.Application;
import com.simpleAnalytics.TenetService.exception.ApplicationNotFoundException;
import com.simpleAnalytics.TenetService.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/tenet/{tenetId}")
    public ResponseEntity<UUID> createApplication(@PathVariable UUID tenetId, @RequestBody Application application) {
        try {
            UUID applicationId = applicationService.createApplication(tenetId, application);
            log.info("Created application with ID: {} for tenet: {}", applicationId, tenetId);
            return ResponseEntity.status(HttpStatus.CREATED).body(applicationId);
        } catch (Exception e) {
            log.error("Error creating application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getApplication(@PathVariable UUID id) {
        try {
            Application application = applicationService.getApplication(id);
            if (application != null) {
                ApplicationDTO applicationDTO = ApplicationDTO.fromEntity(application);
                return ResponseEntity.ok(applicationDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ApplicationNotFoundException e) {
            log.error("Application not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tenet/{tenetId}")
    public ResponseEntity<List<ApplicationDTO>> getAllApplicationsByTenet(@PathVariable UUID tenetId) {
        try {
            List<Application> applications = applicationService.getAllApplications(tenetId);
            List<ApplicationDTO> applicationDTOs = applications.stream()
                    .map(ApplicationDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(applicationDTOs);
        } catch (Exception e) {
            log.error("Error retrieving applications for tenet {}: {}", tenetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateApplication(@PathVariable UUID id, @RequestBody Application application) {
        try {
            applicationService.updateApplication(id, application);
            log.info("Updated application with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (ApplicationNotFoundException e) {
            log.error("Application not found for update: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/apikeys")
    public ResponseEntity<List<APIKeyDTO>> getApplicationApiKeys(@PathVariable UUID id) {
        try {
            Application application = applicationService.getApplication(id);
            if (application != null && application.getApiKeysList() != null) {
                List<APIKeyDTO> apiKeyDTOs = application.getApiKeysList().stream()
                        .map(APIKeyDTO::fromEntity)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(apiKeyDTOs);
            } else if (application != null) {
                return ResponseEntity.ok(List.of()); // Empty list if no API keys
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (ApplicationNotFoundException e) {
            log.error("Application not found for API keys: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving API keys for application {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
