package com.challenge.myfinances.model.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.challenge.myfinances.model.entity.Lancamento;
import com.challenge.myfinances.model.entity.Usuario;
import com.challenge.myfinances.model.enumeration.StatusLancamento;
import com.challenge.myfinances.model.enumeration.TipoLancamento;
import com.challenge.myfinances.model.exception.BusinessRuleException;
import com.challenge.myfinances.model.repository.LancamentoRepository;
import com.challenge.myfinances.model.repository.LancamentoRepositoryTest;
import com.challenge.myfinances.model.service.implementation.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento salvarLancamento = LancamentoRepositoryTest.criarLancamento();
		
		//Spy -> mockando metodos internos de service
		Mockito.doNothing().when(service).validar(salvarLancamento);
		
		//Mock -> simulando salvamento de um lancamento pelo repository
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(salvarLancamento)).thenReturn(lancamentoSalvo);
		
		//ação
		Lancamento lancamento = service.salvar(salvarLancamento);
		
		//verificação
		Assertions.assertThat(lancamento.getId()).isEqualByComparingTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualByComparingTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarLancamentoQuandoHouverErroDeValidacao() {
		//cenário
		Lancamento salvarLancamento = LancamentoRepositoryTest.criarLancamento();
		
		//Spy -> mockando metodos internos de service
		Mockito.doThrow(BusinessRuleException.class).when(service).validar(salvarLancamento);
		
		//ação e verificação
		Assertions.catchThrowableOfType( 
				() -> service.salvar(salvarLancamento), BusinessRuleException.class);
		
		Mockito.verify(repository, Mockito.never()).save(salvarLancamento);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		//Mock -> simulando salvamento de um lancamento pelo repository
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
				
		//Spy -> mockando metodos internos de service
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//ação
		service.salvar(lancamentoSalvo);
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execução e verificaçao
		Assertions.catchThrowableOfType( 
				() -> service.atualizar(lancamento), NullPointerException.class);
		
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		//execução
		service.deletar(lancamento);
		
		//verificação
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void naoLancarErroAoTentarDeletarLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execução
		Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
		
		//verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any( Example.class ) ) ).thenReturn(lista);
		
		//execução
		List<Lancamento> resultado = service.buscar(lancamento);
		
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//Execução
		service.atualizarStatus(lancamento, novoStatus);
		
		
		//verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
		
	}
	
	@Test(expected = Test.None.class)
	public void deveObterUmLancamentoPorId() {
		//cenário
		Long id = 1L;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//Verificação
		Lancamento resultado = service.obterPorId(id);
		
		Assertions.assertThat(resultado).isNotNull();
		
	}
	
	@Test(expected = BusinessRuleException.class)
	public void deveRetornarErroQuandoLancamentoPorIdNaoExistir() {
		//cenário
		Long id = 1L;
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//Ação
		Lancamento resultado = service.obterPorId(id);
		
		//Verificação
		Assertions.assertThat(resultado).isNull();
	}
	
	@Test
	public void deveLancarErrosAoValidarLancamento() {
		//cenário -> Lançamento com descrição nula
		Lancamento lancamento = new Lancamento();
		//Ação
		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe uma Descricao válida");
		
		//cenário 1.1 -> Lançamento com descrição vazia
		lancamento.setDescricao("");
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe uma Descricao válida");
		
		
		
		//cenário 2 Lançamento com mês nulo
		lancamento.setDescricao("Salário");
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um Mês válido");
		
		//cenário 2.1 -> Lançamento com mês menor que 1
		lancamento.setMes(-1);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um Mês válido");
		
		//cenário 2.2 -> Lançamento com mês maior que 12
		lancamento.setMes(13);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um Mês válido");
		
		//cenário 3 -> Lançamento com o Ano nulo
		lancamento.setMes(1);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um Ano válido");
		
		//cenário 3.1 -> Lançamento com o Ano menor que 4 digitos
		lancamento.setAno(999);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um Ano válido");
		
		//cenário 4 -> Lançamento com Usuario nulo
		lancamento.setAno(2020);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um usuário");
		
		//cenário 4 -> Lançamento com Usuario de Id nulo
		lancamento.setUsuario(new Usuario());
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um usuário");
		
		//cenário 5 -> Lançamento com valor nulo
		lancamento.getUsuario().setId(1L);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um valor Válido");
		
		//cenário 5.1 -> Lançamento com valor zero
		lancamento.setValor(BigDecimal.ZERO);
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um valor Válido");
		
		//cenário 6 -> Lancamento com Tipo nulo
		lancamento.setValor(BigDecimal.valueOf(200L));
		//Ação
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		//Verificação
		Assertions.assertThat(erro)
		.isInstanceOf(BusinessRuleException.class)
		.hasMessage("Informe um tipo de lancamento");
	}
	
}
