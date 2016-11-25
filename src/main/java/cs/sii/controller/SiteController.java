package cs.sii.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs.sii.model.role.Role;
import cs.sii.model.role.RoleRepository;
import cs.sii.model.user.User;
import cs.sii.model.user.UserRepository;
import cs.sii.service.dao.RoleServiceImpl;
import cs.sii.service.dao.UserServiceImpl;


@Controller
public class SiteController {
	
	@Autowired
	AuthenticationTrustResolver authenticationTrustResolver;
	
	@Autowired
	PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;
	
	@Autowired
	UserServiceImpl userService;
	
	@Autowired
	RoleServiceImpl roleService;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String Home() {
		return "white";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {		
//			if (isCurrentAuthenticationAnonymous()) {
//				return "login";
//		    } else {
//		    	return "redirect:/index";  
//		    }
		return "login";
	}
	
//	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index() {
	    	return "index";  
	    
	}

	@RequestMapping(value = "/forms", method = RequestMethod.GET)
	public String forms() {
	    	return "forms";		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/maps", method = RequestMethod.GET)
	public String maps() {
	    	 return "maps";
	}
	
	
	/**
	 * This method handles logout requests.
	 * Toggle the handlers if you are RememberMe functionality is useless in your app.
	 */
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			//new SecurityContextLogoutHandler().logout(request, response, auth);
			persistentTokenBasedRememberMeServices.logout(request, response, auth);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "white";
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
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result,ModelMap model) {

		if (result.hasErrors()) {
			System.out.println("1 "+result.toString());
			return "registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [sso] should be implementing custom @Unique annotation 
		 * and applying it on field [sso] of Model class [User].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(userService.getUserRepository().findBySsoId(user.getSsoId()) != null){
			System.out.println("2");
			return "registration";
		}else{
			
//		if(!userRepository.isUserSSOUnique(user.getId(), user.getSsoId())){
//			FieldError ssoError =new FieldError("user","ssoId",messageSource.getMessage("non.unique.ssoId", new String[]{user.getSsoId()}, Locale.getDefault()));
//		    result.addError(ssoError);
//			return "registration";
//		}
			System.out.println("3 "+user.toString());
			
			
		userService.save(user);

		model.addAttribute("success", "User " + user.getFirstName() + " "+ user.getLastName() + " registered successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		//return "success";
		return "registrationsuccess";
		}
	}
	
	
	
	
	
	/**
	 * This method will provide UserProfile list to views
	 */
	@ModelAttribute("roles")
	public List<Role> initializeProfiles() {
		return roleService.getRoleRepository().findAll();
	}
	
	
	
	private String getPrincipal(){
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails)principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}


	/**
	 * This method returns true if users is already authenticated [logged-in], else false.
	 */
	private boolean isCurrentAuthenticationAnonymous() {
	    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    return authenticationTrustResolver.isAnonymous(authentication);
	}

}
