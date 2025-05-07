package com.ufpe.defisio.linfedemapp.repositories;

import com.ufpe.defisio.linfedemapp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
