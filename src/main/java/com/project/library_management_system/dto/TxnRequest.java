package com.project.library_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TxnRequest {

    @NotBlank(message = "User Phone Number should not be blank")
    private String userPhoneNo;

    @NotBlank(message = "Book No. should not be blank")
    private String bookNo;

}
