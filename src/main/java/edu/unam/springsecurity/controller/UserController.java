package edu.unam.springsecurity.controller;

import edu.unam.springsecurity.dto.EvaluacionDTO;
import edu.unam.springsecurity.dto.ServicioDTO;
import edu.unam.springsecurity.dto.SolicitudDTO;
import edu.unam.springsecurity.dto.TecnicoDTO;
import edu.unam.springsecurity.enums.EstadoSolicitud;
import edu.unam.springsecurity.model.*;
import edu.unam.springsecurity.repository.UsuarioRepository;
import edu.unam.springsecurity.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private TecnicoService tecnicoService;

    @GetMapping("/usuario")
    public String userDashboard(Model model) {
        model.addAttribute("titulo", "Servitec");
        return "usuario";
        //return "admin";
    }


    @GetMapping("/user/plomeros")
    public String mostrarTecnicosPlomeria(Model model) {
        String categoriaPlomeria = "Plomería"; // con tilde
        List<Tecnico> tecnicos = tecnicoService.obtenerTecnicosPorCategoria(categoriaPlomeria);

        List<TecnicoDTO> tecnicoDTOs = tecnicos.stream().map(tecnico -> {
            Double promedio = tecnico.getEvaluaciones() == null || tecnico.getEvaluaciones().isEmpty() ?
                    null :
                    tecnico.getEvaluaciones().stream()
                            .mapToInt(Evaluacion::getCalificacion)
                            .average().orElse(0.0);

            List<EvaluacionDTO> evaluacionesDTO = tecnico.getEvaluaciones().stream()
                    .map(e -> new EvaluacionDTO(
                            e.getCalificacion(),
                            e.getComentarios(),
                            e.getFechaEvaluacion()
                    )).collect(Collectors.toList());

            List<ServicioDTO> serviciosDTO = tecnico.getServicios().stream()
                    .map(servicio -> new ServicioDTO(
                            servicio.getId(),
                            servicio.getNombreServicio(),
                            servicio.getDescripcion(),
                            servicio.getTarifa().getTarifaBase()
                    )).collect(Collectors.toList());

            return new TecnicoDTO(
                    tecnico.getId(),
                    tecnico.getNombre(),
                    tecnico.getCorreo(),
                    tecnico.getTelefono(),
                    promedio,
                    evaluacionesDTO,
                    serviciosDTO,
                    tecnico.getEstado()
            );
        }).collect(Collectors.toList());

        model.addAttribute("tecnicos", tecnicoDTOs);
        return "user/plomeros"; // Asegúrate de que este sea el nombre correcto del HTML
    }



    @GetMapping("/user/electricistas")
    public String mostrarTecnicosElectricistas(Model model) {
        // 1. Obtener todos los técnicos asociados a la categoría "Electricidad"
        List<Tecnico> tecnicos = tecnicoService.obtenerTecnicosPorCategoria("Electricidad");

        // 2. Convertir a DTO
        List<TecnicoDTO> tecnicoDTOs = tecnicos.stream().map(tecnico -> {
            double promedio = tecnico.getEvaluaciones().isEmpty() ? 0 :
                    tecnico.getEvaluaciones().stream()
                            .mapToInt(Evaluacion::getCalificacion)
                            .average().orElse(0.0);

            List<EvaluacionDTO> evaluacionesDTO = tecnico.getEvaluaciones().stream()
                    .map(e -> new EvaluacionDTO(
                            e.getCalificacion(),
                            e.getComentarios(),
                            e.getFechaEvaluacion()
                    ))
                    .collect(Collectors.toList());

            List<ServicioDTO> serviciosDTO = tecnico.getServicios().stream()
                    .map(servicio -> new ServicioDTO(
                            servicio.getId(),
                            servicio.getNombreServicio(),
                            servicio.getDescripcion(),
                            servicio.getTarifa().getTarifaBase()
                    )).collect(Collectors.toList());

            return new TecnicoDTO(
                    tecnico.getId(),
                    tecnico.getNombre(),
                    tecnico.getCorreo(),
                    tecnico.getTelefono(),
                    tecnico.getEvaluaciones().isEmpty() ? null : promedio,
                    evaluacionesDTO,
                    serviciosDTO,
                    tecnico.getEstado()
            );

        }).collect(Collectors.toList());

        // 3. Enviar a la vista
        model.addAttribute("tecnicos", tecnicoDTOs);
        return "user/electricistas";
    }


    @GetMapping("/user/electrodomesticos")
    public String mostrarTecnicosElectrodomesticos(Model model) {
        // 1. Obtener todos los técnicos asociados a la categoría "Electrodomésticos"
        List<Tecnico> tecnicos = tecnicoService.obtenerTecnicosPorCategoria("Reparación de Electrodomésticos");

        // 2. Convertir a DTO
        List<TecnicoDTO> tecnicoDTOs = tecnicos.stream().map(tecnico -> {
            double promedio = tecnico.getEvaluaciones().isEmpty() ? 0 :
                    tecnico.getEvaluaciones().stream()
                            .mapToInt(Evaluacion::getCalificacion)
                            .average().orElse(0.0);

            List<EvaluacionDTO> evaluacionesDTO = tecnico.getEvaluaciones().stream()
                    .map(e -> new EvaluacionDTO(
                            e.getCalificacion(),
                            e.getComentarios(),
                            e.getFechaEvaluacion()
                    ))
                    .collect(Collectors.toList());

            List<ServicioDTO> serviciosDTO = tecnico.getServicios().stream()
                    .map(servicio -> new ServicioDTO(
                            servicio.getId(),
                            servicio.getNombreServicio(),
                            servicio.getDescripcion(),
                            servicio.getTarifa().getTarifaBase()
                    ))
                    .collect(Collectors.toList());

            return new TecnicoDTO(
                    tecnico.getId(),
                    tecnico.getNombre(),
                    tecnico.getCorreo(),
                    tecnico.getTelefono(),
                    tecnico.getEvaluaciones().isEmpty() ? null : promedio,
                    evaluacionesDTO,
                    serviciosDTO,
                    tecnico.getEstado()
            );
        }).collect(Collectors.toList());

        // 3. Enviar a la vista
        model.addAttribute("tecnicos", tecnicoDTOs);
        return "user/electrodomesticos";
    }


    @Autowired
    private ServicioService servicioService;

    @GetMapping("/user/agendar/{idTecnico}/{idServicio}")
    public String mostrarFormularioAgendar(@PathVariable Integer idTecnico,
                                           @PathVariable Integer idServicio,
                                           HttpSession session,
                                           Model model) {


        Tecnico tecnico = tecnicoService.buscarPorId(idTecnico);
        Servicio servicio = servicioService.buscarPorId(idServicio);
        //Usuario usuario = (Usuario) session.getAttribute("usuarioActual");


        // También puedes verificar estos datos si quieres
        System.out.println("👷 Técnico seleccionado: " + tecnico.getNombre());
        System.out.println("🔧 Servicio seleccionado: " + servicio.getNombreServicio());

        model.addAttribute("tecnico", tecnico);
        model.addAttribute("servicio", servicio);
        //model.addAttribute("idUsuario", usuario.getId()); // 👈 esto es CLAVE

        return "user/agendar";
    }


    @Autowired
    private SolicitudService solicitudService;
    @Autowired
    private UserService userService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/user/guardarSolicitud")
    public String guardarSolicitud(@ModelAttribute SolicitudDTO solicitudDTO,
                                   RedirectAttributes redirectAttributes) {

        // Obtener técnico y servicio
        Tecnico tecnico = tecnicoService.buscarPorId(solicitudDTO.getIdTecnico());
        Servicio servicio = servicioService.buscarPorId(solicitudDTO.getIdServicio());

        // Extraer input (correo o teléfono) del usuario autenticado
        String input = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar el usuario autenticado
        Usuario usuario = userService.buscarPorCorreoOTelefono(input);
        if (usuario == null) {
            throw new IllegalStateException("Usuario no encontrado con: " + input);
        }

        // Crear solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setTecnico(tecnico);
        solicitud.setServicio(servicio);
        solicitud.setDireccion(solicitudDTO.getDireccion());
        solicitud.setHoraLlegada(solicitudDTO.getHoraLlegada());
        solicitud.setEstado(String.valueOf(EstadoSolicitud.PENDIENTE));
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setFecha(solicitud.getFechaSolicitud());
        solicitud.setUsuario(usuario);

        // Crear y asociar el pago
        PagoSimulado pago = new PagoSimulado();
        pago.setNumeroTarjeta(solicitudDTO.getNumeroTarjeta());
        LocalDate fechaVencimiento = LocalDate.parse(solicitudDTO.getFechaVencimiento() + "-01");
        pago.setFechaVencimiento(fechaVencimiento.toString());
        pago.setCvv(Integer.parseInt(solicitudDTO.getCvv()));
        pago.setMonto(BigDecimal.valueOf(servicio.getTarifa().getTarifaBase()));

        solicitud.setPago(pago);
        pago.setSolicitud(solicitud);

        solicitudService.guardar(solicitud);

        redirectAttributes.addFlashAttribute("mensajeExito", "¡Solicitud registrada con éxito!");
        return "user/solicitudExitosa";
    }



    @GetMapping("/user/solicitudes")
    public String mostrarSolicitudesUsuario(HttpSession session, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Puede ser correo o teléfono

        Usuario usuario = userService.buscarPorCorreoOTelefono(username);
        if (usuario == null) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        List<Solicitud> solicitudes = solicitudService.buscarPorUsuario(usuario.getId());
        model.addAttribute("solicitudes", solicitudes);
        return "user/solicitudes"; // El HTML que ya tienes
    }


    @Autowired
    private PagoSimuladoService pagoSimuladoService;

    @GetMapping("/user/pagos")
    public String mostrarPagosUsuario(Model model) {
        // Obtener correo del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName(); // Funciona para correo o teléfono (según login)

        // Buscar usuario por correo o teléfono
        Usuario usuario = userService.buscarPorCorreoOTelefono(correo);
        if (usuario == null) {
            throw new IllegalStateException("Usuario no autenticado correctamente.");
        }

        // Obtener pagos del usuario a través de sus solicitudes
        List<PagoSimulado> pagos = pagoSimuladoService.buscarPorUsuario(usuario.getId());

        model.addAttribute("pagos", pagos);
        return "user/pagos"; // Asegúrate de que coincida con tu ruta de template
    }




//    private final UserService userService;
//
//    public UserController(UsuarioRepository usuarioRepository, UserService userService) {
//        this.userService = userService;
//    }
//    @PostMapping("/usuarios/guardar")
//    public String guardarUsuarioDesdeAdmin(@Valid @ModelAttribute Usuario usuario,
//                                           BindingResult result,
//                                           RedirectAttributes redirectAttributes,
//                                           Model model) {
//        if (result.hasErrors()) {
//            redirectAttributes.addFlashAttribute("error", "Error al registrar el usuario.");
//            return "redirect:/admin/usuarios";
//        }
//
//        if (userService.existePorCorreo(usuario.getCorreo())) {
//            redirectAttributes.addFlashAttribute("error", "Este correo ya está registrado.");
//            return "redirect:/admin/usuarios";
//        }
//
//        userService.guardar(usuario);
//        redirectAttributes.addFlashAttribute("mensajeExito", "Usuario agregado exitosamente.");
//        return "redirect:/admin/usuarios";
//    }
}
