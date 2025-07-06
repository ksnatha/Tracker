package com.tracker.workflow.service;

import com.tracker.workflow.dto.UserDTO;
import com.tracker.workflow.model.UserRoles;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for user-related operations.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private List<UserDTO> userList = new ArrayList<>();

    private Map<String,UserDTO> userMap = new HashMap<>();

    UserDTO getUserById(String userId){
        return userMap.get(userId);
    }
    
    /**
     * Gets users by their role.
     *
     * @param role the role to filter by
     * @return a list of users with the specified role
     */
    List<UserDTO> getUsersByRole(String role){
       return userList.stream().filter(userDTO -> userDTO.getUserRoles().contains(role)).toList();
    }

    
    /**
     * Validates if a user exists.
     *
     * @param userId the ID of the user to validate
     * @return true if the user exists, false otherwise
     */
    boolean validateUser(String userId){
        return userMap.containsKey(userId);
    }

    @PostConstruct
    public void initialize(){
        userList =  List.of(
            UserDTO.builder().userId("U1000").fullName("Alan Belan").userRoles(List.of(UserRoles.INITIATOR.toString())).build(),
            UserDTO.builder().userId("U1001").fullName("Martin Sanchez").userRoles(List.of(UserRoles.SPONSOR.toString())).build(),
            UserDTO.builder().userId("U1002").fullName("Norman Cooper").userRoles(List.of(UserRoles.OWNER.toString())).build(),
            UserDTO.builder().userId("U1003").fullName("Magic Patterson").userRoles(List.of(UserRoles.MANAGER.toString())).build(),
            UserDTO.builder().userId("U1004").fullName("Andrew Ambrose").userRoles(List.of(UserRoles.FINANCE_APPROVER.toString())).build(),
            UserDTO.builder().userId("U1005").fullName("Malcom Marshal").userRoles(List.of(UserRoles.INITIATOR.toString(),UserRoles.SPONSOR.toString())).build(),
            UserDTO.builder().userId("U1006").fullName("John Nottingam").userRoles(List.of(UserRoles.SPONSOR.toString(),UserRoles.OWNER.toString())).build(),
            UserDTO.builder().userId("U1007").fullName("Melaine Lara").userRoles(List.of(UserRoles.MANAGER.toString())).build(),
            UserDTO.builder().userId("U1008").fullName("Graham Lavis").userRoles(List.of(UserRoles.ANALYST.toString())).build(),
            UserDTO.builder().userId("U1009").fullName("Ben Master").userRoles(List.of(UserRoles.OWNER.toString())).build(),
            UserDTO.builder().userId("U1010").fullName("Adam Doe").userRoles(List.of(UserRoles.FINANCE_APPROVER.toString())).build());

        userMap = userList.stream().collect(Collectors.toMap(UserDTO::getUserId, Function.identity()));
    }
}