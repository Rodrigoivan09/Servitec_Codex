package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "servicios")
@Entity
@Table(name = "Categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer id;

    @Column(name = "nombre_categoria", nullable = false, unique = true, length = 100)
    private String nombreCategoria;

    // Relación: Categoría ↔ Servicios
    @OneToMany(mappedBy = "categoria", fetch = FetchType.LAZY)
    private List<Servicio> servicios;

    // Relación: Categoría ↔ Técnicos
    @ManyToMany(mappedBy = "categorias")
    private List<Tecnico> tecnicos;


}
