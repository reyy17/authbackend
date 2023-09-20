package com.authLogin.AuthLogin.controller;

import com.authLogin.AuthLogin.dto.LoginDto;
import com.authLogin.AuthLogin.dto.ResetPasswordDto;
import com.authLogin.AuthLogin.dto.SignUpDto;
import com.authLogin.AuthLogin.entity.Role;
import com.authLogin.AuthLogin.entity.User;
import com.authLogin.AuthLogin.repo.RoleRepository;
import com.authLogin.AuthLogin.repo.UserRepository;
import com.authLogin.AuthLogin.service.EmailService;
import com.authLogin.AuthLogin.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/v1")
@CrossOrigin(origins = "http://localhost:3001")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;



    @PostMapping("/signin")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>("User signed-in successfully!", HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(signUpDto.getName());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));


        Set<Role> userRoles = new HashSet<>();
        if (signUpDto.getRoles() != null) {
            for (String roleName : signUpDto.getRoles()) {
                Role role = roleRepository.findByName(roleName).orElse(null);
                if (role != null) {
                    userRoles.add(role);
                }
            }
        }
        user.setRoles(userRoles);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }


    @RequestMapping(value = "/forgot", method = RequestMethod.GET)
    public ModelAndView displayForgotPasswordPage() {
        return new ModelAndView("forgotPassword");
    }


    @RequestMapping(value = "/forgot", method = RequestMethod.POST)
    public ModelAndView processForgotPasswordForm(ModelAndView modelAndView,
                                                  @RequestParam("email") String userEmail, HttpServletRequest request) {


        Optional<User> optional = userService.findUserByEmail(userEmail);

        if (!optional.isPresent()) {
            modelAndView.addObject("errorMessage",
                    "We didn't find an account for that e-mail address.");
        } else {

            User user = optional.get();
            user.setResetToken(UUID.randomUUID().toString());


            userService.save(user);


            String appUrl = request.getScheme() + "://" + request.getServerName() + ":"
                    + request.getServerPort() + request.getContextPath() + "/api/auth/v1";


            SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
            passwordResetEmail.setFrom("support@demo.com");
            passwordResetEmail.setTo(user.getEmail());
            passwordResetEmail.setSubject("Password Reset Request");
            passwordResetEmail.setText("To reset your password, click the link below:\n" + appUrl
                    + "/reset?token=" + user.getResetToken());

            emailService.sendEmail(passwordResetEmail);


            modelAndView.addObject("successMessage",
                    "A password reset link has been sent to " + userEmail);
        }

        modelAndView.setViewName("forgotPassword");
        return modelAndView;

    }


    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ModelAndView displayResetPasswordPage(ModelAndView modelAndView, @RequestParam("token") String token) {

        Optional<User> user = userService.findUserByResetToken(token);

        if (user.isPresent()) {

             modelAndView = new ModelAndView("resetPassword");
            modelAndView.addObject("resetToken", token);
            return modelAndView;
        } else {
            modelAndView
                    .addObject("errorMessage", "Oops! This is an invalid password reset link.");
            modelAndView.setViewName("errorPage");
        }

        return modelAndView;
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String resetToken, @RequestBody ResetPasswordDto resetPasswordDto) {

        Optional<User> optionalUser = userService.findUserByResetToken(resetToken);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            user.setResetToken(null); // Clear the reset token

            userRepository.save(user);

            return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid reset token", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, null, authentication);
        }
        return new ResponseEntity<>("User logged out successfully", HttpStatus.OK);
    }



    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ModelAndView handleMissingParams(MissingServletRequestParameterException ex) {
        return new ModelAndView("redirect:login");
    }

}
