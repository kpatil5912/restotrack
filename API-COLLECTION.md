# RestoTrack — API Collection (curl)

Base URL: `http://localhost:9091`

All request/response bodies are JSON. Seed ingredients (ids 1–6) are loaded on first run:
`1 Flour (KG)`, `2 Chicken (KG)`, `3 Tomato (KG)`, `4 Cheese (KG)`, `5 Olive Oil (LITRE)`, `6 Burger Bun (PIECE)`.

Valid `unit` values: `GRAM`, `KILOGRAM`, `MILLILITRE`, `LITRE`, `PIECE`.

> On Windows PowerShell, `curl` is an alias for `Invoke-WebRequest`. Use `curl.exe` (shown below) to run these as real curl, or use the PowerShell examples at the end.

---

## Ingredients

### Create an ingredient — `POST /api/ingredients`
```bash
curl.exe -X POST http://localhost:9091/api/ingredients \
  -H "Content-Type: application/json" \
  -d '{ "name": "Basil", "stockUnit": "KILOGRAM", "costPerUnit": 120, "lowStockThreshold": 0.2 }'
```
`201 Created`
```json
{ "id": 7, "name": "Basil", "stockUnit": "KILOGRAM", "currentStock": 0,
  "costPerUnit": 120, "lowStockThreshold": 0.2, "lowStock": true }
```
> New ingredients start at zero stock. Add stock via the restock endpoint.

### List ingredients — `GET /api/ingredients`
```bash
curl.exe http://localhost:9091/api/ingredients
```
`200 OK` — array of `IngredientResponse`.

### Get one ingredient — `GET /api/ingredients/{id}`
```bash
curl.exe http://localhost:9091/api/ingredients/1
```
`200 OK`
```json
{ "id": 1, "name": "Flour", "stockUnit": "KILOGRAM", "currentStock": 10.00,
  "costPerUnit": 40.00, "lowStockThreshold": 2.00, "lowStock": false }
```
`404 Not Found` if the id does not exist.

### List low-stock ingredients — `GET /api/ingredients/low-stock`
```bash
curl.exe http://localhost:9091/api/ingredients/low-stock
```
`200 OK` — ingredients where `currentStock <= lowStockThreshold`.

### Restock an ingredient — `POST /api/ingredients/{id}/restock`
Quantity may be in any unit within the same family; it is converted to the ingredient's stock unit.
```bash
curl.exe -X POST http://localhost:9091/api/ingredients/1/restock \
  -H "Content-Type: application/json" \
  -d '{ "quantity": 5000, "unit": "GRAM", "note": "weekly delivery" }'
```
`200 OK` — Flour goes from `10.00` to `15.00` KG (5000 g = 5 kg).

---

## Menu items (dishes)

### Create a dish + recipe — `POST /api/menu-items`
```bash
curl.exe -X POST http://localhost:9091/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Margherita Pizza",
        "sellingPrice": 350,
        "recipe": [
          { "ingredientId": 1, "quantity": 250, "unit": "GRAM" },
          { "ingredientId": 3, "quantity": 150, "unit": "GRAM" },
          { "ingredientId": 4, "quantity": 120, "unit": "GRAM" },
          { "ingredientId": 5, "quantity": 20,  "unit": "MILLILITRE" }
        ]
      }'
```
`201 Created`
```json
{ "id": 1, "name": "Margherita Pizza", "sellingPrice": 350,
  "recipe": [
    { "ingredientId": 1, "ingredientName": "Flour", "quantity": 250, "unit": "GRAM" },
    { "ingredientId": 3, "ingredientName": "Tomato", "quantity": 150, "unit": "GRAM" },
    { "ingredientId": 4, "ingredientName": "Cheese", "quantity": 120, "unit": "GRAM" },
    { "ingredientId": 5, "ingredientName": "Olive Oil", "quantity": 20, "unit": "MILLILITRE" }
  ] }
```
`404` if any `ingredientId` is unknown; `400` if a recipe unit can't convert to the ingredient's stock unit.

### List dishes — `GET /api/menu-items`
```bash
curl.exe http://localhost:9091/api/menu-items
```
`200 OK` — array of dishes with their recipes.

### Get one dish — `GET /api/menu-items/{id}`
```bash
curl.exe http://localhost:9091/api/menu-items/1
```
`200 OK` / `404 Not Found`.

### Sell a dish (deplete stock) — `POST /api/menu-items/sell`
```bash
curl.exe -X POST http://localhost:9091/api/menu-items/sell \
  -H "Content-Type: application/json" \
  -d '{ "menuItemId": 1, "quantity": 3 }'
```
`204 No Content` — deducts the recipe × quantity from stock atomically.
`409 Conflict` if any ingredient is short (nothing is deducted):
```json
{ "error": "Conflict", "status": 409,
  "message": "Insufficient stock for Flour: need 25000.000000 KILOGRAM, have 9.25" }
```

---

## Wastage

### Log wastage — `POST /api/wastage`
```bash
curl.exe -X POST http://localhost:9091/api/wastage \
  -H "Content-Type: application/json" \
  -d '{ "ingredientId": 4, "quantity": 200, "unit": "GRAM", "reason": "spoiled" }'
```
`204 No Content` — records a WASTE movement and reduces stock (200 g cheese → −0.2 KG).

### Daily wastage report — `GET /api/wastage/report`
Defaults to today; pass `?date=YYYY-MM-DD` for a specific day.
```bash
curl.exe http://localhost:9091/api/wastage/report
curl.exe "http://localhost:9091/api/wastage/report?date=2026-08-31"
```
`200 OK`
```json
{ "date": "2026-08-31", "totalWastageCost": 80.0000,
  "items": [
    { "ingredientName": "Cheese", "quantity": 0.20, "unit": "KILOGRAM", "cost": 80.0000, "reason": "spoiled" }
  ] }
```

---

## Reports

### Cost-per-dish and margin — `GET /api/reports/dish-cost/{menuItemId}`
```bash
curl.exe http://localhost:9091/api/reports/dish-cost/1
```
`200 OK`
```json
{ "menuItemId": 1, "name": "Margherita Pizza", "sellingPrice": 350.00,
  "ingredientCost": 72.50000000, "margin": 277.50000000 }
```
`404 Not Found` if the menu item does not exist.

---

## Error shape

All errors share this JSON structure (from the global exception handler):
```json
{ "error": "Bad Request", "status": 400,
  "message": "name: must not be blank; costPerUnit: must not be null",
  "timestamp": "2026-08-31T13:44:31.544Z" }
```

| Status | When |
|--------|------|
| `400 Bad Request` | Validation failure (missing/invalid fields) |
| `404 Not Found` | Referenced ingredient or menu item does not exist |
| `409 Conflict`   | Business rule violated (e.g. insufficient stock, concurrent sale) |

---

## PowerShell equivalents

If you prefer native PowerShell instead of `curl.exe`:
```powershell
# GET
Invoke-RestMethod http://localhost:9091/api/ingredients

# POST with a JSON body
$body = '{ "menuItemId": 1, "quantity": 3 }'
Invoke-RestMethod http://localhost:9091/api/menu-items/sell `
  -Method Post -ContentType 'application/json' -Body $body
```
