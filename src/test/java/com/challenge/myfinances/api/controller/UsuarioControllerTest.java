package com.challenge.myfinances.api.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.challenge.myfinances.api.dto.UsuarioDTO;
import com.challenge.myfinances.model.entity.Usuario;
import com.challenge.myfinances.model.exception.BusinessRuleException;
import com.challenge.myfinances.model.exception.ErroAutentificacaoException;
import com.challenge.myfinances.model.service.LancamentoService;
import com.challenge.myfinances.model.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class) // -> sube o contexto rest para testar o controller
@AutoConfigureMockMvc 
public class UsuarioControllerTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Autowired
	MockMvc mvc; 
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		//cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
										.post(API.concat("/autenticar"))
										.accept(JSON)
										.contentType(JSON)
										.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoAutenticarUmUsuario() throws Exception {
		//cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		//simulando o lancamento de exception
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutentificacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
										.post(API.concat("/autenticar"))
										.accept(JSON)
										.contentType(JSON)
										.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		//cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
		
		Mockito.when(service.cadastrar(Mockito.any(Usuario.class))).thenReturn(usuario);
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
										.post(API)
										.accept(JSON)
										.contentType(JSON)
										.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveRetornarBadRequestAoCadadastrarUmNovoUsuario() throws Exception {
		//cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//simulando o lancamento de exception
		Mockito.when(service.cadastrar(Mockito.any(Usuario.class)))
		.thenThrow(BusinessRuleException.class);
		
		
		//execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
										.post(API)
										.accept(JSON)
										.contentType(JSON)
										.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
}
