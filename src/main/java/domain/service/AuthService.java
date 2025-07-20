package domain.service;

import api.dto.AuthSignUpRequest;
import domain.repository.UsersRepository;
import infra.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepository usersRepository;

    @Transactional
    public Boolean signUp(AuthSignUpRequest request){
        if(usersRepository.findByEmail(request.getEmail())!=null) {
            return false;
        }

        Users users = Users.builder()
                .email(request.getEmail())
                .passwordHash(request.getPassword())
                .name(request.getName())
                .birthDay(request.getBirthDay())
                .build();

        usersRepository.save(users);

        return true;
    }

}
