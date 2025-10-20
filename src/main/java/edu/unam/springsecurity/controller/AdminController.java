package edu.unam.springsecurity.controller;


import edu.unam.springsecurity.dto.EvaluacionDTO;
import edu.unam.springsecurity.dto.ServicioDTO;
import edu.unam.springsecurity.dto.TecnicoDTO;
import edu.unam.springsecurity.dto.UsuarioDTO;
import edu.unam.springsecurity.model.*;
import edu.unam.springsecurity.repository.*;
import edu.unam.springsecurity.service.TecnicoService;
import edu.unam.springsecurity.service.UserService;
import edu.unam.springsecurity.util.Archivos;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static edu.unam.springsecurity.util.Archivos.obtenerExtension;

@Controller
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

//    private final HomeService homeService;
      private final UserService userService;
//    private final AdminService adminService;
//    private final TecnicoService tecnicoService;
//
//    // Controller Injection
//    public AdminController(HomeService homeService, UserService userService, AdminService adminService,TecnicoService tecnicoService) {
//        this.homeService = homeService;
//        this.userService = userService;
//        this.adminService = adminService;
//        this.tecnicoService= tecnicoService;
//    }

    private final UsuarioRepository usuarioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final TarifaRepository tarifaRepository;
    private final EvaluacionRepository evaluacionRepository;
//    private final TecnicoService tecnicoService;

    public AdminController(UsuarioRepository usuarioRepository, UserService userService, TecnicoService tecnicoService, TecnicoRepository tecnicoRepository, ServicioRepository servicioRepository, CategoriaRepository categoriaRepository, TarifaRepository tarifaRepository, EvaluacionRepository evaluacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.userService = userService;
        this.tecnicoRepository = tecnicoRepository;
//        this.tecnicoService = tecnicoService;
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.tarifaRepository = tarifaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("titulo", "Panel de Administración");
        return "plantilla-admin";
        //return "admin";
    }



    // Listar técnicos
    @GetMapping("/admin/tecnicos")
    public String listarTecnicos(Model model) {
        List<Tecnico> tecnicos = tecnicoRepository.findAll();
        List<TecnicoDTO> tecnicoDTOs = tecnicos.stream()
                .map(t -> new TecnicoDTO(
                        t.getId(),
                        t.getNombre(),
                        t.getCorreo(),
                        t.getTelefono(),
                        t.getEvaluaciones() != null && !t.getEvaluaciones().isEmpty() ?
                                t.getEvaluaciones().stream()
                                        .mapToDouble(Evaluacion::getCalificacion)
                                        .average()
                                        .orElse(0.0) : 0.0,
                        t.getEvaluaciones() != null ?
                                t.getEvaluaciones().stream()
                                        .map(e -> new EvaluacionDTO(e.getId(), e.getComentarios(), e.getCalificacion()))
                                        .collect(Collectors.toList())
                                : Collections.emptyList(),
                        t.getServicios() != null ?
                                t.getServicios().stream()
                                        .map(s -> new ServicioDTO(s.getId(), s.getNombreServicio(), s.getDescripcion(), s.getTarifa().getTarifaBase()))
                                        .collect(Collectors.toList())
                                : Collections.emptyList(),
                        t.getEstado()
                ))
                .collect(Collectors.toList());

        model.addAttribute("tecnicos", tecnicoDTOs);
        model.addAttribute("tecnico", new Tecnico());
        return "admin/tecnicos";
    }


