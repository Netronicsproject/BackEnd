package hello.netro.service;

import hello.netro.auth.LoginUser;
import hello.netro.domain.User;
import hello.netro.dto.UserProfileDto;
import hello.netro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    public void updateUserProfile(User user, UserProfileDto userProfileDto)
    {
        user.updateProfile(userProfileDto);
    }
}
