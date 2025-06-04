package com.ufpe.defisio.linfedemapp.dto.Paciente;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HormonoterapyDTO {
    //Tipo
    private String type;
    //Duração
    private String duration;
}
