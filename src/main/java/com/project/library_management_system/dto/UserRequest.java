package com.project.library_management_system.dto;

import com.project.library_management_system.model.entity.User;
import com.project.library_management_system.model.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "User Name should not be blank")
    private String userName;

    @NotBlank(message = "Phone Number should not be blank")
    private String phoneNo;

    private String email;

    private String address;

    @NotBlank(message = "user password should not be blank")
    private String password;

    public User toUser() {
        return User.
                builder().
                name(this.userName).
                phoneNo(this.phoneNo).
                email(this.email).
                address(this.address).
                userStatus(UserStatus.ACTIVE).
                build();
    }
}
