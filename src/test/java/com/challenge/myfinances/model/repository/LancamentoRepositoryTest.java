package com.challenge.myfinances.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.challenge.myfinances.model.entity.Lancamento;
import com.challenge.myfinances.model.enumeration.StatusLancamento;
import com.challenge.myfinances.model.enumeration.TipoLancamento;

@RunWith(SpringRunner.class)
@DataJpaTest //-> para testes de integração
@AutoConfigureTestDatabase(replace = Replace.NONE) //-> não sobrescreve configurações de testes
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository repository; //-> classe alvo
	
	@Autowired
	TestEntityManager entityManager; //auxilia na criação de cenários
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento lancamento = criarLancamento();
		
		//acão
		lancamento = repository.save(lancamento);
		
		//verificação
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}

	
	@Test
	public void deveDeletarUmLancamento() {
		//centario
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		//açao
		repository.delete(lancamento);
		
		Lancamento inexistente = entityManager.find(Lancamento.class, lancamento.getId());
		
		Assertions.assertThat(inexistente).isNull();
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenario
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		
		lancamento.setMes(4);
		lancamento.setDescricao("Teste de atualização");
		lancamento.setStatus(StatusLancamento.EFETIVADO);
		
		//ação
		repository.save(lancamento);
		
		//verificação
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
	
		Assertions.assertThat(lancamentoAtualizado.getMes()).isEqualByComparingTo(4);
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste de atualização");
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualByComparingTo(StatusLancamento.EFETIVADO);
	}
	

	@Test
	public void deveBuscarLancamentoPorId() {
		//cenario
		Lancamento lancamento = criarLancamento();
		entityManager.persist(lancamento);
		
		//Ação
		Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());
		
		//verificação
		Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
	}
	
	//auxiliar
	public static Lancamento criarLancamento() {
		return Lancamento.builder()
				.ano(2019).mes(1)
				.descricao("Lancamento Teste")
				.valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE)
				.dataCadastro(LocalDate.now())
				.build();
	}
}
