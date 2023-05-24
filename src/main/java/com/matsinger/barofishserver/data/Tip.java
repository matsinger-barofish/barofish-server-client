package com.matsinger.barofishserver.data;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tip", schema = "barofish_dev", catalog = "")
public class Tip {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    @Basic
    @Column(name = "description", nullable = false, length = 200)
    private String description;
    @Basic
    @Column(name = "image", nullable = false, length = -1)
    private String image;
    @Basic
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tip tipEntity = (Tip) o;
        return id == tipEntity.id &&
                Objects.equals(title, tipEntity.title) &&
                Objects.equals(description, tipEntity.description) &&
                Objects.equals(image, tipEntity.image) &&
                Objects.equals(createdAt, tipEntity.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, image, createdAt);
    }
}
