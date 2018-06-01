package com.unloadbrain.blog.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Tag extends BaseEntity {

    @Column(unique = true)
    @Length(min = 1, max = 255)
    private String name;

    @Column(unique = true)
    @Length(min = 1, max = 255)
    private String slug;

}
