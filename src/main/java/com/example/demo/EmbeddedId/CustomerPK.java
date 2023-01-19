package com.example.demo.EmbeddedId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class CustomerPK implements Serializable {

    private Long unitId;

    private Long versionId;

}
