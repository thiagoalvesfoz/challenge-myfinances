package com.challenge.myfinances.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.myfinances.api.dto.AtualizaStatusDTO;
import com.challenge.myfinances.api.dto.LancamentoDTO;
import com.challenge.myfinances.model.entity.Lancamento;
import com.challenge.myfinances.model.enumeration.StatusLancamento;
import com.challenge.myfinances.model.enumeration.TipoLancamento;
import com.challenge.myfinances.model.exception.BusinessRuleException;
import com.challenge.myfinances.model.service.LancamentoService;
import com.challenge.myfinances.model.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {
	
	private final LancamentoService service;
	private final UsuarioService usuarioService;
	
	@GetMapping("/{id}")
	public ResponseEntity get(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(toModel(service.obterPorId(id)));
		}catch(BusinessRuleException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		
	}

	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "usuario", required = true) Long idUsuario,
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "tipo", required = false) String tipo
			) {
		
		try {
			if(tipo != null && !tipo.equals( TipoLancamento.DESPESA.toString() ) 
					&& !tipo.equals( TipoLancamento.RECEITA.toString() )) {				
				throw new BusinessRuleException("Tipo inválido");
			};
			
			LancamentoDTO filtro = new LancamentoDTO();
			filtro.setUsuario(idUsuario);
			filtro.setDescricao(descricao);
			filtro.setMes(mes);
			filtro.setAno(ano);
			filtro.setTipo(tipo);			

			Lancamento entity = toEntity(filtro);	
			List<Lancamento> lancamentos = service.buscar(entity);
			return ResponseEntity.ok(lancamentos);
			
		} catch(BusinessRuleException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
		try {
			Lancamento entidade = toEntity(dto);
			entidade = service.salvar(entidade);
			return new ResponseEntity<>(entidade, HttpStatus.CREATED);
		}catch(BusinessRuleException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}
	
	
	@PutMapping("/{id}")
	public ResponseEntity atualizar(@PathVariable Long id, @RequestBody LancamentoDTO dto) {
		try {
			service.obterPorId(id);
			Lancamento atualizado = toEntity(dto);
			atualizado.setId(id); 
			return ResponseEntity.ok(service.atualizar(atualizado));	
		}catch(BusinessRuleException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}
	
	@PutMapping("/{id}/atualizar-status")
	public ResponseEntity atualizarStatusLancamento(@PathVariable Long id, 
			@RequestBody AtualizaStatusDTO dto) {
		
		try {
			Lancamento lancamento = service.obterPorId(id);
	
			if(!dto.getStatus().equals(StatusLancamento.EFETIVADO.name()) 
					&& !dto.getStatus().equals(StatusLancamento.CANCELADO.name())
					&& !dto.getStatus().equals(StatusLancamento.PENDENTE.name())) {
				
				throw new BusinessRuleException("Não foi possivel atualizar o status do lançamento,"
						+ " envie um status válido");
			}
			
			StatusLancamento status = StatusLancamento.valueOf(dto.getStatus());
			
			service.atualizarStatus(lancamento, status);
			return ResponseEntity.ok(service.obterPorId(id));
		} 
		catch (BusinessRuleException | NullPointerException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity deletar(@PathVariable Long id) {
		try {
			service.deletar(service.obterPorId(id));
			return ResponseEntity.noContent().build();
		}catch(BusinessRuleException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}
	
	public LancamentoDTO toModel(Lancamento entity) {
		return LancamentoDTO.builder()
				.id(entity.getId())
				.descricao(entity.getDescricao())
				.ano(entity.getAno())
				.mes(entity.getMes())
				.valor(entity.getValor())
				.status(entity.getStatus().name())
				.tipo(entity.getTipo().name())
				.usuario(entity.getUsuario().getId())
				.build();		
	}
	
	public Lancamento toEntity(LancamentoDTO dto) {
	
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		if(dto.getUsuario() != null)
			lancamento.setUsuario(usuarioService.obterPorId(dto.getUsuario()));
		
		if(dto.getTipo() != null)
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		
		if(dto.getStatus() != null)
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		
		return lancamento;
	}
	
}
