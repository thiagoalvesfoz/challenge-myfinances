package com.challenge.myfinances.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.myfinances.model.entity.Usuario;
import com.challenge.myfinances.model.exception.BusinessRuleException;
import com.challenge.myfinances.model.exception.ErroAutentificacaoException;
import com.challenge.myfinances.model.repository.UsuarioRepository;
import com.challenge.myfinances.model.service.UsuarioService;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class UsuarioServiceTest {
	
	/*
	 * Considerações iniciais teste
	 * Um mock nunca chama um método original
	 * Um Spy sempre chama um método original
	 * 
	 * Um Spy é um Mock também, mas especializado nos métodos internos
	 * 
	 * Se não especificar o tipo de retorno, os metódos mockados retornam
	 * o valor padrão, para Objetos é null, para primitivos seus respectivos valores default.
	 */
	
	@SpyBean
	UsuarioService service;
	
	@MockBean
	UsuarioRepository repository;
	
	/*
	 * Criando Mocks e Spy sem anotations
	@Before
	public void setUp() {
		this.service = Mockito.spy(UsuarioServiceImpl.class); 
		this.repository = Mockito.mock(UsuarioRepository.class);
	}*/
	
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUsuarioComSucesso() {
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = criarUsuario();
		usuario.setId(1L);
		
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario resultado = service.autenticar(email, senha);
		
		//verificar
		Assertions.assertThat(resultado).isNotNull();
	}
	
	@Test
	public void deveRetornarErroQuandoNaoEncontrarOEmailInformado() {
		//cenario
		Mockito.when( repository.findByEmail(Mockito.anyString()) ).thenReturn( Optional.empty() );
		
		//acao
		Throwable exception = Assertions
				.catchThrowable( () -> service.autenticar("email@email.com", "senha") );
		
		Assertions.assertThat(exception)
			.isInstanceOf(ErroAutentificacaoException.class)
			.hasMessage("Usuário não encontrado!");
	
		
	}
	
	@Test
	public void deveRetornarErroQuandoOUsuarioDigitarASenhaIncorreta() {
	
		//cenario
		Usuario usuario = criarUsuario();
		String senhaIncorreta = "senha-incorreta";
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//acao
		Throwable exception = Assertions
				.catchThrowable( () -> service.autenticar("email@email.com", senhaIncorreta) );
		
		Assertions.assertThat(exception)
			.isInstanceOf(ErroAutentificacaoException.class)
			.hasMessage("Senha inválida!");
	}
	

	
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString()))
		.thenReturn(false);
		
		//ação
		service.validarEmail("email@email.com");	
		
	}
	

	@Test(expected = BusinessRuleException.class)
	public void deveLancarErroAoValidarQuandoExistirEmailCadastrado() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString()))
		.thenReturn(true);
		
		//ação
		service.validarEmail("email@email.com");
	}
	
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = criarUsuario();
		usuario.setId(1L);
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		//ação
		Usuario result = service.cadastrar(new Usuario());
		
		//Verificação
		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getId()).isEqualTo(1L);
		Assertions.assertThat(result.getNome()).isEqualTo("usuario");
		Assertions.assertThat(result.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(result.getSenha()).isEqualTo("senha");
		
	}
	
	@Test(expected = BusinessRuleException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		//cenario
		String email = "email@teste.com";
		
		Usuario usuario = criarUsuario();
		usuario.setEmail(email);
		Mockito.doThrow(BusinessRuleException.class).when(service).validarEmail(email);
		
		//ação
		service.cadastrar(usuario);
		
		//verificao 
		Mockito.verify( repository, Mockito.never() ).save(usuario);
	}
	
	
	public static Usuario criarUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNome("usuario");
		usuario.setEmail("email@email.com");
		usuario.setSenha("senha");
		return usuario;
	}
}
