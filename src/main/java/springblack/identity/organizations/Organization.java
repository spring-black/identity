package springblack.identity.organizations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import springblack.common.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "organizations")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Organization {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    public Status status = Status.PENDING;

    public String name;
    public String description;

    @CreationTimestamp
    private LocalDateTime stampCreated;

}
