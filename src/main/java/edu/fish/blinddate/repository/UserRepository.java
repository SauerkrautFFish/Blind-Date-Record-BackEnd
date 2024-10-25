package edu.fish.blinddate.repository;

import edu.fish.blinddate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where u.id in ?1")
    List<User> findByIdIn(List<Integer> userId);
}
