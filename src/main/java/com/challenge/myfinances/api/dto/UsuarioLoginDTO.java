package com.challenge.myfinances.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class UsuarioLoginDTO {
	private String email;
    private String senha;
}
