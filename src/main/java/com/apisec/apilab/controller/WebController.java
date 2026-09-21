package com.apisec.apilab.controller;

import com.apisec.apilab.dto.LoginRequest;
import com.apisec.apilab.dto.RegisterRequest;
import com.apisec.apilab.entity.User;
import com.apisec.apilab.exception.DuplicateEmailException;
import com.apisec.apilab.exception.InvalidCredentialsException;
import com.apisec.apilab.exception.InvalidRegistrationException;
import com.apisec.apilab.security.JwtCookieService;
import com.apisec.apilab.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    private final AuthService authService;
    private final JwtCookieService jwtCookieService;

    public WebController(AuthService authService, JwtCookieService jwtCookieService) {
        this.authService = authService;
        this.jwtCookieService = jwtCookieService;
    }

    @GetMapping("/")
    public String landing(@AuthenticationPrincipal User user) {
        if (user != null) {
            return "redirect:/dashboard";
        }
        return "index";
    }

    @GetMapping("/signup")
    public String signupForm(Model model, @AuthenticationPrincipal User user) {
        if (user != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        try {
            var auth = authService.register(request);
            jwtCookieService.write(response, auth.getToken());
            return "redirect:/dashboard";
        } catch (InvalidRegistrationException | DuplicateEmailException ex) {
            model.addAttribute("formError", ex.getMessage());
            return "signup";
        }
    }

    @GetMapping("/login")
    public String loginForm(
            Model model,
            @AuthenticationPrincipal User user,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered) {
        if (user != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        if ("auth".equals(error)) {
            model.addAttribute("formError", "Please sign in to continue");
        }
        if (registered != null) {
            model.addAttribute("formSuccess", "Account created. Sign in to continue.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") LoginRequest request,
            BindingResult bindingResult,
            HttpServletResponse response,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            var auth = authService.login(request);
            jwtCookieService.write(response, auth.getToken());
            return "redirect:/dashboard";
        } catch (InvalidCredentialsException ex) {
            model.addAttribute("formError", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("activePage", "profile");
        return "profile";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        jwtCookieService.clear(response);
        SecurityContextHolder.clearContext();
        redirectAttributes.addFlashAttribute("formSuccess", "You have been signed out.");
        return "redirect:/login";
    }
}
