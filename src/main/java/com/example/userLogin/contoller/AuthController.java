package com.example.userLogin.contoller;

// import java.util.HashMap;
// import java.util.Map;

// import javax.servlet.http.HttpSession;

// import org.apache.commons.lang3.RandomStringUtils;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// public class AuthController {
//     @Autowired
//     private JavaMailSender javaMailSender;

//     private Map<String, String> otpStorage = new HashMap<>();

//     @GetMapping("/login")
//     public String login() {
//         return "login";
//     }

//     @GetMapping("/register")
//     public String register() {
//         return "register";
//     }

//     @PostMapping("/register")
//     public String register(@RequestParam String username, @RequestParam String password) {
//         return "redirect:/login";
//     }

//     @PostMapping("/otp")
//     public String sendOtp(HttpSession session, @RequestParam String email) {
//         String otp = RandomStringUtils.randomNumeric(6);
//         otpStorage.put(email, otp);

//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setTo(email);
//         message.setSubject("Your OTP Code");
//         message.setText("Your OTP code is : " + otp);
//         javaMailSender.send(message);
//         return "verify-otp";
//     }

//     @PostMapping("/verify-otp")
//     public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
//         String storedOtp = otpStorage.get(email);
//         if (storedOtp != null && storedOtp.equals(otp)) {
//             otpStorage.remove(email);
//             Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//             if (auth != null && auth.getName() != null) {
//                 // OTP verified successfully
//                 return "redirect:/home";
//             }
//         }
//         model.addAttribute("error", "Invalid OTP");
//         return "verify-otp";
//     }

//     @GetMapping("/home")
//     public String home() {
//         return "home";
//     }
// }


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.userLogin.entity.User;
import com.example.userLogin.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Map<String, String> otpStorage = new HashMap<>();

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return "redirect:/login";
    }

    @PostMapping("/otp")
    public String sendOtp(HttpSession session, @RequestParam String email) {
        String otp = RandomStringUtils.randomNumeric(6);
        otpStorage.put(email, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is : " + otp);
        javaMailSender.send(message);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, Model model) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            return "redirect:/home";
        }
        model.addAttribute("error", "Invalid OTP");
        return "verify-otp";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
