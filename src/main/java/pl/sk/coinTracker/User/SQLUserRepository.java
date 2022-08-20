package pl.sk.coinTracker.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLUserRepository extends UserRepository, JpaRepository<User, Long> {
}
