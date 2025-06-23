package notificationservice.dto;

import lombok.Data;

@Data
public class UserEventNotificDto {
    private String operation; // "CREATE" или "DELETE"
    private String email;
}
