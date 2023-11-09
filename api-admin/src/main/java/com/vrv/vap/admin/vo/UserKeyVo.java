package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.User;
import lombok.Data;

@Data
public class UserKeyVo extends User {
    private  String serial;
    private  String publicKey;
}
