package springblack.identity.organizations.links;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Range;
import springblack.identity.organizations.Organization;

import javax.persistence.*;

@Data
@Entity
@Table(name = "organization_links")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrganizationLink {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Range(max = 4294967295L)
    @Column(nullable = false, updatable = false, unique = true)
    public Long id;

    @OneToOne
    private Organization parent;

    @OneToOne
    private Organization child;

}
