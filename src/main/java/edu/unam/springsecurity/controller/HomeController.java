package edu.unam.springsecurity.controller;

import edu.unam.springsecurity.enums.EstadoTecnico;
import edu.unam.springsecurity.model.Administrador;
import edu.unam.springsecurity.model.Categoria;
import edu.unam.springsecurity.model.Tecnico;
import edu.unam.springsecurity.model.Usuario;
import edu.unam.springsecurity.repository.AdministradorRepository;
import edu.unam.springsecurity.repository.TecnicoRepository;
import edu.unam.springsecurity.repository.UsuarioRepository;
import edu.unam.springsecurity.service.*;
import edu.unam.springsecurity.util.Archivos;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

import static edu.unam.springsecurity.util.Archivos.obtenerExtension;

@Controller
public class HomeController {
	private final HomeService homeService;
	private final UserService userService;
	private final AdminService adminService;
	private final TecnicoService tecnicoService;

	// Controller Injection
	public HomeController(HomeService homeService, UserServiceImpl userService, AdminService adminService, TecnicoService tecnicoService) {
		this.homeService = homeService;
		this.userService = userService;
		this.adminService = adminService;
		this.tecnicoService= tecnicoService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("text", homeService.getText());
		return "index";
	}

	@GetMapping("/index")
	public String index() {
		return "redirect:/";
	}
	
	@GetMapping("/user")
	public String user(Model model) {
		model.addAttribute("text", userService.getText());
		return "user";
	}

	@GetMapping("/registro")
	public String mostrarFormularioRegistro(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "registro"; // <-- este debe ser el nombre del HTML sin la extensión
	}

	//REGISTRO USUARIO


	@PostMapping("/registro")
	public String registrarUsuario(@Valid @ModelAttribute Usuario usuario,
								   BindingResult result,
								   RedirectAttributes redirectAttributes,
								   Model model) {
		if (result.hasErrors()) {
			model.addAttribute("error", "Corrige los errores del formulario");
			return "principal/registro";
		}

		if (userService.existePorCorreo(usuario.getCorreo())) {
			model.addAttribute("error", "Este correo ya está registrado");
			return "principal/registro";
		}

		userService.guardar(usuario);
		redirectAttributes.addFlashAttribute("mensajeExito", "Usuario registrado exitosamente");
		return "redirect:/registro";
	}

//Registro Tecnico

	@Autowired
	private ServicioService servicioService;



	private final String RUTA_EFIRMA = "uploads/efirma";
	private final String RUTA_CERTIFICACION = "uploads/certificacion";
	@GetMapping("/registroTecnico")
	public String mostrarFormularioRegistroTecnico(Model model) {
		model.addAttribute("tecnico", new Tecnico());
//		List<Categoria> categoriasEstaticas = List.of(
//				new Categoria(1, "Electricista", null, null),
//				new Categoria(2, "Plomero", null, null),
//				new Categoria(3, "Técnico en electrodomésticos", null, null)
//		);
//		model.addAttribute("categorias", categoriasEstaticas);
//		model.addAttribute("servicios", servicioService.obtenerTodos());
		//model.addAttribute("estados", EstadoTecnico.values());
		return "registroTecnico"; // <-- este debe ser el nombre del HTML sin la extensión
	}

	@PostMapping("/registroTecnico")
	public String registrarTecnico(@Valid @ModelAttribute("tecnico") Tecnico tecnico,
								   BindingResult result,
								   @RequestParam("efirma") MultipartFile efirma,
								   @RequestParam("certificacion") MultipartFile certificacion,
								   Model model) {

		if (result.hasErrors()) {
			model.addAttribute("error", "Por favor corrige los errores del formulario.");
			return "registroTecnico";
		}

		if (tecnicoService.existePorCorreo(tecnico.getCorreo())) {
			model.addAttribute("error", "Este correo ya está registrado.");
			return "registroTecnico";
		}

		// 1️⃣ Guardar técnico para obtener el ID
		tecnico.setRutaEfirma(""); // si los campos en BD permiten null, puedes omitir
		tecnico.setRutaCertificacion("");
		tecnicoService.guardar(tecnico); // Ahora ya tiene ID

		// 2️⃣ Guardar los archivos con el ID como nombre
		String nombreEfirma = tecnico.getId() + "_efirma." + obtenerExtension(efirma.getOriginalFilename());
		String nombreCert = tecnico.getId() + "_certificacion." + obtenerExtension(certificacion.getOriginalFilename());

		String rutaEfirma = Archivos.almacenarConNombre(efirma, RUTA_EFIRMA, nombreEfirma);
		String rutaCert = Archivos.almacenarConNombre(certificacion, RUTA_CERTIFICACION, nombreCert);

		// 3️⃣ Asignar rutas reales y guardar nuevamente
		tecnico.setRutaEfirma("/" + RUTA_EFIRMA + "/" + nombreEfirma);
		tecnico.setRutaCertificacion("/" + RUTA_CERTIFICACION + "/" + nombreCert);
		tecnicoService.guardar(tecnico);

		model.addAttribute("mensaje", "Técnico registrado exitosamente");
		return "registroTecnico";
	}


//	@GetMapping("/admin")
//	public String admin(Model model) {
//		model.addAttribute("text", adminService.getText());
//		return "admin";
//	}

