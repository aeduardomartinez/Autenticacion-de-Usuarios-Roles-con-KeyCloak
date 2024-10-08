package com.api.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    @Value("${jwt.auth.converter.principleAtribute}")
    private String principleAtribute;
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities = Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(),extractResourceRoles(jwt).stream())
                .toList();

        return new JwtAuthenticationToken(jwt,authorities,getPrincipleName(jwt));
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt){

        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String>  resourceRobles;

        if(jwt.getClaim("resource_access")==null){
            return List.of();
        }

        resourceAccess = jwt.getClaim("resource_access");
        if(resourceAccess.get(resourceId) == null){
            return List.of();
        }
        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        if(resource.get("roles")==null){
            return List.of();
        }

        resourceRobles= (Collection<String>) resource.get("roles");

        return  resourceRobles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                .toList();
    }

    private String getPrincipleName(Jwt jwt){
        String clainName = JwtClaimNames.SUB;

        if(principleAtribute != null){
            clainName = principleAtribute;
        }
        return jwt.getClaim(clainName);
    }
}


