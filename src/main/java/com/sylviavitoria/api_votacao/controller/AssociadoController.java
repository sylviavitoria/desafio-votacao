package com.sylviavitoria.api_votacao.controller;

import com.sylviavitoria.api_votacao.dto.AssociadoListarResponse;
import com.sylviavitoria.api_votacao.dto.AssociadoRequest;
import com.sylviavitoria.api_votacao.dto.AssociadoResponse;
import com.sylviavitoria.api_votacao.interfaces.IAssociado;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/associados")
@RequiredArgsConstructor
public class AssociadoController {

    private final IAssociado IAssociado;

    @PostMapping
    public ResponseEntity<AssociadoResponse> criar(@RequestBody @Valid AssociadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IAssociado.criar(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(IAssociado.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<AssociadoListarResponse>> listarTodos(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(IAssociado.listarTodos(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssociadoResponse> atualizar(@PathVariable Long id, @RequestBody @Valid AssociadoRequest request) {
        return ResponseEntity.ok(IAssociado.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        IAssociado.deletar(id);
        return ResponseEntity.noContent().build();
    }

}