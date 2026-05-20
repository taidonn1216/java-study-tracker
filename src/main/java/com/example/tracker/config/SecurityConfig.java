package com.example.tracker.config;
import com.example.tracker.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security の設定クラス
 * 
 * <p>ログイン・ログアウトの設定、アクセス制御、
 * パスワードエンコーダー、認証プロバイダーの定義を行う。</p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 */
@Configuration
public class SecurityConfig {

    /** ユーザー情報取得サービス */
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * コンストラクタインジェクション
     * 
     * @param customUserDetailsService {@link CustomUserDetailsService} インスタンス
     */
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * セキュリティフィルターチェーンの設定。
     * 
     * <p>ログインページ・ ユーザー登録ページ・CSSは認証不要。
     * それ以外のページは認証が必須。</p>
     * 
     * @param http {@link HttpSecurity} インスタンス
     * @return {@link SecurityFilterChain} インスタンス
     * @throws Exception 設定エラー時
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    if(authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/admin");
                    } else {
                        response.sendRedirect("/");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )    
            .authenticationProvider(authenticationProvider());
             
        return http.build();
    }

    /**
     * BCrypt パスワードエンコーダーを Bean として登録する。
     * 
     * @return {@link PasswordEncoder} インスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 認証プロバイダーの設定。
     * 
     * <p> {@link CustomUserDetailsService} と {@link PasswordEncoder} を
     * 使用してユーザー認証を行う。 </p>
     * 
     * @return {@link DaoAuthenticationProvider}
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
