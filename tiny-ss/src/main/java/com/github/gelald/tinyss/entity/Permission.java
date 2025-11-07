package com.github.gelald.tinyss.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@ToString(exclude = {"roles"})
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "resource_url")
    private String resourceUrl;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_enabled")
    private Boolean enabled = true;

    public Permission() {}

    public Permission(String name, String description, String code) {
        this.name = name;
        this.description = description;
        this.code = code;
    }

    public Permission(String name, String description, String code, String resourceType, String resourceUrl) {
        this.name = name;
        this.description = description;
        this.code = code;
        this.resourceType = resourceType;
        this.resourceUrl = resourceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}