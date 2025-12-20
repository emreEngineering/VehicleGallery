package com.vehiclegallery.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "dealers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Dealer extends Personnel {

    private String companyName;

    @Column(unique = true)
    private String taxNumber;

    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL)
    private List<Listing> listings;
}