//    @GetMapping("/admin/tecnicos/nuevo")
//    public String mostrarFormularioRegistro(Model model) {
//        model.addAttribute("tecnico", new Tecnico());
//        return "admin/tecnicos";
//    }
    @Autowired
    private TecnicoService tecnicoService;

    private final String RUTA_EFIRMA = "uploads/efirma";
    private final String RUTA_CERTIFICACION = "uploads/certificacion";

    @PostMapping("/admin/tecnicos/guardar")
    public String guardarTecnicoDesdeAdmin(@Valid @ModelAttribute("tecnico") Tecnico tecnico,
                                           BindingResult result,
                                           @RequestParam("efirma") MultipartFile efirma,
                                           @RequestParam("certificacion") MultipartFile certificacion,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Corrige los errores del formulario.");
            return "redirect:/admin/tecnicos";
        }

        if (tecnicoService.existePorCorreo(tecnico.getCorreo())) {
            redirectAttributes.addFlashAttribute("error", "El correo ya está registrado.");
            return "redirect:/admin/tecnicos";
        }

        if (efirma == null || efirma.isEmpty() || certificacion == null || certificacion.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debes adjuntar la eFirma y la certificación.");
            return "redirect:/admin/tecnicos";
        }

        try {
            // Guardar técnico sin rutas
            tecnico.setRutaEfirma("");
            tecnico.setRutaCertificacion("");
            tecnicoService.guardar(tecnico);  // Genera ID

            // Nombrar archivos
            String extensionEfirma = obtenerExtension(efirma.getOriginalFilename());
            String extensionCert = obtenerExtension(certificacion.getOriginalFilename());
            if (!StringUtils.hasText(extensionEfirma) || !StringUtils.hasText(extensionCert)) {
                redirectAttributes.addFlashAttribute("error", "Los archivos deben contar con una extensión válida.");
                tecnicoService.eliminar(tecnico.getId());
                return "redirect:/admin/tecnicos";
            }
            String nombreEfirma = tecnico.getId() + "_efirma." + extensionEfirma;
            String nombreCert = tecnico.getId() + "_certificacion." + extensionCert;

            // Guardar archivos
            Archivos.almacenarConNombre(efirma, RUTA_EFIRMA, nombreEfirma);
            Archivos.almacenarConNombre(certificacion, RUTA_CERTIFICACION, nombreCert);

            // Asignar rutas y guardar nuevamente
            tecnico.setRutaEfirma("/" + RUTA_EFIRMA + "/" + nombreEfirma);
            tecnico.setRutaCertificacion("/" + RUTA_CERTIFICACION + "/" + nombreCert);
            tecnicoService.guardar(tecnico);

            redirectAttributes.addFlashAttribute("mensajeExito", "Técnico registrado correctamente.");
        } catch (RuntimeException e) {
            log.error("Error al almacenar documentos para el técnico", e);
            if (tecnico.getId() != null) {
                tecnicoService.eliminar(tecnico.getId());
            }
            redirectAttributes.addFlashAttribute("error", "No fue posible guardar la documentación del técnico. Intenta nuevamente.");
        }

        return "redirect:/admin/tecnicos";
    }




    @GetMapping("/admin/tecnicos/editar/{id}")
    public String editarTecnico(@PathVariable("id") Integer id, Model model) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("tecnico", tecnico);
        return "admin/editar-tecnico";
    }



    @PostMapping("/admin/tecnicos/actualizar")
    public String actualizarTecnico(@ModelAttribute Tecnico tecnicoActualizado,
                                    @RequestParam("efirma") MultipartFile efirma,
                                    @RequestParam("certificacion") MultipartFile certificacion,
                                    RedirectAttributes redirectAttributes) {
        Tecnico existente = tecnicoRepository.findById(tecnicoActualizado.getId()).orElse(null);

        if (existente == null) {
            redirectAttributes.addFlashAttribute("error", "El técnico no existe.");
            return "redirect:/admin/tecnicos";
        }

        // Conservar contraseña si está vacía
        if (!StringUtils.hasText(tecnicoActualizado.getContrasena())) {
            tecnicoActualizado.setContrasena(existente.getContrasena());
        }

        // Conservar archivos si no se actualizan
        if (efirma == null || efirma.isEmpty()) {
            tecnicoActualizado.setRutaEfirma(existente.getRutaEfirma());
        } else {
            String extensionEfirma = obtenerExtension(efirma.getOriginalFilename());
            if (!StringUtils.hasText(extensionEfirma)) {
                redirectAttributes.addFlashAttribute("error", "La eFirma debe tener una extensión válida.");
                return "redirect:/admin/tecnicos";
            }
            String nombreEfirma = tecnicoActualizado.getId() + "_efirma." + extensionEfirma;
            Archivos.almacenarConNombre(efirma, "uploads/efirma", nombreEfirma);
            tecnicoActualizado.setRutaEfirma("/uploads/efirma/" + nombreEfirma);
        }

        if (certificacion == null || certificacion.isEmpty()) {
            tecnicoActualizado.setRutaCertificacion(existente.getRutaCertificacion());
        } else {
            String extensionCert = obtenerExtension(certificacion.getOriginalFilename());
            if (!StringUtils.hasText(extensionCert)) {
                redirectAttributes.addFlashAttribute("error", "La certificación debe tener una extensión válida.");
                return "redirect:/admin/tecnicos";
            }
            String nombreCert = tecnicoActualizado.getId() + "_certificacion." + extensionCert;
            Archivos.almacenarConNombre(certificacion, "uploads/certificacion", nombreCert);
            tecnicoActualizado.setRutaCertificacion("/uploads/certificacion/" + nombreCert);
        }

        tecnicoRepository.save(tecnicoActualizado);
        redirectAttributes.addFlashAttribute("mensajeExito", "Técnico actualizado correctamente.");
        return "redirect:/admin/tecnicos";
    }


    @GetMapping("/admin/tecnicos/eliminar/{id}")
    public String eliminarTecnico(@PathVariable("id") Integer id) {
        tecnicoRepository.deleteById(id);
        return "redirect:/admin/tecnicos";
    }





    //CRUD SERVICIO




    @GetMapping("/admin/servicios")
    public String listarServicios(Model model) {
        List<Servicio> servicios = servicioRepository.findAll();
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("servicios", servicios);
        model.addAttribute("categorias", categorias);
        model.addAttribute("servicio", new Servicio());
        return "admin/servicios";
    }

    @PostMapping("/admin/servicios/guardar")
    public String guardarServicio(@ModelAttribute Servicio servicio,
                                  @RequestParam("categoria.id") Integer categoriaId,
                                  @RequestParam("tarifa.tarifaBase") Double tarifaBase,
                                  RedirectAttributes redirectAttributes) {
        try {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            servicio.setCategoria(categoria);

            Tarifa tarifa = new Tarifa();
            tarifa.setTarifaBase(tarifaBase);
            tarifa.setServicio(servicio); // relación bidireccional
            servicio.setTarifa(tarifa);

            servicioRepository.save(servicio);
            redirectAttributes.addFlashAttribute("mensajeExito", "Servicio registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el servicio: " + e.getMessage());
        }

        return "redirect:/admin/servicios";
    }

    @GetMapping("/admin/servicios/editar/{id}")
    public String editarServicio(@PathVariable("id") Integer id, Model model) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de servicio inválido"));
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("servicio", servicio);
        model.addAttribute("categorias", categorias);
        return "admin/editar-servicio";
    }

    @PostMapping("/admin/servicios/actualizar")
    public String actualizarServicio(@ModelAttribute Servicio servicioActualizado,
                                     @RequestParam("categoria.id") Integer categoriaId,
                                     @RequestParam("tarifa.tarifaBase") Double tarifaBase,
                                     RedirectAttributes redirectAttributes) {
        Servicio existente = servicioRepository.findById(servicioActualizado.getId()).orElse(null);

        if (existente == null) {
            redirectAttributes.addFlashAttribute("error", "El servicio no existe.");
            return "redirect:/admin/servicios";
        }

        existente.setNombreServicio(servicioActualizado.getNombreServicio());
        existente.setDescripcion(servicioActualizado.getDescripcion());
        existente.setCategoria(categoriaRepository.findById(categoriaId).orElse(null));
        if (existente.getTarifa() == null) {
            Tarifa tarifa = new Tarifa();
            tarifa.setServicio(existente);
            tarifa.setTarifaBase(tarifaBase);
            existente.setTarifa(tarifa);
        } else {
            existente.getTarifa().setTarifaBase(tarifaBase);
        }

        servicioRepository.save(existente);
        redirectAttributes.addFlashAttribute("mensajeExito", "Servicio actualizado correctamente.");
        return "redirect:/admin/servicios";
    }

    @GetMapping("/admin/servicios/eliminar/{id}")
    public String eliminarServicio(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        servicioRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Servicio eliminado correctamente.");
        return "redirect:/admin/servicios";
    }





