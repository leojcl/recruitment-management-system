package com.leojcl.recruitmentsystem.repository;

import com.leojcl.recruitmentsystem.entity.UsersType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersTypeRepository extends JpaRepository<UsersType, Integer> {

}
