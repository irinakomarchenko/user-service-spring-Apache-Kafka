package notificationservice.dto;

import lombok.Data;

@Data
public class UserEventNotificDto {
    private String operation;
    private String email;
}
