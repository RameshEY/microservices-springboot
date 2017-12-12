package com.cassiomolin.example.shoppinglist.service;

import com.cassiomolin.example.shoppinglist.model.Product;
import com.cassiomolin.example.shoppinglist.model.ShoppingList;
import com.cassiomolin.example.shoppinglist.repository.ShoppingListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service that provides operations for shopping lists.
 *
 * @author cassiomolin
 */
@Service
public class ShoppingListService {

    @Autowired
    private ProductApiClient productApiClient;

    @Autowired
    private ShoppingListRepository shoppingListRepository;

    public String createShoppingList(ShoppingList shoppingList) {

        shoppingList.getItems().forEach(product -> {
            if (!productApiClient.checkIfProductExists(product.getId())) {
                throw new ProductNotFoundException(String.format("Product not found with id %s", product.getId()));
            }
        });

        shoppingList = shoppingListRepository.save(shoppingList);
        return shoppingList.getId();
    }

    public List<ShoppingList> findShoppingLists() {

        List<ShoppingList> shoppingLists = shoppingListRepository.findAll();
        shoppingLists.forEach(shoppingList -> {
            shoppingList.getItems().forEach(item -> {
                Optional<Product> optionalProduct = productApiClient.getProduct(item.getId());
                optionalProduct.ifPresent(product -> item.setName(product.getName()));
            });
        });

        return shoppingLists;
    }

    @StreamListener(ProductDeletedInput.PRODUCT_DELETED_INPUT)
    public void handleDeletedProduct(Product product) {
        shoppingListRepository.deleteProductsById(product.getId());
    }
}