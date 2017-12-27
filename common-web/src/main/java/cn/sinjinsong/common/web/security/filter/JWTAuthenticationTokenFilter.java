package cn.sinjinsong.common.web.security.filter;

import cn.sinjinsong.common.web.properties.AuthenticationProperties;
import cn.sinjinsong.common.web.security.domain.TokenCheckResult;
import cn.sinjinsong.common.web.security.token.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JWTAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenManager tokenManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        log.info("经过JWTAuthenticationTokenFilter");
        //拿到token
        String token = request.getHeader(AuthenticationProperties.AUTH_HEADER);
        //验证token，如果无效，结果返回exception；如果有效，结果返回username
        TokenCheckResult result = tokenManager.checkToken(token);
        if (!result.isValid()) {
            log.info("Token无效");
            request.setAttribute(AuthenticationProperties.EXCEPTION_ATTR_NAME, result.getException());
        } else {
            log.info("checking authentication {}", result);
            UserDetails userDetails = userDetailsService.loadUserByUsername(result.getUsername());
            //如果登陆后首次访问
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                        request));
                log.info("authenticated user {} ,setting security context", result);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
