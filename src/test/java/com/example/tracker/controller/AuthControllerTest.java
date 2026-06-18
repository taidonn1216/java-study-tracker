package com.example.tracker.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.tracker.config.SecurityConfig;
import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.CustomUserDetailsService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;



@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void testRegister_UsernameBlank() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "")
                .param("password", "pass")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "ユーザー名を入力してください。"));

        verify(userRepository, never()).insert(anyString(), anyString());
    }

    @Test
    void testRegister_UsernameTooShort() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "a")
                .param("password", "pass")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "ユーザー名は2文字以上で入力してください。"));

        verify(userRepository, never()).insert(anyString(), anyString());
    }

    @Test
    void testRegister_UsernameDuplicate() throws Exception {
        when(userRepository.findByUsername("taro")).thenReturn(Optional.of(new User(/* 既存ユーザー */)));

        mockMvc.perform(post("/register")
                .param("username", "taro")
                .param("password", "pass")
                .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "そのユーザー名はすでに使われています。"));

        verify(userRepository, never()).insert(anyString(), anyString());

    }

    @Test
    void testRegister_PasswordBlank() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "taro")
                .param("password", "")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "パスワードを入力してください。"));

        verify(userRepository, never()).insert(anyString(), anyString());
    }

    @Test
    void testRegister_PasswordTooShort() throws Exception {
        mockMvc.perform(post("/register")
                .param("username", "taro")
                .param("password", "abc")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attribute("errorMessage", "パスワードは4文字以上で入力してください。"));

        verify(userRepository, never()).insert(anyString(), anyString());
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userRepository.findByUsername("taro")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("hashedPass");

        mockMvc.perform(post("/register")
                .param("username", "taro")
                .param("password", "pass")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userRepository, times(1)).insert("taro", "hashedPass");
    }

}
