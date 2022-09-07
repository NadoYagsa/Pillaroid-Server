package com.nadoyagsa.pillaroid.configuration;

import com.nadoyagsa.pillaroid.jwt.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfiguration implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    @Autowired
    public SecurityConfiguration(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 토큰 검사 안하는 경로 설정함(/login/**, 알약 검색 등)
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(new String[]{"/login/**", "/medicine/**", "/pill"});
    }
}
