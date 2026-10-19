package NarayanGroup.example.E_Commerce.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/** Authentication user lookup contract used by login and the JWT filter. */
public interface ILoginService extends UserDetailsService {
    Long getUserId(String email);
}
