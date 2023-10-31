package com.matsinger.barofishserver.domain.data.topbar.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "top_bar", schema = "barofish_dev", catalog = "")
public class TopBar {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @OneToMany(mappedBy = "topBar", cascade = CascadeType.ALL)
    private List<TopBarProductMap> curationProductMaps = new ArrayList<>();
    public void setName(String name) {
        this.name = name;
    }
}
