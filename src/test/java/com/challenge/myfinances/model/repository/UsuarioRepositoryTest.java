package com.challenge.myfinances.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.myfinances.model.entity.Usuario;


@RunWith(SpringRunner.class) //JUNIT 4
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) //impede desconfigurar application-dev.properties.
public class UsuarioRepositoryTest {

    /*
     * Testes de integração serve para testar os recursos externos
     * da aplicação, como o acesso ao banco de dados
     *
     * Teste unitário serve para testar os recurso internos da aplicação.
     * Regras do teste Unitário
     * - Cenario
     * - Ação/Execução
     * - Verificação
     */
	
	//Nota de estudo, esse teste funcionado com a versão JUNIT 4, talves
	//Há um problema de compatibilidade com a versão 5 no eclipse

    @Autowired
    UsuarioRepository repository;
    
    @Autowired
    TestEntityManager entityManager;

    @Test //pacote diferente para o Junit4
    public void deveVerificarAExistenciaDeUmEmail() {
        //Cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        boolean result = repository.existsByEmail("usuario@email.com");

        Assertions.assertThat(result).isTrue();

    }
    
    @Test
    public void retornaFalsoSeOEmailNaoExistirNoBancoDeDados() {
   	
    	boolean result = repository.existsByEmail("usuario@email.com");
    	Assertions.assertThat(result).isFalse();
    }
    
    
    @Test
    public void devePersistirUmUsuarioNaBaseDeDados() {
    	Usuario usuario = criarUsuario();
    	
    	Usuario usuarioSalvo = repository.save(usuario);
    	
    	Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
    }
    
    @Test
    public void deveBuscarUmUsuarioPorEmail() {
    	Usuario usuario = criarUsuario();
    	entityManager.persist(usuario);
    	
    	Optional<Usuario> result = repository.findByEmail("usuario@email.com");
    	
    	Assertions.assertThat( result.isPresent() ).isTrue();
    }
    
    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNãoExisteNaBase() {
    	
    	Optional<Usuario> result = repository.findByEmail("usuario@email.com");
    	
    	Assertions.assertThat( result.isEmpty() ).isTrue();
    }
    
    
    public static Usuario criarUsuario() {
    	Usuario usuario = new Usuario();
        usuario.setNome("Usuario");
        usuario.setEmail("usuario@email.com");
        return usuario;
    }
}