package edu.unam.springsecurity.controller;

import edu.unam.springsecurity.model.Solicitud;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.repository.SolicitudRepository;
import edu.unam.springsecurity.repository.TecnicoRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    private final TecnicoRepository tecnicoRepository;
    private final SolicitudRepository solicitudRepository;

    public TecnicoController(TecnicoRepository tecnicoRepository, SolicitudRepository solicitudRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.solicitudRepository = solicitudRepository;
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

        List<Solicitud> mias = tecnico == null ? List.of() : solicitudRepository.findByTecnicoId(tecnico.getId());
        Map<String, Long> porEstado = mias.stream().collect(Collectors.groupingBy(Solicitud::getEstado, Collectors.counting()));
        long pendientes = porEstado.getOrDefault("Pendiente", 0L);
        long enCurso = porEstado.getOrDefault("En Curso", 0L) + porEstado.getOrDefault("EnCurso", 0L);
        long completadas = porEstado.getOrDefault("Completado", 0L) + porEstado.getOrDefault("Completada", 0L);

        model.addAttribute("totalSolicitudes", mias.size());
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enCurso", enCurso);
        model.addAttribute("completadas", completadas);

        // Evaluaciones y servicios (EAGER desde entidad)
        model.addAttribute("evaluaciones", tecnico != null ? tecnico.getEvaluaciones() : List.of());
        model.addAttribute("servicios", tecnico != null ? tecnico.getServicios() : List.of());

        return "tecnico/dashboard";
    }

    @GetMapping("/solicitudes")
    public String solicitudes(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        List<Solicitud> mias = tecnico == null ? List.of() : solicitudRepository.findByTecnicoId(tecnico.getId());
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("solicitudes", mias);
        return "tecnico/solicitudes";
    }

    @GetMapping("/evaluaciones")
    public String evaluaciones(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("evaluaciones", tecnico != null ? tecnico.getEvaluaciones() : List.of());
        return "tecnico/evaluaciones";
    }

    @GetMapping("/servicios")
    public String servicios(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);
        model.addAttribute("servicios", tecnico != null ? tecnico.getServicios() : List.of());
        return "tecnico/servicios";
    }

    @GetMapping("/perfil")
    public String perfil(Authentication auth, Model model) {
        Tecnico tecnico = currentTecnico(auth);
        model.addAttribute("tecnico", tecnico);
        return "tecnico/perfil";
    }

    private static final String RUTA_FOTO = "uploads/foto_perfil";

    @PostMapping("/foto")
    public String subirFoto(Authentication auth,
                            @RequestParam("foto") MultipartFile foto,
                            Model model) {
        Tecnico tecnico = currentTecnico(auth);
        if (tecnico == null || foto == null || foto.isEmpty()) {
            return "redirect:/tecnico/perfil";
        }
        try {
            String ext = edu.unam.springsecurity.util.Archivos.obtenerExtension(foto.getOriginalFilename());
            String nombre = tecnico.getId() + "_" + System.currentTimeMillis() + "." + ext;
            String nombreGuardado = edu.unam.springsecurity.util.Archivos.almacenarConNombre(foto, RUTA_FOTO, nombre);
            if (nombreGuardado != null) {
                tecnico.setRutaFotoPerfil("/" + RUTA_FOTO + "/" + nombreGuardado);
                tecnicoRepository.save(tecnico);
            }
        } catch (Exception e) {
            // Log simple; se puede mejorar con un logger
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
