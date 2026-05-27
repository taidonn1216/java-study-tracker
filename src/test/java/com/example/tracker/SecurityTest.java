package com.example.tracker;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Spring Security のアクセス制御テスト。
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 未ログインで科目一覧にアクセスするとログイン画面にリダイレクト() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void 未ログインで科目詳細にアクセスするとログイン画面にリダイレクト() throws Exception {
        mockMvc.perform(get("/subjects/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void 未ログインで登録画面にアクセスできる() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void 未ログインで管理画面にアクセスするとログイン画面にリダイレクト() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(roles = "GENERAL")
    void GENERALロールで管理画面にアクセスすると403() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ADMINロールで管理画面にアクセスすると200() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

}
