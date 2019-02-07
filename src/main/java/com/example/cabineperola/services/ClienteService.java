package com.example.cabineperola.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cabineperola.domain.Cidade;
import com.example.cabineperola.domain.Cliente;
import com.example.cabineperola.domain.Endereco;
import com.example.cabineperola.repositories.ClienteRepository;
import com.example.cabineperola.repositories.EnderecoRepository;
import com.example.cabineperola.services.exceptions.ObjectNotFoundException;

import dto.ClienteDTO;
import dto.ClienteNewDTO;


@Service
public class ClienteService {
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	public Cliente find(Integer id) {
		Optional<Cliente> obj = clienteRepository.findById(id);  
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName())); 
	}
	
	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = clienteRepository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}
	
	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return clienteRepository.save(newObj);
	}
	
	public void delete(Integer id) {
		find(id);
		clienteRepository.deleteById(id);
	}
	
	public List<Cliente> findAll(){
		return clienteRepository.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction){
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return clienteRepository.findAll(pageRequest);
	}
	
	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(null, objDto.getCpf(), objDto.getNome(),  objDto.getEmail(), objDto.getSenha());
	}
	
	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getCpf(), objDto.getNome(),  objDto.getEmail(), objDto.getSenha());
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
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}

}
