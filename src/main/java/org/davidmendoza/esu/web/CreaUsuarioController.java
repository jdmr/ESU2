/*
 * The MIT License
 *
 * Copyright 2015 J. David Mendoza.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.davidmendoza.esu.web;

import java.util.Arrays;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.davidmendoza.esu.Constants;
import org.davidmendoza.esu.dao.RolRepository;
import org.davidmendoza.esu.model.SocialMediaService;
import org.davidmendoza.esu.model.Usuario;
import org.davidmendoza.esu.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
public class CreaUsuarioController extends BaseController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    protected AuthenticationManager authenticationManager;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @RequestMapping(value = "/usuario/crea", method = RequestMethod.GET)
    public String creaUsuario(WebRequest request, Model model, ProviderSignInUtils providerSignInUtils, HttpSession session) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);

        Usuario usuario = new Usuario();
        if (connection != null) {
            log.debug("Found connection... Fetching profile...");
            UserProfile profile = connection.fetchUserProfile();
            log.debug("Creating User for {}", profile.getUsername());
            usuario.setUsername(profile.getEmail());
            usuario.setNombre(profile.getFirstName());
            usuario.setApellido(profile.getLastName());

            ConnectionKey providerKey = connection.getKey();
            usuario.setSignInProvider(SocialMediaService.valueOf(providerKey.getProviderId().toUpperCase()));

            String imageUrl = connection.getImageUrl();
            log.debug("ImageUrl: {}", imageUrl);
            if (StringUtils.isNotEmpty(imageUrl)) {
                log.debug("Storing image in session");
                session.setAttribute("imageUrl", imageUrl);
            }
        }
        model.addAttribute("usuario", usuario);

        return "usuario/crea";
        
    }
    
    @RequestMapping(value = "/usuario/crea", method = RequestMethod.POST)
    public String creaUsuario(Usuario user, BindingResult bindingResult, HttpServletRequest request, ProviderSignInUtils providerSignInUtils, WebRequest webRequest, HttpSession session) {
        log.debug("Registering user {}", user.getUsername());
        String back = "user/registration";
        if (bindingResult.hasErrors()) {
            log.warn("Could not register user {}", bindingResult.getAllErrors());
            return back;
        }

        try {
            Usuario u = usuarioService.obtiene(user.getUsername());
            if (u == null) {
                String password;
                if (StringUtils.isNotBlank(user.getPassword())) {
                    password = user.getPassword();
                } else {
                    password = Arrays.toString(KeyGenerators.secureRandom().generateKey());
                    user.setPassword(password);
                }
                user.getRoles().add(rolRepository.findByAuthorityIgnoreCase("ROLE_USER"));
                usuarioService.crea(user);

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
                token.setDetails(new WebAuthenticationDetails(request));
                Authentication authenticatedUser = authenticationManager.authenticate(token);

                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

                if (user.getSignInProvider() != null) {
                    providerSignInUtils.doPostSignUp(user.getUsername(), webRequest);
                    password = user.getSignInProvider().toString() + " Account";
                }

                MimeMessage message = mailSender.createMimeMessage();
                InternetAddress[] addresses = {new InternetAddress("Escuela Sabática Universitaria <portal@um.edu.mx>")};
                message.addFrom(addresses);
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(user.getUsername());
                helper.setSubject("");
                String content = "<p>Bienvenido a Escuela Sabática Universitaria!</p>"
                        + "<p>Esta cuenta servirá para que puedas compartir con tus amigos y familiares opiniones dentro de este sitio.</p>";
                content = content.replaceAll("@@NAME@@", user.getNombre());
                content = content.replaceAll("@@USERNAME@@", user.getUsername());
                content = content.replaceAll("@@PASSWORD@@", password);

                helper.setText(content, true);
                mailSender.send(message);

            } else {
                log.warn("User already exists", user.getUsername());
                bindingResult.rejectValue("username", "user.email.already.exists");
                return back;
            }

            return "redirect:/";
        } catch (Exception e) {
            log.error("Could not register user", e);
            return back;
        }
    }
    
    @RequestMapping(value = "/usuario/olvido", method = RequestMethod.GET)
    public String olvido(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "usuario/olvido";
    }

    @RequestMapping(value = "/usuario/olvido", method = RequestMethod.POST)
    public String forgot(@RequestParam String username, RedirectAttributes redirectAttributes) {
        log.debug("Username: {}", username);
        if (StringUtils.isNotEmpty(username)) {
            
            Usuario usuario = usuarioService.obtiene(username);

            if (usuario != null) {
                String password = RandomStringUtils.random(6, false, true);
                String encoded = passwordEncoder.encode(password);
                usuario.setPassword(encoded);
                usuario.setPasswordVerification(encoded);
                usuarioService.actualiza(usuario);

                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    InternetAddress[] addresses = {new InternetAddress("Escuela Sabática Universitaria <portal@um.edu.mx>")};
                    message.addFrom(addresses);
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setTo(usuario.getUsername());
                    helper.setSubject("Cambio de contraseña");
                    String content = "<p>@@NAME@@</p><p>La nueva contraseña es: <strong>@@PASSWORD@@</strong> para el usuario: <strong>@@USERNAME@@</strong>.</p>";
                    content = content.replaceAll("@@NAME@@", usuario.getNombre());
                    content = content.replaceAll("@@USERNAME@@", usuario.getUsername());
                    content = content.replaceAll("@@PASSWORD@@", password);

                    helper.setText(content, true);
                    mailSender.send(message);
                    
                    redirectAttributes.addFlashAttribute("message", "Se ha enviado correo electrónico con nueva contraseña.");
                } catch (Exception e) {
                    log.error("Could not send forgot email", e);
                }
            }
        }
        return "redirect:/";
    }
}
