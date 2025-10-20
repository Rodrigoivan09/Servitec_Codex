package edu.unam.springsecurity.controller;

import edu.unam.springsecurity.enums.EstadoSolicitud;
import edu.unam.springsecurity.enums.MotivoDeclinacion;
import edu.unam.springsecurity.model.Solicitud;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.model.TecnicoDisponibilidad;
import edu.unam.springsecurity.repository.SolicitudRepository;
import edu.unam.springsecurity.repository.TecnicoRepository;
import edu.unam.springsecurity.service.SolicitudService;
import edu.unam.springsecurity.service.TecnicoService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private static final Logger log = LoggerFactory.getLogger(TecnicoController.class);

    private final TecnicoRepository tecnicoRepository;
    private final SolicitudRepository solicitudRepository;
    private final SolicitudService solicitudService;
    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoRepository tecnicoRepository,
                             SolicitudRepository solicitudRepository,
                             SolicitudService solicitudService,
                             TecnicoService tecnicoService) {
        this.tecnicoRepository = tecnicoRepository;
        this.solicitudRepository = solicitudRepository;
        this.solicitudService = solicitudService;
        this.tecnicoService = tecnicoService;
    }

    private Tecnico currentTecnico(Authentication auth) {
        if (auth == null) return null;
        String username = auth.getName();
        Tecnico t = tecnicoRepository.findByCorreo(username);
        if (t == null && username.matches("\\d{10}")) {
            t = tecnicoRepository.findByTelefono(username);
        }
        if (t == null) {
            // También soportar username semántico (p.ej. 'tecnico') si está guardado en correo
            t = tecnicoRepository.findByCorreo(username);
        }
        return t;
    }

    @GetMapping("")
    public String dashboard(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);

        List<Solicitud> mias = tecnico == null ? Collections.emptyList() : solicitudRepository.findByTecnicoId(tecnico.getId());
        Map<EstadoSolicitud, Long> porEstado = mias.stream()
                .collect(Collectors.groupingBy(Solicitud::getEstado, Collectors.counting()));
        long pendientes = porEstado.getOrDefault(EstadoSolicitud.PENDIENTE, 0L);
        long aceptadas = porEstado.getOrDefault(EstadoSolicitud.ACEPTADA, 0L);
        long enProceso = porEstado.getOrDefault(EstadoSolicitud.EN_PROCESO, 0L);
        long completadas = porEstado.getOrDefault(EstadoSolicitud.COMPLETADA, 0L);
        long declinadas = porEstado.getOrDefault(EstadoSolicitud.DECLINADA, 0L);

        model.addAttribute("totalSolicitudes", mias.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aceptadas", aceptadas);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("completadas", completadas);
        model.addAttribute("declinadas", declinadas);

        // Evaluaciones y servicios (EAGER desde entidad)
        model.addAttribute("evaluaciones", tecnico != null ? tecnico.getEvaluaciones() : Collections.emptyList());
        model.addAttribute("servicios", tecnico != null ? tecnico.getServicios() : Collections.emptyList());

        return "tecnico/dashboard";
    }

    @GetMapping("/solicitudes")
    public String solicitudes(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            return "redirect:/login";
        }
        List<Solicitud> pendientes = solicitudService.buscarPendientesPorTecnico(tecnico.getId());
        List<Solicitud> agenda = solicitudService.buscarAgendaPorTecnico(tecnico.getId());

        model.addAttribute("tecnico", tecnico);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("agenda", agenda);
        model.addAttribute("motivosDeclinacion", MotivoDeclinacion.values());
        model.addAttribute("disponibilidades", tecnicoService.listarDisponibilidades(tecnico.getId()));
        return "tecnico/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Integer id,
                                   @RequestParam(value = "contactoConfirmado", defaultValue = "false") boolean contactoConfirmado,
                                   @RequestParam(value = "observaciones", required = false) String observaciones,
                                   Authentication auth,
                                   RedirectAttributes redirectAttributes) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el técnico autenticado.");
            return "redirect:/login";
        }
        try {
            solicitudService.aceptarSolicitud(id, tecnico.getId(), contactoConfirmado, observaciones);
            redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud aceptada correctamente.");
        } catch (Exception ex) {
            log.warn("No fue posible aceptar la solicitud {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/declinar")
    public String declinarSolicitud(@PathVariable Integer id,
                                    @RequestParam("motivo") MotivoDeclinacion motivo,
                                    @RequestParam(value = "detalle", required = false) String detalle,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró el técnico autenticado.");
            return "redirect:/login";
        }
        try {
            solicitudService.declinarSolicitud(id, tecnico.getId(), motivo, detalle);
            redirectAttributes.addFlashAttribute("mensajeInfo", "Solicitud declinada y motivo registrado.");
        } catch (Exception ex) {
            log.warn("No fue posible declinar la solicitud {}: {}", id, ex.getMessage());
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/disponibilidad/ahora")
    public String actualizarDisponibilidadAhora(@RequestParam("disponible") boolean disponible,
                                                @RequestParam(value = "notas", required = false) String notas,
                                                Authentication auth,
                                                RedirectAttributes redirectAttributes) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            return "redirect:/login";
        }
        tecnicoService.actualizarDisponibilidadInmediata(tecnico.getId(), disponible, notas);
        redirectAttributes.addFlashAttribute("mensajeExito", "Disponibilidad actualizada correctamente.");
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/disponibilidad")
    public String registrarDisponibilidad(@RequestParam("dia") DayOfWeek dia,
                                          @RequestParam("horaInicio") String horaInicio,
                                          @RequestParam("horaFin") String horaFin,
                                          Authentication auth,
                                          RedirectAttributes redirectAttributes) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            return "redirect:/login";
        }
        try {
            LocalTime inicio = LocalTime.parse(horaInicio);
            LocalTime fin = LocalTime.parse(horaFin);
            tecnicoService.registrarDisponibilidad(tecnico.getId(), dia, inicio, fin);
            redirectAttributes.addFlashAttribute("mensajeExito", "Horario agregado correctamente.");
        } catch (Exception ex) {
            log.warn("Error al registrar disponibilidad: {}", ex.getMessage());
            redirectAttributes.addFlashAttribute("error", "No se pudo registrar la disponibilidad: " + ex.getMessage());
        }
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/disponibilidad/{id}/estado")
    public String actualizarEstadoDisponibilidad(@PathVariable Integer id,
                                                 @RequestParam("activo") boolean activo,
                                                 Authentication auth,
                                                 RedirectAttributes redirectAttributes) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            return "redirect:/login";
        }
        tecnicoService.actualizarEstadoDisponibilidad(id, tecnico.getId(), activo);
        redirectAttributes.addFlashAttribute("mensajeInfo", "Disponibilidad actualizada.");
        return "redirect:/tecnico/solicitudes";
    }

    @PostMapping("/disponibilidad/{id}/eliminar")
    public String eliminarDisponibilidad(@PathVariable Integer id,
                                         Authentication auth,
                                         RedirectAttributes redirectAttributes) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            return "redirect:/login";
        }
        tecnicoService.eliminarDisponibilidad(id, tecnico.getId());
        redirectAttributes.addFlashAttribute("mensajeInfo", "Disponibilidad eliminada.");
        return "redirect:/tecnico/solicitudes";
    }

    @GetMapping("/evaluaciones")
    public String evaluaciones(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("evaluaciones", tecnico != null ? tecnico.getEvaluaciones() : Collections.emptyList());
        return "tecnico/evaluaciones";
    }

    @GetMapping("/servicios")
    public String servicios(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("servicios", tecnico != null ? tecnico.getServicios() : Collections.emptyList());
        return "tecnico/servicios";
    }

    @GetMapping("/perfil")
    public String perfil(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);
        if (tecnico != null) {
            model.addAttribute("disponibilidades", tecnicoService.listarDisponibilidades(tecnico.getId()));
        } else {
            model.addAttribute("disponibilidades", Collections.emptyList());
        }
        return "tecnico/perfil";
    }

    private static final String RUTA_FOTO = "uploads/foto_perfil";

    @PostMapping("/foto")
    public String subirFoto(Authentication auth,
                            @RequestParam("foto") MultipartFile foto,
                            Model model) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null) {
            return "redirect:/tecnico/perfil";
        }
        if (foto == null || foto.isEmpty()) {
            model.addAttribute("errorFoto", "Debes seleccionar una imagen válida.");
            return "tecnico/perfil";
        }
        try {
            String ext = edu.unam.springsecurity.util.Archivos.obtenerExtension(foto.getOriginalFilename());
            if (!StringUtils.hasText(ext)) {
                model.addAttribute("errorFoto", "El archivo debe contener una extensión válida.");
                return "tecnico/perfil";
            }
            String nombre = tecnico.getId() + "_" + System.currentTimeMillis() + "." + ext;
            String nombreGuardado = edu.unam.springsecurity.util.Archivos.almacenarConNombre(foto, RUTA_FOTO, nombre);
            tecnico.setRutaFotoPerfil("/" + RUTA_FOTO + "/" + nombreGuardado);
            tecnicoRepository.save(tecnico);
            model.addAttribute("mensajeFoto", "Foto actualizada correctamente.");
        } catch (RuntimeException e) {
            log.error("Error al almacenar la foto de perfil para el técnico {}: {}", tecnico.getId(), e.getMessage(), e);
            model.addAttribute("errorFoto", "No se pudo guardar la foto. Intenta nuevamente.");
            return "tecnico/perfil";
        }
        return "redirect:/tecnico/perfil";
    }

    @GetMapping(value = "/foto", produces = MediaType.ALL_VALUE)
    public ResponseEntity<Resource> verFoto(Authentication auth) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null || tecnico.getRutaFotoPerfil() == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            String ruta = tecnico.getRutaFotoPerfil(); // e.g. /uploads/foto_perfil/...
            java.nio.file.Path p = java.nio.file.Paths.get(ruta.startsWith("/") ? ruta.substring(1) : ruta);
            if (!java.nio.file.Files.exists(p)) {
                // intentar relativo a workspace
                p = java.nio.file.Paths.get("." + ruta);
            }
            byte[] bytes = java.nio.file.Files.readAllBytes(p);
            String ctype = java.nio.file.Files.probeContentType(p);
            if (ctype == null) ctype = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + p.getFileName())
                    .contentType(MediaType.parseMediaType(ctype))
                    .body(new ByteArrayResource(bytes));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
