package woowoong.slacker.service.login;

//카카오에서 받은 사용자 정보를 통해 사용자 데이터를 처리합니다.
// 여기서 일반 사용자와 사장님을 구분하고, DB에 사용자 정보를 저장

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import woowoong.slacker.domain.Role;
import woowoong.slacker.domain.User;
import woowoong.slacker.dto.login.KakaoLoginResponse;
import woowoong.slacker.dto.login.KakaoUserInfoResponse;
import woowoong.slacker.dto.login.UserResponse;
import woowoong.slacker.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public KakaoLoginResponse processKakaoLogin(KakaoUserInfoResponse kakaoUserInfoResponse, Role role) {
        Long kakaoId = kakaoUserInfoResponse.id;
        String nickname = kakaoUserInfoResponse.kakaoAccount.getProfile().getNickName();

        // 사용자 정보가 이미 있는지 확인
        Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);

        if (existingUser.isPresent()) {
            // 기존 사용자 업데이트
            User user = existingUser.get();
            user.setUsername(nickname);
            if(role == Role.OWNER) {
                user.setRole(role);
            }
            User savedUser = userRepository.save(user);
            // 첫 로그인이 아님
            return new KakaoLoginResponse(new UserResponse(savedUser), "false");
        } else {
            // 새로운 사용자 등록
            User newUser = new User(kakaoId, nickname, role);
            User savedUser =  userRepository.save(newUser);
            // 첫 로그인
            return new KakaoLoginResponse(new UserResponse(savedUser), "true");
        }
    }
}
