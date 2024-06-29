package com.br.alura.forum.controller;

import com.br.alura.forum.dto.UsuarioRecordDto;
import com.br.alura.forum.dto.UsuarioResponse;
import com.br.alura.forum.model.Usuario;
import com.br.alura.forum.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Transactional
    public ResponseEntity<Object> registrarUsuario(@RequestBody @Valid UsuarioRecordDto usuarioRecordDto, UriComponentsBuilder uriComponentsBuilder) {
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioRecordDto, usuario);
        if (usuarioService.buscarUsuarioPorEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um usuário com o email informado");
        }
        if (usuarioService.buscarUsuarioPorNome(usuario.getNome()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um usuário com o nome informado");
        }
        usuarioService.salvarUsuario(usuario);
        UsuarioResponse usuarioResponse = new UsuarioResponse(usuario);
        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuarioResponse.getId()).toUri();
        return ResponseEntity.created(uri).body(usuarioResponse);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> buscarUsuarioPorId(@PathVariable Long id) {
        Optional <Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe um usuário com o id informado");
        }
        var usuario = usuarioOptional.get();
        UsuarioResponse usuarioResponse = new UsuarioResponse(usuario);
        return ResponseEntity.ok(usuarioResponse);
    }

    @GetMapping
    @Transactional
    public ResponseEntity<Object> listarUsuarios(@PageableDefault(page = 0, size = 10, sort = "id") Pageable paginacao) {
        Optional <Page<Usuario>> usuariosOptional = Optional.ofNullable(usuarioService.listarUsuarios(paginacao));
        if (usuariosOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existem usuários cadastrados");
        }
        Page<Usuario> usuarios = usuariosOptional.get();
        Page<UsuarioResponse> usuariosResponse = usuarios.map(UsuarioResponse::new);
        return ResponseEntity.ok(usuariosResponse);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> atualizarUsuario(@PathVariable Long id, @RequestBody @Valid UsuarioRecordDto usuarioRecordDto) {
        Optional <Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe um usuário com o id informado");
        }
        var usuario = usuarioOptional.get();
        BeanUtils.copyProperties(usuarioRecordDto, usuario);
        usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok(new UsuarioResponse(usuario));
    }

    @PutMapping("/{id}/ativar")
    @Transactional
    public ResponseEntity<Object> ativarUsuario(@PathVariable Long id) {
        Optional <Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe um usuário com o id informado");
        }
        Usuario usuario = usuarioOptional.get();
        if (usuario.isAtivo()) {
            return ResponseEntity.badRequest().body("O usuário já está ativo");
        }
        usuario.setAtivo(true);
        usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok().body(" O usuário " + id + " foi ativado com sucesso!");
    }

    @PutMapping("/{id}/desativar")
    @Transactional
    public ResponseEntity<Object> desativarUsuario(@PathVariable Long id) {
        Optional <Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe um usuário com o id informado");
        }
        Usuario usuario = usuarioOptional.get();
        if (!usuario.isAtivo()) {
            return ResponseEntity.badRequest().body("O usuário já está desativado");
        }
        usuario.setAtivo(false);
        usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok().body(" O usuário " + id + " foi desativado com sucesso!");
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> excluirUsuario(@PathVariable Long id) {
        Optional <Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe um usuário com o id informado");
        }
        usuarioService.excluirUsuario(id);
        return ResponseEntity.ok().body(" O usuário " + id + " foi excluído com sucesso!");
    }

}
