package edu.unam.springsecurity.model;

import edu.unam.springsecurity.enums.EstadoSolicitud;
import edu.unam.springsecurity.enums.MotivoDeclinacion;
import edu.unam.springsecurity.enums.TipoAtencion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.ToString(exclude = {"adjuntos", "pago"})
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_tecnico", nullable = false)
    private Tecnico tecnico;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(name = "fecha", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "hora_llegada")
    private LocalTime horaProgramada;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atencion", nullable = false)
    private TipoAtencion tipoAtencion = TipoAtencion.INMEDIATA;

    @Column(name = "detalles_adicionales", length = 500)
    private String detallesAdicionales;

    @Column(name = "requiere_videollamada")
    private Boolean requiereVideollamada = Boolean.FALSE;

    @Column(name = "contacto_confirmado")
    private Boolean contactoConfirmado = Boolean.FALSE;

    @Column(name = "observaciones_tecnico", length = 500)
    private String observacionesTecnico;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_declinar")
    private MotivoDeclinacion motivoDeclinacion;

    @Column(name = "motivo_declinar_detalle", length = 500)
    private String motivoDeclinacionDetalle;

    @Column(name = "fecha_decision")
    private LocalDateTime fechaDecision;

    @Column(name = "fecha_limite_respuesta")
    private LocalDateTime fechaLimiteRespuesta;

    @OneToOne(mappedBy = "solicitud", cascade = CascadeType.ALL)
    private PagoSimulado pago;

    @Column(nullable = false)
    private String direccion;

    @OneToMany(
            mappedBy = "solicitud",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<SolicitudAdjunto> adjuntos = new ArrayList<>();
}
