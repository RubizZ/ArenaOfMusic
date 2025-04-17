package es.ucm.fdi.iw.model;


import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "object")
public class Object implements Transferable<Object.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String objectType;

    @OneToMany(mappedBy = "object")
    private Set<Inventory> inventarios;



    @Getter
    @AllArgsConstructor
    public class Transfer {
        private long id;
        private boolean active;
        private String name;
        private int price;
        private String objectType;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, active, name, price, objectType);
    }
    
    
}
