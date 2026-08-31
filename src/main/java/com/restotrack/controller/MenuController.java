package com.restotrack.controller;

import com.restotrack.entity.MenuItem;
import com.restotrack.entity.RecipeItem;
import com.restotrack.service.MenuService;
import com.restotrack.dto.CreateMenuItemRequest;
import com.restotrack.dto.SellRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu-items")
public class MenuController {

    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateMenuItemRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toView(service.create(req)));
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return service.findAll().stream().map(this::toView).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable Long id) {
        return toView(service.get(id));
    }

    /** Sell a dish; depletes recipe ingredients from stock. */
    @PostMapping("/sell")
    public ResponseEntity<Void> sell(@Valid @RequestBody SellRequest req) {
        service.sell(req);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toView(MenuItem item) {
        List<Map<String, Object>> recipe = item.getRecipe().stream()
                .map((RecipeItem r) -> Map.<String, Object>of(
                        "ingredientId", r.getIngredient().getId(),
                        "ingredientName", r.getIngredient().getName(),
                        "quantity", r.getQuantity(),
                        "unit", r.getUnit()))
                .toList();
        return Map.of(
                "id", item.getId(),
                "name", item.getName(),
                "sellingPrice", item.getSellingPrice() == null ? BigDecimal.ZERO : item.getSellingPrice(),
                "recipe", recipe);
    }
}
