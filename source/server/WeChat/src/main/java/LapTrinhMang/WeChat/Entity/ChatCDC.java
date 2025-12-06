package LapTrinhMang.WeChat.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_cdc")
public class ChatCDC {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    //Ví dụ:"broadcast";"publish";"send_message"....
    @Column(nullable = false,length = 50)
    private String method = "broadcast";

    @Column(columnDefinition = "json",nullable = false)
    private String payload;

    //Dùng để partition theo roomId,userId,...
    @Column(name = "partition_key",nullable = false)
    private String partitionKey;

    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist(){
        if(createdAt == null){
            createdAt = LocalDateTime.now();
        }
    }
}
