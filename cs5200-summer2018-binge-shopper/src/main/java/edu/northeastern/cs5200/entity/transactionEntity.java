package edu.northeastern.cs5200.entity;

import javax.persistence.*;
import java.util.Date;

public class transactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "date")
    private Date date;
    @Column(name = "productId")
    private int productId;
    @Column(name = "sellerId")
    private int sellerId;
    @Column(name = "orderId")
    private int orderId;
}