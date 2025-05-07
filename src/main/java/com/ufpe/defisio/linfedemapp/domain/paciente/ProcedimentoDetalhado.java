package com.ufpe.defisio.linfedemapp.domain.paciente;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentoDetalhado {
    private String tipo;
    private String duracao;
}
