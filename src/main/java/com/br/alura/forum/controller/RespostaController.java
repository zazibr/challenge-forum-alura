package com.br.alura.forum.controller;

import com.br.alura.forum.dto.RespostaRecordDto;
import com.br.alura.forum.dto.RespostaResponse;
import com.br.alura.forum.model.Resposta;
import com.br.alura.forum.model.StatusTopico;
import com.br.alura.forum.model.Topico;
import com.br.alura.forum.model.Usuario;
import com.br.alura.forum.service.RespostaService;
import com.br.alura.forum.service.TopicoService;
import com.br.alura.forum.service.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/respostas")
public class RespostaController {

    @Autowired
    private RespostaService respostaService;
    @Autowired
    private TopicoService topicoService;
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    @Transactional
    public ResponseEntity<String> responderTopico(@RequestBody @Valid RespostaRecordDto respostaRecordDto){
        Optional<Topico> topicoOptional = Optional.ofNullable(topicoService.buscarTopicoPorId(respostaRecordDto.topico_id()));
        if (topicoOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Tópico não encontrado!");
        }
        Topico topico = topicoOptional.get();
        Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(respostaRecordDto.autor_id());
        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Autor não encontrado!");
        }
        Usuario usuario = usuarioOptional.get();
        if (!usuario.isAtivo()){
            return ResponseEntity.badRequest().body("Usuário não está ativo!");
        }
        if (topico.getStatus().equals(StatusTopico.FECHADO)){
            return ResponseEntity.badRequest().body("Não é possível responder um tópico Fechado!");
        } else if (topico.getStatus().equals(StatusTopico.NAO_RESPONDIDO)){
            topico.setStatus(StatusTopico.NAO_SOLUCIONADO);
            topicoService.salvarTopico(topico);
        }
        Resposta resposta = new Resposta();
        resposta.setMensagem(respostaRecordDto.mensagem());
        resposta.setAutor(usuario);
        resposta.setTopico(topico);
        respostaService.salvarResposta(resposta);
        return ResponseEntity.ok().body("Resposta cadastrada com sucesso!!!");
    }

    @GetMapping("/usuario/{id}")
    @Transactional
    public ResponseEntity<List<RespostaResponse>> listarRespostasPorUsuario(@PathVariable Long id){
        Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);
        if (usuarioOptional.isEmpty()){
            return ResponseEntity.badRequest().body(null);
        }
        Usuario usuario = usuarioOptional.get();
        List<RespostaResponse> respostaResponses = respostaService.listarRespostasPorAutorId(usuario.getId());
        return ResponseEntity.ok().body(respostaResponses);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<String> atualizarResposta(@PathVariable Long id, @RequestBody @Valid RespostaRecordDto respostaRecordDto){
        Optional<Resposta> respostaOptional = respostaService.BuscarRespostaPorId(id);
        if (respostaOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Resposta não encontrada!!!");
        }
        Resposta resposta = respostaOptional.get();
        resposta.setMensagem(respostaRecordDto.mensagem());
        respostaService.salvarResposta(resposta);
        return ResponseEntity.ok().body("Resposta "+ id + " atualizada com sucesso!!!");
    }

    @PutMapping("{id}/solucao")
    @Transactional
    public ResponseEntity<String> marcarRespostaComoSolucao(@PathVariable Long id){
        Optional<Resposta> respostaOptional = respostaService.BuscarRespostaPorId(id);
        if (respostaOptional.isEmpty()){
            return ResponseEntity.badRequest().body("Resposta não encontrada!!!");
        }
        var resposta = respostaOptional.get();
        Topico topico = resposta.getTopico();
        topico.setStatus(StatusTopico.SOLUCIONADO);
        topicoService.salvarTopico(topico);
        resposta.setSolucao(true);
        respostaService.salvarResposta(resposta);
        return ResponseEntity.ok().body("Resposta "+ id + " marcada como solução com sucesso!!!");
    }

    //    @PutMapping
//    @Transactional
//    public ResponseEntity<?> atualizarResposta(@RequestBody @Valid DadosAtualizacaoResposta dados){
//        var resposta = respostaRepository.getReferenceById(dados.resposta_id());
//        var topico = topicoRepository.getReferenceById(resposta.getTopico().getId());
//        if (topico.getStatus().equals(StatusTopico.FECHADO)){
//            return ResponseEntity.badRequest().body("Não é possível atualizar uma resposta de um tópico Fechado!!!");
//        }
//        if (dados.resposta_solucao().equals(true)){
//            topico.setStatus(StatusTopico.SOLUCIONADO);
//            topicoRepository.save(topico);
//        } else {
//            topico.setStatus(StatusTopico.NAO_SOLUCIONADO);
//            topicoRepository.save(topico);
//        }
//        resposta.atualizarInformacoesResposta(dados, topico);
//        return ResponseEntity.ok().body(new DadosDetalhamentoTopico(topico));
//    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> excluirResposta(@PathVariable Long id){
        respostaService.excluirResposta(id);
        return ResponseEntity.ok().body("Resposta "+ id + " excluída com sucesso!!!");
    }
}