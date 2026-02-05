package com.seolstudy.seolstudy_backend.global.util;

import com.seolstudy.seolstudy_backend.global.error.BusinessException;
import com.seolstudy.seolstudy_backend.global.error.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    /**
     * Retrieves the current authenticated user's ID.
     * For MVP/Testing, this returns a fixed ID.
     * In the future, this will extract the ID from the SecurityContext.
     */
    public Long getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null){
            throw new BusinessException("인증되지 않은 회원입니다.", ErrorCode.UNAUTHORIZED);
        }

        try{
            return Long.parseLong(authentication.getName());
        } catch(NumberFormatException e){
            return null;
        }
    }
}
