package com.ufpe.defisio.linfedemapp.dto.Paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDTO {
    private UUID id;
    private String fullName;
    private String birthDate;
    private String address;
    private String phone;
    private String weight;
    private String height;
    private String activityLevel;
    private String maritalStatus;
    private String occupation;
    private String cancerDiagnosisDate;
    private List<String> procedures;
    private List<String> skinChanges;
    private String musculoskeletalComplaints;
    private String lymphedemaSymptoms;
    private String cacifoSign;
    private String orangePeelSign;
    private String stemmerSign;
    private RadiotherapyDTO radiotherapy;
    private SurgeryDTO surgery;
    private AxillaryDissectionDTO axillaryDissection;
    private HormonoterapyDTO hormonoterapy;
    private String detailsHormonoterapy;
    private QuimioterapyDTO quimioterapy;
    private String observation;
}