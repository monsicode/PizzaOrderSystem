package com.deliciouspizza.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.deliciouspizza.entity.product.Product;
import com.deliciouspizza.utils.StatusOrder;

import java.util.Map;

public class ProductRepository {

    private static final String FILE_PATH = "src/com/deliciouspizza/files/product.json";

    Map<Product, StatusOrder> active;
    Map<Product, StatusOrder> inactive;
    private ObjectMapper objectMapper;




}

// Product.json
// ---------------
//Margarita | ACTIVE
//Cola | Active
// ...