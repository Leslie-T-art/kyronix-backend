package com.kyronic.riskengine.auth.infrastructure.security;

import com.kyronic.riskengine.auth.domain.UserAccount;
import com.kyronic.riskengine.auth.infrastructure.persistence.UserAccountRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public DatabaseUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if (account.isLocked()) {
            throw new LockedException("account is locked");
        }
        if (!account.isActive()) {
            throw new UsernameNotFoundException("account is inactive");
        }
        return new LocalUserPrincipal(
                account.getId(),
                account.getUsername(),
                account.getPasswordHash(),
                account.isActive(),
                !account.isLocked(),
                account.getFullName(),
                account.getDepartmentId(),
                account.getBranchId(),
                account.getRoles(),
                account.getPermissions(),
                Stream.concat(
                                account.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                                account.getPermissions().stream().map(SimpleGrantedAuthority::new))
                        .toList()
        );
    }
}
