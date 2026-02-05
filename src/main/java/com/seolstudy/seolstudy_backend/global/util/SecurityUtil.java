package com.seolstudy.seolstudy_backend.global.util;

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
        // TODO: Implement actual SecurityContext extraction
        return 2L; // Returning "Min Yujin" as per db.md
    }

    public static Long getLoginUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(auth.getName());
    }
}
