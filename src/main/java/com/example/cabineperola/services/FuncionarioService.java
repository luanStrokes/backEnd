package com.example.cabineperola.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cabineperola.domain.Cidade;
import com.example.cabineperola.domain.Funcionario;
import com.example.cabineperola.domain.Endereco;
import com.example.cabineperola.repositories.FuncionarioRepository;
import com.example.cabineperola.repositories.EnderecoRepository;
import com.example.cabineperola.services.exceptions.DataIntegrityException;
import com.example.cabineperola.services.exceptions.ObjectNotFoundException;

import dto.FuncionarioDTO;
import dto.FuncionarioNewDTO;


@Service
public class FuncionarioService {
	
	@Autowired
	private FuncionarioRepository funcionarioRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public Funcionario find(Integer id) {
		Optional<Funcionario> obj = funcionarioRepository.findById(id);  
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Funcionario.class.getName())); 
	}
	
	@Transactional
	public Funcionario insert(Funcionario obj) {
		obj.setId(null);
		obj = funcionarioRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Funcionario update(Funcionario obj) {
		Funcionario newObj = find(obj.getId());
		updateDate(newObj, obj);
		return funcionarioRepository.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		try {
			funcionarioRepository.deleteById(id);
		}catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
		
	}
	
	public List<Funcionario> findAll(){
		return funcionarioRepository.findAll();
	}
	
	public Page<Funcionario> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return funcionarioRepository.findAll(pageRequest);
	}
	
	public Funcionario fromDTO(FuncionarioDTO objDto) {
		return new Funcionario(null, objDto.getCpf(), objDto.getNome(),  objDto.getEmail(), objDto.getSenha());
	}
	
	public Funcionario fromDTO(FuncionarioNewDTO objDto) {
		Funcionario cli = new Funcionario(null, objDto.getCpf(), objDto.getNome(),  objDto.getEmail(), objDto.getSenha());
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(), objDto.getBairro(), objDto.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if(objDto.getTelefone2() != null)
			cli.getTelefones().add(objDto.getTelefone2());
		if(objDto.getTelefone3() != null)
			cli.getTelefones().add(objDto.getTelefone3());
		return cli;		
	}
	
	private void updateDate(Funcionario newObj, Funcionario obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

}
