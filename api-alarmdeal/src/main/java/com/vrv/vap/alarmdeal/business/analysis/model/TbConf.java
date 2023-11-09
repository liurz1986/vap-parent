package com.vrv.vap.alarmdeal.business.analysis.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "tb_conf")
public class TbConf {
     
	@Id
	@Column(name="conf_id")
	private String key;
	@Column(name="conf_value")
	private String value;
}
