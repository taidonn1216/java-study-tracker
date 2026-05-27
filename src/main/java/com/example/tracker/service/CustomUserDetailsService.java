package com.example.tracker.service;

import com.example.tracker.model.User;
import com.example.tracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security 用のユーザー取得サービス。
 * 
 * <p>
 * {@link UserRepository} を使って {@code USERS} テーブルから
 * ユーザー情報を取得し、 {@link UserDetails} に変換する。
 * ログイン時にユーザー名からユーザー情報を検索し、パスワード認証に使用される。
 * </p>
 * 
 * @author tracker-team
 * @version 1.0
 * @since 1.0
 * @see UserRepository
 * @see UserDetails
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /** ユーザー情報取得用のリポジトリ */
    private final UserRepository userRepository;

    /**
     * コンストラクタインジェクション
     * 
     * @param userRepository {@link UserRepository} インスタンス
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ユーザー名でユーザー情報を検索し、Spring Security 用の {@link UserDetails} に変換する。
     * 
     * <p>
     * ログイン処理の際に Spring Security から呼び出される。
     * 指定されたユーザー名が見つからない場合は {@link UsernameNotFoundException} をスロー。
     * </p>
     * 
     * @param username 検索するユーザー名
     * @return Spring Security が使用する {@link UserDetails} インスタンス
     * @throws UsernameNotFoundException ユーザーが見つからない場合
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
