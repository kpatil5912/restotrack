package com.restotrack.service;

import com.restotrack.entity.Ingredient;
import com.restotrack.entity.MovementType;
import com.restotrack.entity.StockMovement;
import com.restotrack.repository.StockMovementRepository;
import com.restotrack.dto.WastageRequest;
import com.restotrack.dto.WastageReportResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Use case 3: log wastage and report on it. Wastage deducts stock via a WASTE
 * movement, and the daily report values each waste event using the ingredient's
 * cost per unit.
 */
@Service
public class WastageService {

    private final IngredientService ingredientService;
    private final StockMovementRepository movements;

    public WastageService(IngredientService ingredientService, StockMovementRepository movements) {
        this.ingredientService = ingredientService;
        this.movements = movements;
    }

    @Transactional
    public void logWastage(WastageRequest req) {
        Ingredient ingredient = ingredientService.get(req.ingredientId());
        BigDecimal inStockUnit = UnitConverter.convert(req.quantity(), req.unit(), ingredient.getStockUnit());
        ingredient.setCurrentStock(ingredient.getCurrentStock().subtract(inStockUnit));
        movements.save(new StockMovement(ingredient, MovementType.WASTE, inStockUnit, req.reason()));
    }

    @Transactional(readOnly = true)
    public WastageReportResponse dailyReport(LocalDate date) {
        ZoneId zone = ZoneId.systemDefault();
        Instant from = date.atStartOfDay(zone).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(zone).toInstant();

        List<StockMovement> wastes = movements.findByTypeInRange(MovementType.WASTE, from, to);
        List<WastageReportResponse.Line> lines = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (StockMovement m : wastes) {
            Ingredient ing = m.getIngredient();
            BigDecimal cost = m.getQuantity().multiply(ing.getCostPerUnit());
            total = total.add(cost);
            lines.add(new WastageReportResponse.Line(
                    ing.getName(), m.getQuantity(), ing.getStockUnit().name(), cost, m.getReason()));
        }
        return new WastageReportResponse(date, total, lines);
    }
}
