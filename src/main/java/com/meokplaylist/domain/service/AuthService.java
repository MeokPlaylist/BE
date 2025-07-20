package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.*;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.infra.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void login(AuthLoginRequest request){

    }

    @Transactional
    public void signUp(AuthSignUpRequest request){

        Users users = Users.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birthDay(request.birthDay())
                .build();

        usersRepository.save(users);

    }

    @Transactional
    public void emailInspect(AuthEmailInspectRequest request){
        if(usersRepository.findByEmail(request.email()).isPresent() ) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    @Transactional
    public Users findPassword(AuthFindPasswordRequest request) throws IllegalAccessException {
        Optional<Users> optionalUser = usersRepository.findByNameAndEmailAndBirthDay(request.name(), request.email(), request.birthDay());
        if(optionalUser.isEmpty()){
            throw new IllegalAccessException("존재하지 않는 회원입니다.");
        }

        return optionalUser.get();
    }

    @Transactional
    public void newPassword(AuthNewPasswordRequest request,Users user){
        user.setPasswordHash(passwordEncoder.encode(request.password()));
    }


}