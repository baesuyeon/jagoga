package com.project.jagoga.user.domain;

import java.util.Optional;

public interface UserRepository {

    public User save(User user);

    public Optional<User> getByEmail(String email);

    public boolean existsByEmail(String email);

    public void deleteAll();
}
