package inclub9.entity;

import inclub9.annotation.FieldLabel;

public class Product {
    private Long id;

    @FieldLabel("product name")
    private String name;

    @FieldLabel("price")
    private String price;
}