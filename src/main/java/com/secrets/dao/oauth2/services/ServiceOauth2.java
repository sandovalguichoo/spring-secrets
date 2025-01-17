package com.secrets.dao.oauth2.services;



import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.secrets.dao.modelo.repositories.IUsuariosCrudRepository;
import com.secrets.dao.oauth2.services.entitys.EntityUsuario;


@Service(value = "serviceOauth2Usuarios")
public class ServiceOauth2 implements UserDetailsService {

	private Logger logger= LoggerFactory.getLogger(UserDetailsService.class);
	
	//-- Inyeccion de servicios
	@Autowired
	private IUsuariosCrudRepository serviceCrudUsuarios;
	
	
	
	//===================== Metodo implementado por la classe:  "UserDetailsService - AUTH2" =================================

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		EntityUsuario entityUsuario= this.serviceCrudUsuarios.buscarUsuarioByUsername(username);
		
		//-- Validar que exista el usuario
		if (entityUsuario==null) {
			this.logger.error("El usuario no existe");
			throw new UsernameNotFoundException("El usuario no existe");
		}
		
		//-- Obtenemos los roles y los transformamos en un GrantedAuthority para poder añadirlo al objeto "UserDetails" que debemos devolver
		List<GrantedAuthority> authorities= entityUsuario.getRoles()
				.stream()
				.map(rol-> new SimpleGrantedAuthority(rol.getNombre()))
				.collect(Collectors.toList());
		
		//-- Devolvemos nuestro usuario
		return new User(entityUsuario.getUsername(), entityUsuario.getPassword(),entityUsuario.getEnabled(), true, true, true, authorities);
	}
	

}
