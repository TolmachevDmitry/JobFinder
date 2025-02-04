package com.tolmic;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class User {

    private Long botId;
    private String phone;
    private String email;
    private boolean isActive;

}
