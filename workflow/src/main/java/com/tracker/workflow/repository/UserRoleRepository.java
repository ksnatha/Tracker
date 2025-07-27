package com.tracker.workflow.repository;

import com.tracker.workflow.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUserId(String userId);
    
    List<UserRole> findByRoleId(Long roleId);
    
    @Query("SELECT ur.userId FROM UserRole ur WHERE ur.role.roleName = :roleName")
    List<String> findUserIdsByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT ur.role.roleName FROM UserRole ur WHERE ur.userId = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") String userId);
}
