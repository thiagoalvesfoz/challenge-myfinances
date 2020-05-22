package com.challenge.myfinances.model.service.implementation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenge.myfinances.model.entity.Lancamento;
import com.challenge.myfinances.model.enumeration.StatusLancamento;
import com.challenge.myfinances.model.enumeration.TipoLancamento;
import com.challenge.myfinances.model.exception.BusinessRuleException;
import com.challenge.myfinances.model.repository.LancamentoRepository;
import com.challenge.myfinances.model.service.LancamentoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LancamentoServiceImpl implements LancamentoService {
	
	private final LancamentoRepository repository;

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId()); //garante que o lancamento tem id;
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional 
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentofiltro) {
		Example<Lancamento> example = Example.of( lancamentofiltro, 
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withStringMatcher(StringMatcher.CONTAINING));
		return repository.findAll(example);
	}

	@Override
	@Transactional 
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		 lancamento.setStatus(status);
		 atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals(""))
			throw new BusinessRuleException("Informe uma Descricao válida");
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12)
			throw new BusinessRuleException("Informe um Mês válido");
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4)
			throw new BusinessRuleException("Informe um Ano válido");
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null)
			throw new BusinessRuleException("Informe um usuário");
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1)
			throw new BusinessRuleException("Informe um valor Válido");
		
		if(lancamento.getTipo() == null)
			throw new BusinessRuleException("Informe um tipo de lancamento");
	}

	@Override
	public Lancamento obterPorId(Long id) {
		return repository.findById(id)
				.orElseThrow( () -> new BusinessRuleException("Lançamento não encontrado"));

	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
		
		if(receitas == null) receitas = BigDecimal.ZERO;
		
		if(despesas == null) despesas = BigDecimal.ZERO;
		
		return receitas.subtract(despesas);
	}

}
