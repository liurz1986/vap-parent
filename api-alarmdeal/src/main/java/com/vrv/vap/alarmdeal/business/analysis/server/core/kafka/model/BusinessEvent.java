package com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonRawValue;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * mq对应的实体
 * @author wd-pc
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class BusinessEvent implements Serializable {
	
    private static final long serialVersionUID = 2840081172778887899L;

    @NotNull
    @NotBlank
    private String businessType;

    @NotNull
    @NotBlank
    @JsonRawValue
    private String payload;

    @NotNull
    @NotBlank
    private String guid;

	@Override
	public String toString() {
		return "BusinessEvent [businessType=" + businessType + ", payload=" + payload + ", guid=" + guid + "]";
	}

    
}