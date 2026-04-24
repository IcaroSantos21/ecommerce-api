package com.icaro.ecommerce.service;

import com.icaro.ecommerce.dto.CartItemRequestDTO;
import com.icaro.ecommerce.dto.CartItemResponseDTO;
import com.icaro.ecommerce.dto.CartResponseDTO;
import com.icaro.ecommerce.model.Cart;
import com.icaro.ecommerce.model.CartItem;
import com.icaro.ecommerce.model.Product;
import com.icaro.ecommerce.repository.CartItemRepository;
import com.icaro.ecommerce.repository.CartRepository;
import com.icaro.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponseDTO getOrCreateCart(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    return cartRepository.save(newCart);
                });
        return toResponseDTO(cart);
    }

    public CartResponseDTO addItem(String sessionId, CartItemRequestDTO dto) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setSessionId(sessionId);
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produto com o Id: " + dto.getProductId() + "Não existe"));

        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Estoque do produto: " + product.getName() + " é insuficiente");
        }

        cartItemRepository.findByCartAndProduct(cart, product)
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + dto.getQuantity()),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setProduct(product);
                            newItem.setQuantity(dto.getQuantity());
                            cart.getItems().add(newItem);
                        }
                );
        return toResponseDTO(cartRepository.save(cart));
    }

    public CartResponseDTO updateItem(String sessionId, Long itemId, CartItemRequestDTO dto) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com a sessão: " + sessionId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com o ID: " + itemId));

        if (item.getProduct().getStock() < dto.getQuantity()) {
            throw new RuntimeException("O estoque do produto: " + item.getProduct().getName() + " é insuficiente");
        }
        item.setQuantity(dto.getQuantity());
        cartItemRepository.save(item);

        return toResponseDTO(cartRepository.findById(cart.getId()).get());
    }

    public CartResponseDTO removeItem(String sessionId, Long itemId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com a sessão: " + sessionId));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("item com o id: " + itemId + " não foi encontrado"));

        cart.getItems().remove(item);
        return toResponseDTO(cartRepository.save(cart));
    }

    public void clearCart(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado com a sessão: " + sessionId));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartResponseDTO toResponseDTO(Cart cart) {
        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setSessionId(cart.getSessionId());

        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> {
                    CartItemResponseDTO itemDTO = new CartItemResponseDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductId(item.getProduct().getId());
                    itemDTO.setProductName(item.getProduct().getName());
                    itemDTO.setProductImageUrl(item.getProduct().getImageUrl());
                    itemDTO.setUnitPrice(item.getProduct().getPrice());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setSubtotal(item.getProduct().getPrice()
                                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setItems(items);
        dto.setTotal(items.stream()
                    .map(CartItemResponseDTO::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        return dto;
    }
}
