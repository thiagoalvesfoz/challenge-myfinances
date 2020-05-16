package com.challenge.myfinances.model.service;

import java.math.BigDecimal;
import java.util.List;

import com.challenge.myfinances.model.entity.Lancamento;
import com.challenge.myfinances.model.enumeration.StatusLancamento;

public interface LancamentoService {
	
	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentofiltro);
	
	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Lancamento obterPorId(Long id);
	
	BigDecimal obterSaldoPorUsuario(Long id);
}