	@GetMapping("/tecnico")
	public String tecnico(Model model) {
		model.addAttribute("text", tecnicoService.getText());
		return "tecnico";
	}


	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@Autowired
	private AdministradorRepository administradorRepository;

	@Autowired
	private TecnicoRepository tecnicoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@PostMapping("/login_sesion")
	public String loginPersonalizado(@RequestParam String username,
									 @RequestParam String password,
									 HttpSession session,
									 RedirectAttributes redirectAttributes) {

		// 1. Validar si es administrador
		Administrador admin = administradorRepository.findByCorreo(username);
		if (admin == null && username.matches("\\d{10}")) {
			admin = administradorRepository.findByTelefono(username);
		}
		if (admin != null && admin.getContrasena().equals(password)) {
			session.setAttribute("admin", admin);
			session.setAttribute("idAdmin", admin.getId());

			UserDetails userDetails = org.springframework.security.core.userdetails.User
					.withUsername(admin.getCorreo())
					.password(admin.getContrasena())
					.roles("ADMIN")
					.build();

			Authentication auth = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);

			return "redirect:/admin";
		}

		// 2. Validar si es usuario
		Usuario usuario = usuarioRepository.findByCorreo(username);
		if (usuario == null && username.matches("\\d{10}")) {
			usuario = usuarioRepository.findByTelefono(username);
		}
		if (usuario != null && usuario.getContrasena().equals(password)) {
			session.setAttribute("usuario", usuario);
			session.setAttribute("idUsuario", usuario.getId());

			UserDetails userDetails = org.springframework.security.core.userdetails.User
					.withUsername(usuario.getCorreo())
					.password(usuario.getContrasena())
					.roles("USER")
					.build();

			Authentication auth = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);

			return "redirect:/usuario";
		}

		// 3. Validar si es técnico
		Tecnico tecnico = tecnicoRepository.findByCorreo(username);
		if (tecnico == null && username.matches("\\d{10}")) {
			tecnico = tecnicoRepository.findByTelefono(username);
		}
		if (tecnico != null && tecnico.getContrasena().equals(password)) {
			session.setAttribute("tecnico", tecnico);
			session.setAttribute("idTecnico", tecnico.getId());

			UserDetails userDetails = org.springframework.security.core.userdetails.User
					.withUsername(tecnico.getCorreo())
					.password(tecnico.getContrasena())
					.roles("TECNICO")
					.build();

			Authentication auth = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);

			return "redirect:/tecnico";
		}

		// Si ninguno coincide
		redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
		return "redirect:/login";
	}










	@PostMapping("/login")
	public String iniciarSesion(@RequestParam String username,
						@RequestParam String password,
						HttpSession session,
						RedirectAttributes redirectAttributes) {

		System.out.println("📩 Correo recibido: " + username);
		System.out.println("🔐 Contraseña recibida: " + password);

		Usuario usuario = userService.validarLogin(username, password);
		if (usuario != null) {
			session.setAttribute("usuarioActual", usuario);
			session.setAttribute("idUsuario", usuario.getId());
			return "redirect:/spring/login_inicio";
		} else {
			redirectAttributes.addFlashAttribute("error", "Credenciales inválidas");
			return "redirect:/login";  // esta es tu ruta actual de formulario
		}
	}

	@PostMapping("/login_success_handler")
	public String loginSuccessHandler() {
		System.out.println("Logging user login success...");
		return "index";
	}

	@PostMapping("/login_failure_handler")
	public String loginFailureHandler() {
		System.out.println("Login failure handler....");
		return "login";
	}

}
