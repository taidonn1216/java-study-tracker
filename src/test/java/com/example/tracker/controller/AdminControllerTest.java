package com.example.tracker.controller;

//JUnit
import org.junit.jupiter.api.Test;

//Spring boot Test
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

//Spring Security test
import org.springframework.security.test.context.support.WithMockUser;

//アプリ内テスト
import com.example.tracker.config.SecurityConfig;
import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import com.example.tracker.service.CustomUserDetailsService;

//static import
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(com.example.tracker.controller.AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMINで管理画面が表示される() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @Test
    @WithMockUser(roles = "GENERAL")
    void GENERALで管理画面は403() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void 権限が成功したらadminにリダイレクト() throws Exception {
        User target = new User();
        target.setUsername("user");

        when(userRepository.findById(1L)).thenReturn(target);

        mockMvc.perform(post("/admin/users/1/role")
               .param("role", "GENERAL")
               .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void 自分自身の権限変更はエラーになる() throws Exception {
        User target = new User();
        target.setUsername("admin");

        when(userRepository.findById(1L)).thenReturn(target);

        mockMvc.perform(post("/admin/users/1/role")
               .param("role", "GENERAL")
               .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/admin"))
               .andExpect(flash().attribute("errorMessage", "自分自身の権限は変更できません。"));
    }

    @Test
    void 未認証でPOSTすると302リダイレクト() throws Exception {
        mockMvc.perform(post("/admin/users/1/role")
               .param("role", "GENERAL")
               .with(csrf()))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }

}
