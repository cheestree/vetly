package com.cheestree.vetly.config

import com.cheestree.vetly.http.AuthenticatedUserArgumentResolver
import com.cheestree.vetly.http.AuthenticatorInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    @Autowired
    private lateinit var protectedRouteInterceptor: AuthenticatorInterceptor

    @Autowired
    private lateinit var authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins("http://localhost:8081")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(protectedRouteInterceptor).addPathPatterns("/**")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}
