package domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import infra.Users;
import org.springframework.data.jpa.repository.Query;

public interface UsersRepository extends JpaRepository<Users,Long>{

    String findByEmail(String email);
}
