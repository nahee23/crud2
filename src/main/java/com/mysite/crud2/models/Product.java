package com.mysite.crud2.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String brand;
    private String category;
    private int price;

    @Column(columnDefinition = "TEXT")
    private String description;
    @CreationTimestamp
    private Date createAt; //자동 날짜 입력
    private String imageFileName; //이미지 파일명

}
