package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    @Email(message = "Incorrect email address")
    @NotNull(message = "Email cannot be empty")
    private String email;
    @NotBlank(message = "Name cannot be empty")
    private String name;
}
