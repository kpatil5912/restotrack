package com.restotrack.controller;

import com.restotrack.service.IngredientService;
import com.restotrack.dto.CreateIngredientRequest;
import com.restotrack.dto.IngredientResponse;
import com.restotrack.dto.RestockRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService service;

    public IngredientController(IngredientService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IngredientResponse> create(@Valid @RequestBody CreateIngredientRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IngredientResponse.from(service.create(req)));
    }

    @GetMapping
    public List<IngredientResponse> list() {
        return service.findAll().stream().map(IngredientResponse::from).toList();
    }

    @GetMapping("/{id}")
    public IngredientResponse get(@PathVariable Long id) {
        return IngredientResponse.from(service.get(id));
    }

    @GetMapping("/low-stock")
    public List<IngredientResponse> lowStock() {
        return service.findLowStock().stream().map(IngredientResponse::from).toList();
    }

    @PostMapping("/{id}/restock")
    public IngredientResponse restock(@PathVariable Long id, @Valid @RequestBody RestockRequest req) {
        return IngredientResponse.from(service.restock(id, req));
    }
}
