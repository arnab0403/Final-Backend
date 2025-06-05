package com.major.k1.resturant.Repository;

import com.major.k1.resturant.Entites.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu,Long>{
    Optional<Menu> findByid(Long id);
}
