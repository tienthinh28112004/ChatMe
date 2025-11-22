package LapTrinhMang.WeChat.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {
    private final CustomJwtDecoder customJwtDecoder;
    private static final String[] White_List = {
            "/**"
    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //xử lý các endpoint public
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
        http.authorizeHttpRequests(request -> request
                .requestMatchers(White_List).permitAll()
                .anyRequest().authenticated()
        ).sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS));
        //xử lý các endpoint cần token
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())//bắt lỗi 401(đăng nhâp)
                .accessDeniedHandler(new JwtAccessDined()));//bắt lỗi 403(không có quyền)
        return http.build();
    }
    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}