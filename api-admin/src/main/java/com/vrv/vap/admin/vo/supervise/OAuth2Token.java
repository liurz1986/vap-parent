package com.vrv.vap.admin.vo.supervise;

import lombok.Data;

@Data
public class OAuth2Token {
	String refreshToken;
	String accessToken;
}
