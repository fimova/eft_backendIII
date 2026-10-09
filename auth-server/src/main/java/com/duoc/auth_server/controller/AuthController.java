package com.duoc.auth_server.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//endpoint para iniciar y acceder a un canal 
@Controller
public class AuthController {

        public static final String CHANNEL_SESSION_KEY = "AUTH_CHANNEL";

        @GetMapping("/api/auth/login/{channel}")
        public String iniciarLogin(
                        @PathVariable String channel,
                        HttpSession session) {

                String canal = channel.toUpperCase();

                if (!canal.equals("WEB")
                                && !canal.equals("MOBILE")
                                && !canal.equals("ATM")) {
                        throw new IllegalArgumentException(
                                        "Canal no válido");
                }

                session.setAttribute(
                                CHANNEL_SESSION_KEY,
                                canal);

                return "redirect:/oauth2/authorization/github";
        }
}
