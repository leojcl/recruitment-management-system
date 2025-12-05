package com.leojcl.recruitmentsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users_type")
public class UsersType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userTypeId;

    private String userTypeName;

    @OneToMany(targetEntity = Users.class, mappedBy = "userType", cascade = CascadeType.ALL)
    private List<Users> users;

    public UsersType() {
    }

    public UsersType(int userTypeId, String userTypeName, List<Users> users) {
        this.userTypeId = userTypeId;
        this.userTypeName = userTypeName;
        this.users = users;
    }

    // don't add List<Users>, because infinite recursion
    @Override
    public String toString() {
        return "UsersType{" +
                "userTypeId=" + userTypeId +
                ", userTypeName='" + userTypeName + '\'' +
                '}';
    }
}
