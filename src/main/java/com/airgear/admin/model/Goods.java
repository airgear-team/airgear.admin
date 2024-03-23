package com.airgear.admin.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_modified")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OffsetDateTime lastModified;

    @Column(name = "deleted_at")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OffsetDateTime deletedAt;
}
