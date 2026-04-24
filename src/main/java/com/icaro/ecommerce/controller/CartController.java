package com.icaro.ecommerce.controller;

import com.icaro.ecommerce.dto.CartItemRequestDTO;
import com.icaro.ecommerce.dto.CartResponseDTO;
import com.icaro.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET /api/cart?sessionId=abc123
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@RequestParam String sessionId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(sessionId));
    }

    // POST /api/cart?sessionId=abc123
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addItem(@RequestParam String sessionId,
                                                   @Valid @RequestBody CartItemRequestDTO dto) {
        return ResponseEntity.ok(cartService.addItem(sessionId, dto));
    }

    // PUT /api/cart?sessionId=abc123
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateItem(@RequestParam String sessionId,
                                                      @PathVariable Long itemId,
                                                      @Valid @RequestBody CartItemRequestDTO dto) {
        return ResponseEntity.ok(cartService.updateItem(sessionId, itemId, dto));
    }

    // DELETE /api/cart/items/{itemId}?sessionId=abc123
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDTO> deleteItem(@RequestParam String sessionId,
                                                      @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(sessionId, itemId));
    }

    // DELETE /api/cart?sessionId=abc123
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam String sessionId) {
        cartService.clearCart(sessionId);
        return ResponseEntity.noContent().build();
    }
}
