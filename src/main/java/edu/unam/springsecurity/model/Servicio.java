package edu.unam.springsecurity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "categoria")
@Entity
@Table(name = "Servicios")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    private Integer id;

    @Column(name = "nombre_servicio", nullable = false, unique = true, length = 100)
    private String nombreServicio;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // Relación: muchos servicios pertenecen a una categoría
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToOne(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Tarifa tarifa;

}