// USUARIOS CRUD

    @GetMapping("/admin/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll(); // o tu servicio
        List<UsuarioDTO> usuarioDTOs = usuarios.stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(), u.getDireccion()))
                .collect(Collectors.toList());

        model.addAttribute("usuarios", usuarioDTOs);
        return "admin/usuarios";
    }

    @PostMapping("/admin/usuarios/guardar")
    public String guardarUsuarioDesdeAdmin(@Valid @ModelAttribute Usuario usuario,
                                           BindingResult result,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el usuario.");
            return "redirect:/admin/usuarios";
        }

        if (userService.existePorCorreo(usuario.getCorreo())) {
            redirectAttributes.addFlashAttribute("error", "Este correo ya está registrado.");
            return "redirect:/admin/usuarios";
        }

        userService.guardar(usuario);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario agregado exitosamente.");
        return "redirect:/admin/usuarios";
    }


    @GetMapping("/admin/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("usuario", usuario);
        return "admin/editar-usuario";
    }

    @PostMapping("/admin/usuarios/actualizar")
    public String actualizarUsuario(@ModelAttribute Usuario usuarioActualizado, RedirectAttributes redirectAttributes) {
        Usuario existente = usuarioRepository.findById(usuarioActualizado.getId()).orElse(null);

        if (existente == null) {
            redirectAttributes.addFlashAttribute("error", "El usuario no existe.");
            return "redirect:/admin/usuarios";
        }

        // Si la contraseña se deja vacía, conserva la anterior
        if (!StringUtils.hasText(usuarioActualizado.getContrasena())) {
            usuarioActualizado.setContrasena(existente.getContrasena());
        }

        usuarioRepository.save(usuarioActualizado);
        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario actualizado correctamente.");
        return "redirect:/admin/usuarios";
    }


    @GetMapping("/admin/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin/usuarios";
    }

    //CRUD ESTADISTICAS


    @GetMapping("/admin/estadisticas")
    public String verEstadisticas(Model model) {
        // Puedes simular datos o conectar métricas reales en el futuro
        model.addAttribute("titulo", "Estadísticas del sistema");
        return "admin/estadisticas";
    }

}
