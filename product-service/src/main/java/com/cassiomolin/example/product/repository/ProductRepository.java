package com.cassiomolin.example.product.repository;

import com.cassiomolin.example.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link Product}.
 *
 * @author cassiomolin
 */
public interface ProductRepository extends MongoRepository<Product, String> {

}
