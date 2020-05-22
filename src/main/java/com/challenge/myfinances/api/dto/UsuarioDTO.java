package com.challenge.myfinances.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
	
	private String nome;

    private String email;

    private String senha;
}
